package game;

/**
 * 遊戲事件定義類
 *
 * 包含所有遊戲中可能發生的事件類型。
 * 這些事件透過 GameEventBus 的發布-訂閱機制傳遞給訂閱者。
 *
 * 事件是遊戲系統之間的通訊媒介，使各個系統保持解耦合。
 *
 * @author RuneRise Event System
 */
public class Events {
    /**
     * 暴擊事件
     * 
     * 當實體發動暴擊時發布此事件。
     * 其他系統（如暴擊護盾技能）可訂閱此事件並做出反應。
     * 
     * @see CritShieldAbility
     */
    public static class OnCrit {
        /** 發動暴擊的攻擊者 */
        public final Entity attacker;

        /**
         * 構造函數
         * @param attacker 暴擊的來源實體
         */
        public OnCrit(Entity attacker) {
            this.attacker = attacker;
        }
    }

    /**
     * Tick 事件
     * 
     * 遊戲的周期性更新事件。
     * 每個遊戲時刻都會發布此事件，用於：
     * - 光環效果的檢查和應用
     * - 持續效果的更新
     * - 時間限制效果的計時
     * 
     * @see AttackAuraAbility
     */
    public static class Tick {
    }

    /**
     * 受到傷害事件
     * 
     * 當實體受到傷害時發布此事件。
     * 其他系統可以訂閱此事件以實現反傷害、格擋等機制。
     */
    public static class OnDamageTaken {
        /** 受到傷害的目標實體 */
        public final Entity target;
        
        /** 傷害數量 */
        public final int amount;

        /**
         * 構造函數
         * @param target 受傷害的實體
         * @param amount 傷害數值
         */
        public OnDamageTaken(Entity target, int amount) {
            this.target = target;
            this.amount = amount;
        }
    }
}
