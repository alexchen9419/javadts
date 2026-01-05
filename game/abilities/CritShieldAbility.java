package game.abilities;

import game.*;

/**
 * 暴擊觸發護盾技能
 * 當持有者發生暴擊時，為自身增加護盾值
 * 支持冷卻時間機制，防止頻繁觸發
 * 
 * 示例：
 * - CritShield(100, 0)    - 每次暴擊加 100 護盾，無冷卻
 * - CritShield(100, 10s)  - 每次暴擊加 100 護盾，10秒冷卻
 */
public class CritShieldAbility implements Ability {
    /** 技能唯一識別碼：用於日誌記錄和技能查詢 */
    private final String id;
    
    /** 每次觸發增加的護盾點數 */
    private final int shieldAmount;
    
    /** 冷卻管理策略：控制技能的觸發頻率 */
    private final CooldownPolicy cd;
    
    /** 遊戲上下文：保存對事件總線的參考 */
    private GameContext ctx;

    /**
     * 構造函數
     * 
     * @param id 技能的唯一識別碼
     * @param shieldAmount 每次觸發增加的護盾量
     * @param cooldownMs 冷卻時間（毫秒，0 表示無冷卻）
     */
    public CritShieldAbility(String id, int shieldAmount, long cooldownMs) {
        this.id = id;
        this.shieldAmount = shieldAmount;
        this.cd = new CooldownPolicy(cooldownMs);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public BuffCategory category() {
        // 此技能是觸發型，分類為 OTHER
        return BuffCategory.OTHER;
    }

    @Override
    public void onAttach(Entity self, GameContext ctx) {
        this.ctx = ctx;
        // 訂閱暴擊事件，設定在每次暴擊時的回應行為
        ctx.bus.subscribe(Events.OnCrit.class, event -> {
            // 只有當暴擊者就是持有者時才處理
            if (event.attacker == self) {
                // 檢查是否已過冷卻時間
                if (cd.ready(self)) {
                    // 為持有者增加護盾值
                    self.addShield(shieldAmount);
                    // 開始新的冷卻週期
                    cd.consume(self);
                    // 記錄此事件到戰鬥日誌
                    // TODO：時間戳應來自全局遊戲時鐘（目前為簡化使用 0）
                    self.log.add(new LogEntry(0, "OnCrit", id, shieldAmount + " shield"));
                }
            }
        });
    }

    @Override
    public void onDetach(Entity self, GameContext ctx) {
        // 由於簡單實現中不容易取消訂閱，這裡留空
        // 如果需要取消訂閱，需要儲存 Consumer 的參照
    }

    @Override
    public StatContext modify(StatContext stats) {
        // 此技能為被動觸發型，不修改屬性，只產生護盾值
        return stats;
    }
}
