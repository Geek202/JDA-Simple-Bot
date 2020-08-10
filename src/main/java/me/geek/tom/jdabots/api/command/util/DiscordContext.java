package me.geek.tom.jdabots.api.command.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

/**
 * Represents the context in which a command was executed.
 */
public class DiscordContext {

    private final Message message;
    private final User author;
    private final TextChannel channel;

    public DiscordContext(Message message, User author, TextChannel channel) {
        this.message = message;
        this.author = author;
        this.channel = channel;
    }

    /**
     * @return The message that triggered this command execution.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * @return The {@link User} who sent this message
     */
    public User getAuthor() {
        return author;
    }

    /**
     * @return The {@link TextChannel} that the message was sent in
     */
    public TextChannel getChannel() {
        return channel;
    }

    /**
     * Wrapper for {@link TextChannel#sendMessage(CharSequence)}
     *
     * @param message The message to send
     * @return A {@link RestAction<Message>} to queue the message.
     */
    public RestAction<Message> reply(CharSequence message) {
        return getChannel().sendMessage(message);
    }

    /**
     * Wrapper for {@link TextChannel#sendMessage(MessageEmbed)}
     *
     * @param embed The message to send
     * @return A {@link RestAction<Message>} to queue the message.
     */
    public RestAction<Message> reply(MessageEmbed embed) {
        return getChannel().sendMessage(embed);
    }

    /**
     * Wrapper for {@link TextChannel#sendMessage(MessageEmbed)} that calls {@link EmbedBuilder#build()} before sending.
     *
     * @param builder The message to send
     * @return A {@link RestAction<Message>} to queue the message.
     */
    public RestAction<Message> reply(EmbedBuilder builder) {
        return getChannel().sendMessage(builder.build());
    }

    /**
     * Wrapper for {@link TextChannel#sendMessage(Message)}
     *
     * @param message The message to send
     * @return A {@link RestAction<Message>} to queue the message.
     */
    public RestAction<Message> reply(Message message) {
        return getChannel().sendMessage(message);
    }
}
