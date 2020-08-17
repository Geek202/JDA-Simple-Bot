package me.geek.tom.jdabots.internal.commands;

import com.jagrosh.jdautilities.menu.Paginator;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.geek.tom.jdabots.api.command.util.DiscordContext;
import me.geek.tom.jdabots.api.extension.IExtension;
import me.geek.tom.jdabots.api.startup.BotLoader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static me.geek.tom.jdabots.api.command.util.Helper.literal;

public class CommandHandler {

    private final CommandDispatcher<DiscordContext> dispatcher = new CommandDispatcher<>();

    private final String botPrefix;

    public CommandHandler() {
        this(".");
    }

    public CommandHandler(String botPrefix) {
        this.botPrefix = botPrefix;
    }

    public void init(BotLoader loader) {
        dispatcher.register(literal("extensions").executes(ctx -> {
            StringBuilder builder = new StringBuilder().append("```\nAvailable extensions:\n\n");
            builder.append(loader.getExtensions().stream().map(ext -> ext.getName() + ": " + ext.getDescription()).collect(Collectors.joining("\n")));
            ctx.getSource().getChannel().sendMessage(builder.append("```")).queue();

            return 0;
        }));
        dispatcher.register(literal("commands").executes(ctx -> {
            StringBuilder builder = new StringBuilder().append("```\nAvailable commands:\n");
            Map<IExtension, List<CommandWrapper>> commands = loader.getCommands();
            for (IExtension ext : commands.keySet()) {
                builder.append(ext.getName()).append(": ").append(ext.getDescription()).append("\n");
                List<CommandWrapper> commandList = commands.get(ext);
                for (CommandWrapper cmd : commandList) {
                    builder.append("\t").append(botPrefix).append(cmd.getName()).append(": ").append(cmd.getDescription()).append("\n");
                }
            }

            ctx.getSource().getChannel().sendMessage(builder.append("```")).queue();

            showCommandsUsage(ctx);

            return 0;
        }).then(literal("usage").executes(ctx -> {
            showCommandsUsage(ctx);
            return 0;
        })));

        for (Map.Entry<IExtension, List<CommandWrapper>> entry : loader.getCommands().entrySet()) {
            entry.getValue().forEach(cmd -> {
                LiteralArgumentBuilder<DiscordContext> argumentBuilder = literal(cmd.getName());
                cmd.getCommand().registerCommand(argumentBuilder);
                dispatcher.register(argumentBuilder);
            });
        }
    }

    private void showCommandsUsage(CommandContext<DiscordContext> ctx) {
        Paginator.Builder builder1 = new Paginator.Builder()
                .setColor(Color.BLUE)
                .setUsers(ctx.getSource().getAuthor())
                .useNumberedItems(false)
                .allowTextInput(false)
                .waitOnSinglePage(false)
                .setColumns(1)
                .setTimeout(60, TimeUnit.SECONDS)
                .showPageNumbers(true)
                .setItemsPerPage(15)
                .setEventWaiter(BotLoader.EVENT_WAITER)
                .setText("All available commands:")
                .setFinalAction(m -> {
                    try {
                        m.clearReactions().queue();
                    } catch (PermissionException e) {
                        m.delete().queue();
                    }
                });

        Arrays.stream(dispatcher.getAllUsage(dispatcher.getRoot(), ctx.getSource(), true))
                .map(s -> "`"+botPrefix+s+"`")
                .forEach(builder1::addItems);
        builder1.build().display(ctx.getSource().getChannel());
    }

    public void handleCommand(GuildMessageReceivedEvent event) {
        if (!event.getChannel().canTalk()) return;

        String msg = event.getMessage().getContentRaw();
        if (!msg.startsWith(botPrefix)) return;

        try {
            dispatcher.execute(msg.substring(botPrefix.length()), new DiscordContext(event.getMessage(), event.getAuthor(), event.getChannel()));
        } catch (CommandSyntaxException e) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("`"+e.getMessage()+"`")
                    .setColor(Color.RED)
            .build()).queue();
        }
    }
}
