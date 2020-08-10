package com.example.bot;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.geek.tom.jdabots.api.command.Command;
import me.geek.tom.jdabots.api.command.util.DiscordContext;
import me.geek.tom.jdabots.api.command.ICommand;

@Command(name = "pin", description = "Pon! - Get the REST and WebSocket pings")
public class PinCommand implements ICommand {
    @Override
    public void registerCommand(LiteralArgumentBuilder<DiscordContext> node) {
        node.executes(ctx -> {
            ctx.getSource().getChannel().sendMessage("Pon" +
                    "\nWS Ping:" + ctx.getSource().getChannel().getJDA().getGatewayPing() + " ms\n" +
                    "REST Ping: " + ctx.getSource().getChannel().getJDA().getRestPing().submit().join() + " ms").queue();
            return 0;
        });
    }
}
