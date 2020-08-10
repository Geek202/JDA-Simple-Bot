package me.geek.tom.jdabots.internal.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.geek.tom.jdabots.api.command.util.DiscordContext;
import me.geek.tom.jdabots.api.extension.IExtension;
import me.geek.tom.jdabots.api.startup.BotLoader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.geek.tom.jdabots.api.command.util.Helper.literal;

public class CommandHandler {

    private final CommandDispatcher<DiscordContext> dispatcher = new CommandDispatcher<>();

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
                List<CommandWrapper> cmds = commands.get(ext);
                for (CommandWrapper cmd : cmds) {
                    builder.append("\t.").append(cmd.getName()).append(": ").append(cmd.getDescription()).append("\n");
                }
            }
            builder.append("```\n```All command usage:\n");
            builder.append(String.join("\n", dispatcher.getAllUsage(dispatcher.getRoot(), ctx.getSource(), true)));


            ctx.getSource().getChannel().sendMessage(builder.append("```")).queue();

            return 0;
        }).then(literal("usage").executes(ctx -> {
            StringBuilder builder = new StringBuilder();
            builder.append("```All command usage:\n");
            builder.append(String.join("\n", dispatcher.getAllUsage(dispatcher.getRoot(), ctx.getSource(), true)));

            ctx.getSource().getChannel().sendMessage(builder.append("```")).queue();

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

    public void handleCommand(GuildMessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();
        if (!msg.startsWith(".")) return;

        try {
            dispatcher.execute(msg.substring(1), new DiscordContext(event.getMessage(), event.getAuthor(), event.getChannel()));
        } catch (CommandSyntaxException e) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("`"+e.getMessage()+"`")
                    .setColor(Color.RED)
            .build()).queue();
        }
    }
}
