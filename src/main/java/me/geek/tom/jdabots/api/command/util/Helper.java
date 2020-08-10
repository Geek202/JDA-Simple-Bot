package me.geek.tom.jdabots.api.command.util;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

public class Helper {

    public static LiteralArgumentBuilder<DiscordContext> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <T extends ArgumentType<T>> RequiredArgumentBuilder<DiscordContext, T> argument(String name, T type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

}
