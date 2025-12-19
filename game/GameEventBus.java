package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface GameEventBus {
    void publish(Object event);

    <T> void subscribe(Class<T> eventType, Consumer<T> handler);

    class SimpleGameEventBus implements GameEventBus {
        private final Map<Class<?>, List<Consumer<?>>> subscribers = new HashMap<>();

        @Override
        public void publish(Object event) {
            Class<?> type = event.getClass();
            if (subscribers.containsKey(type)) {
                for (Consumer<?> handler : subscribers.get(type)) {
                    // Unchecked cast is safe here because we enforce type safety in subscribe
                    ((Consumer<Object>) handler).accept(event);
                }
            }
        }

        @Override
        public <T> void subscribe(Class<T> eventType, Consumer<T> handler) {
            subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
        }
    }
}
