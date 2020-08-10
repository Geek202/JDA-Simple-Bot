package com.example.bot;

import me.geek.tom.jdabots.api.event.EventListener;
import me.geek.tom.jdabots.api.event.Handler;
import me.geek.tom.jdabots.api.extension.IExtension;
import net.dv8tion.jda.api.events.ReadyEvent;

public class ExampleExtension implements IExtension, EventListener {
    @Override
    public String getName() {
        return "Example";
    }

    @Override
    public String getDescription() {
        return "An example bot extension!";
    }

    @Override
    public String getCommandsPackage() {
        return "com.example.bot";
    }

    @Handler
    public void onReadyEvent(ReadyEvent event) {
        System.out.println("ExampleExtension ready on " + event.getGuildTotalCount() + " guilds" +
                (event.getGuildUnavailableCount() == 0 ? "" : " (" + event.getGuildUnavailableCount() + " unavailable)"));
    }
}
