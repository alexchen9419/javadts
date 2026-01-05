package game;

import java.util.HashMap;
import java.util.Map;

/**
 * 冷卻時間管理策略類
 *
 * 為技能實現冷卻機制，防止技能被過度頻繁地觸發。
 * 使用系統時鐘追蹤上次觸發時間，判斷冷卻是否完成。
 *
 * 使用場景示例：
 * - "CritShield(200, 5000ms)" - 暴擊護盾有 5 秒冷卻
 * - 防止單次事件（如暴擊）在短時間內多次觸發同個技能
 *
 * @author RuneRise System
 */
public class CooldownPolicy {
    // 上次觸發時間戳（毫秒），-1 表示從未觸發
    private long lastTriggerTime = -1;
    
    // 冷卻持續時間（毫秒），決定相隔多長時間後才能再次觸發
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
     *
     * 邏輯：
     * - 若從未觸發過（lastTriggerTime == -1），立即可用
     * - 若已觸發過，比較當前時間與上次觸發時間的間隔
     * - 若間隔 >= 冷卻時間，則準備就緒
     *
     * @param e 實體參數（此實現中未使用，但保留供擴展）
     * @return true 如果冷卻已完成且技能可用，false 如果仍在冷卻中
     * 
     * @note 此實現使用系統時鐘 {@link System#currentTimeMillis()}
     *       在實際遊戲中可能需要使用遊戲時間或固定時間步進
     */
    public boolean ready(Entity e) {
        // 從未觸發過，立即可用
        if (lastTriggerTime == -1)
            return true;
        // 已觸發過，檢查冷卻時間是否已過
        long now = System.currentTimeMillis();
        return now - lastTriggerTime >= cooldownDurationMs;
    }

    /**
     * 記錄技能觸發時間，開始冷卻倒計時
     *
     * 應在技能成功觸發後調用，開始冷卻計時。
     * 記錄的時間戳用於後續的 ready() 判定。
     * 
     * @param e 實體參數（此實現中未使用）
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
