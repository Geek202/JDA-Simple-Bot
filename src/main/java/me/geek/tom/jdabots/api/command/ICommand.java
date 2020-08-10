package me.geek.tom.jdabots.api.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.geek.tom.jdabots.api.command.util.DiscordContext;

public interface ICommand {
    void registerCommand(LiteralArgumentBuilder<DiscordContext> node);
}
