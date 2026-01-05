package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 遊戲事件總線接口
 *
 * 實現發布-訂閱設計模式，使遊戲系統之間能夠解耦合地通信。
 * 各個系統可以發布事件而無需知道訂閱者是誰，反之亦然。
 *
 * 典型使用場景：
 * - 技能訂閱 OnCrit 事件，在實體暴擊時觸發
 * - 光環訂閱 Tick 事件，每個遊戲時刻檢查範圍內的盟友
 * - 實體發布受傷事件，供其他系統做出反應
 *
 * @author RuneRise System
 */
public interface GameEventBus {
    /**
     * 發布事件
     *
     * 通知所有訂閱了該事件類型的處理器。
     * 事件發布後立即按訂閱順序通知所有監聽者。
     * 
     * @param event 要發布的事件對象，其類型決定將通知哪些訂閱者
     */
    void publish(Object event);

    /**
     * 訂閱特定類型的事件
     *
     * 註冊一個事件監聽器，當該類型的事件發布時自動觸發。
     * 同一事件類型可以有多個訂閱者，按訂閱順序依次調用。
     * 
     * @param <T> 事件類型參數
     * @param eventType 要訂閱的事件類型
     * @param handler 事件發生時要執行的回調函數
     * 
     * @example
     * <pre>
     * bus.subscribe(Events.OnCrit.class, event -> {
     *     System.out.println("Crit event from " + event.attacker.element);
     * });
     * </pre>
     */
    <T> void subscribe(Class<T> eventType, Consumer<T> handler);

    /**
     * 簡單事件總線實現
     *
     * 使用 HashMap 儲存事件類型到訂閱者列表的映射。
     * 這是一個記憶體高效但無法取消訂閱的簡單實現。
     */
    class SimpleGameEventBus implements GameEventBus {
        // 結構：Class<?> -> List<Consumer<?>>
        private final Map<Class<?>, List<Consumer<?>>> subscribers = new HashMap<>();

        @Override
        public void publish(Object event) {
            Class<?> type = event.getClass();
            // 若該事件類型有訂閱者，逐個通知
            if (subscribers.containsKey(type)) {
                for (Consumer<?> handler : subscribers.get(type)) {
                    // 安全的類型轉換：已在 subscribe() 中驗證
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
