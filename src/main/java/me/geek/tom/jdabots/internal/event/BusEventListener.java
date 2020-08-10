package me.geek.tom.jdabots.internal.event;

import me.geek.tom.jdabots.api.event.EventBus;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class BusEventListener implements EventListener {

    private final EventBus bus;

    public BusEventListener(EventBus bus) {
        this.bus = bus;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        this.bus.post(event);
    }
}
