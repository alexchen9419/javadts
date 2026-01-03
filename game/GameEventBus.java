package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 遊戲事件總線接口
 * 提供事件的發布-訂閱機制
 * 允許不同的遊戲系統之間透過事件進行解耦合的通訊
 */
public interface GameEventBus {
    /**
     * 發布事件
     * 
     * @param event 要發布的事件對象
     */
    void publish(Object event);

    /**
     * 訂閱特定類型的事件
     * 
     * @param eventType 要訂閱的事件類型
     * @param handler   事件處理函數
     * @param <T>       事件類型參數
     */
    <T> void subscribe(Class<T> eventType, Consumer<T> handler);

    /**
     * 簡單的事件總線實現
     * 使用 HashMap 儲存事件訂閱關係
     */
    class SimpleGameEventBus implements GameEventBus {
        // 儲存每種事件類型對應的處理函數列表
        private final Map<Class<?>, List<Consumer<?>>> subscribers = new HashMap<>();

        @Override
        public void publish(Object event) {
            // 獲取事件的實際類型
            Class<?> type = event.getClass();
            // 如果有訂閱者，則通知所有訂閱者
            if (subscribers.containsKey(type)) {
                for (Consumer<?> handler : subscribers.get(type)) {
                    // 類型轉換在這裡是安全的，因為我們在 subscribe 中強制了類型安全
                    @SuppressWarnings("unchecked")
                    Consumer<Object> eventHandler = (Consumer<Object>) handler;
                    eventHandler.accept(event);
                }
            }
        }

        @Override
        public <T> void subscribe(Class<T> eventType, Consumer<T> handler) {
            // 將處理函數添加到對應事件類型的訂閱列表中
            // 如果該類型還沒有訂閱者，先創建一個空列表
            subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
        }
    }
}
