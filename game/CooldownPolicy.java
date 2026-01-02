package game;

import java.util.HashMap;
import java.util.Map;

/**
 * 冷卻時間管理策略類
 * 用於管理技能的冷卻時間，防止技能被頻繁觸發
 * 通常由技能實例持有，用於實現技能的冷卻機制
 * 
 * 需求示例："CritShield(100, 10s)" - 暴擊觸盾有 10 秒冷卻時間
 */
public class CooldownPolicy {
    // 上次觸發時間戳（毫秒），-1 表示從未觸發
    private long lastTriggerTime = -1;
    // 冷卻持續時間（毫秒）
    private final long cooldownDurationMs;

    /**
     * 構造函數
     * @param cooldownDurationMs 冷卻時間（毫秒）
     */
    public CooldownPolicy(long cooldownDurationMs) {
        this.cooldownDurationMs = cooldownDurationMs;
    }

    /**
     * 檢查技能是否準備就緒（冷卻是否完成）
     * @param e 實體參數（此實現中未使用，但保留以符合設計圖）
     * @return true 如果技能可以使用，false 如果仍在冷卻中
     * 
     * 注意：此實現使用系統時鐘（System.currentTimeMillis）來簡化邏輯
     * 在實際遊戲中，可能需要使用遊戲時間或時間步進機制
     */
    public boolean ready(Entity e) {
        // 如果從未觸發過，則立即可用
        if (lastTriggerTime == -1)
            return true;
        // 檢查當前時間與上次觸發時間的間隔是否超過冷卻時間
        return (System.currentTimeMillis() - lastTriggerTime) >= cooldownDurationMs;
    }

    /**
     * 消耗技能使用次數，記錄觸發時間
     * 在技能成功觸發後調用，開始冷卻計時
     * @param e 實體參數
     */
    public void consume(Entity e) {
        this.lastTriggerTime = System.currentTimeMillis();
    }

    /**
     * 設置上次觸發時間（用於測試）
     * @param t 時間戳
     */
    public void setLastTriggerTime(long t) {
        this.lastTriggerTime = t;
    }
}
