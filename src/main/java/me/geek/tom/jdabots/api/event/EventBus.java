package me.geek.tom.jdabots.api.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EventBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);
    private final Map<EventListener, List<HandlerWrapper>> handlers = new ConcurrentHashMap<>();

    public void register(EventListener listener) {
        List<HandlerWrapper> handlers = Arrays.stream(listener.getClass().getDeclaredMethods())
                .filter(method -> !Modifier.isStatic(method.getModifiers()))
                .filter(method -> method.isAnnotationPresent(Handler.class))
                .filter(method -> method.getReturnType().getName().equals("void"))
                .filter(method -> method.getParameterTypes().length == 1)
                .map(HandlerWrapper::new)
                .collect(Collectors.toList());
        this.handlers.put(listener, handlers);
    }

    public void unregister(EventListener listener) {
        handlers.remove(listener);
    }

    public void post(Object event) {
        for (Map.Entry<EventListener, List<HandlerWrapper>> entry : handlers.entrySet()) {
            for (HandlerWrapper handler : entry.getValue()) {
                if (handler.method.getParameterTypes()[0].isAssignableFrom(event.getClass())) {
                    try {
                        handler.method.invoke(entry.getKey(), event);
                    } catch (Exception e) {
                        LOGGER.error(String.format("Event handler %s#%s(%s) failed with exception:", entry.getKey().getClass().getName(), handler.method.getName(), handler.method.getParameterTypes()[0].getSimpleName()), e);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static class HandlerWrapper {
        private final Method method;

        private HandlerWrapper(Method method) {
            this.method = method;
        }
    }
}
