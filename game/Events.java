package game;

/**
 * 遊戲事件定義類
 * 包含所有遊戲中可能發生的事件類型
 * 這些事件會透過 GameEventBus 發布和訂閱
 */
public class Events {
    /**
     * 暴擊事件
     * 當實體發生暴擊時觸發
     */
    public static class OnCrit {
        /** 發動暴擊的攻擊者 */
        public final Entity attacker;

        public OnCrit(Entity attacker) {
            this.attacker = attacker;
        }
    }

    /**
     * Tick 事件
     * 遊戲的周期性更新事件，用於處理持續效果和光環效果
     */
    public static class Tick {
    }

    /**
     * 受到傷害事件
     * 當實體受到傷害時觸發
     */
    public static class OnDamageTaken {
        /** 受到傷害的目標實體 */
        public final Entity target;
        /** 傷害數量 */
        public final int amount;

        public OnDamageTaken(Entity target, int amount) {
            this.target = target;
            this.amount = amount;
        }
    }
}
