package me.geek.tom.jdabots.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b>All classes annotated with this MUST implement {@link ICommand} or the bot will fail loading!</b>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * @return The name of the command.
     */
    String name();

    /**
     * @return A human-readable description of the command that will be shown in help messages
     */
    String description();

}
