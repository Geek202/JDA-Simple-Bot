package me.geek.tom.jdabots.api.extension;

/**
 * An Extension is the main main component of the bot and is used to load commands and event listeners.
 * <br><br>
 * They are loaded using a service provider and thus a file must be present in your JAR called
 * {@code META-INF/services/me.geek.tom.jdabots.api.extension} containing the fully qualified names
 * of all extensions in the JAR, each on their own line.
 * <br><br>
 * If an {@link IExtension} also implements {@link me.geek.tom.jdabots.api.event.EventListener EventListener} then
 * it will be auto-subscribed to the bots {@link me.geek.tom.jdabots.api.event.EventBus EventBus} and can
 * receive any {@link net.dv8tion.jda.api.events.Event events} from the bot.
 */
public interface IExtension {
    /**
     * @return The unique name of this extension
     */
    String getName();

    /**
     * @return A human readable description of this extension.
     */
    String getDescription();

    /**
     * @return The package name containing this extension's {@link me.geek.tom.jdabots.api.command.Command commands}
     */
    String getCommandsPackage();
}
