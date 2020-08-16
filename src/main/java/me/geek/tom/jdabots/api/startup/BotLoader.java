package me.geek.tom.jdabots.api.startup;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
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
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BotLoader extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotLoader.class);

    private final List<IExtension> extensions;
    private final Map<IExtension, List<CommandWrapper>> commands;
    private final CommandHandler handler = new CommandHandler();

    private final EventBus eventBus = new EventBus();

    public static final EventWaiter EVENT_WAITER = new EventWaiter();

    public BotLoader(List<IExtension> extensions, Map<IExtension, List<CommandWrapper>> commands) {
        this.extensions = extensions;
        this.commands = commands;
        autoSubscribeEventListeners();
    }

    private void autoSubscribeEventListeners() {
        for (IExtension extension : extensions) {
            if (EventListener.class.isAssignableFrom(extension.getClass())) {
                LOGGER.info("Registering {} to the EventBus...", extension.getName());
                eventBus.register((EventListener) extension);
            }
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        handler.init(this);
        LOGGER.info("Ready on {} guilds!", event.getGuildAvailableCount());
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
        main(args, Collections.emptyList());
    }

    @SuppressWarnings("unused")
    public static void main(String[] args, List<GatewayIntent> disabledIntents, GatewayIntent... intents) throws LoginException {
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
            LOGGER.info(ext.getName() + ": " + ext.getDescription());
            for (CommandWrapper wrapper : allCommands.get(ext)) {
                LOGGER.info("\t" + wrapper.getName() + ": " + wrapper.getDescription() + " (" + wrapper.getCommand().getClass().getSimpleName() + ")");
            }
        }

        BotLoader loader = new BotLoader(extensions, allCommands);
        JDA builder = JDABuilder.createDefault(arguments.token)
                .addEventListeners(loader, loader.getListener(), EVENT_WAITER)
                .disableIntents(disabledIntents)
                .enableIntents(Arrays.asList(intents))
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
