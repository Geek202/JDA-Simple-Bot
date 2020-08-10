package me.geek.tom.jdabots.api.startup;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import me.geek.tom.jdabots.api.event.EventBus;
import me.geek.tom.jdabots.api.event.EventListener;
import me.geek.tom.jdabots.api.extension.IExtension;
import me.geek.tom.jdabots.internal.commands.CommandHandler;
import me.geek.tom.jdabots.internal.commands.CommandWrapper;
import me.geek.tom.jdabots.internal.event.BusEventListener;
import me.geek.tom.jdabots.internal.extensions.ExtensionLoader;
import me.geek.tom.jdabots.internal.extensions.ExtensionScanner;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BotLoader extends ListenerAdapter {

    private final List<IExtension> extensions;
    private final Map<IExtension, List<CommandWrapper>> commands;
    private final CommandHandler handler = new CommandHandler();

    private final EventBus eventBus = new EventBus();

    public BotLoader(List<IExtension> extensions, Map<IExtension, List<CommandWrapper>> commands) {
        this.extensions = extensions;
        this.commands = commands;
        autoSubscribeEventListeners();
    }

    private void autoSubscribeEventListeners() {
        for (IExtension extension : extensions) {
            if (EventListener.class.isAssignableFrom(extension.getClass())) {
                System.out.printf("Registering %s to the EventBus...%n", extension.getName());
                eventBus.register((EventListener) extension);
            }
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        handler.init(this);
        System.out.println("Ready on " + event.getGuildAvailableCount() + " guilds!");
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        handler.handleCommand(event);
    }

    public List<IExtension> getExtensions() {
        return extensions;
    }

    public Map<IExtension, List<CommandWrapper>> getCommands() {
        return commands;
    }

    public static void main(String[] args) throws LoginException {
        Args arguments = new Args();
        try {
            JCommander.newBuilder()
                    .addObject(arguments)
                    .build()
                    .parse(args);
        } catch (ParameterException e) {
            e.usage();
        }

        ExtensionScanner scanner = new ExtensionScanner();
        List<IExtension> extensions = scanner.scan();

        Map<IExtension, List<CommandWrapper>> allCommands = extensions.stream()
                .collect(Collectors.toMap(ext -> ext, ExtensionLoader::readCommands));

        for (IExtension ext : allCommands.keySet()) {
            System.out.println(ext.getName() + ": " + ext.getDescription());
            for (CommandWrapper wrapper : allCommands.get(ext)) {
                System.out.println("\t" + wrapper.getName() + ": " + wrapper.getDescription() + " (" + wrapper.getCommand().getClass().getSimpleName() + ")");
            }
        }

        BotLoader loader = new BotLoader(extensions, allCommands);
        JDA builder = JDABuilder.createDefault(arguments.token)
                .addEventListeners(loader, loader.getListener())
                .build();
    }

    private net.dv8tion.jda.api.hooks.EventListener getListener() {
        return new BusEventListener(this.eventBus);
    }

    @SuppressWarnings("unused")
    private static class Args {
        @Parameter(names = { "-token" }, description = "The bot token to use to login", required = true)
        private String token;
    }
}
