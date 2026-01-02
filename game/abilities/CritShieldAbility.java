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
    /** 技能唯一識別碼 */
    private final String id;
    /** 每次觸發增加的護盾值 */
    private final int shieldAmount;
    /** 冷卻管理策略 */
    private final CooldownPolicy cd;
    /** 遊戲上下文 */
    private GameContext ctx;

    /**
     * 構造函數
     * @param id 技能 ID
     * @param shieldAmount 護盾數量
     * @param cooldownMs 冷卻時間（毫秒）
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
        // 訂閱暴擊事件
        ctx.bus.subscribe(Events.OnCrit.class, event -> {
            // 檢查暴擊者是否為持有者自己
            if (event.attacker == self) {
                // 檢查冷卻是否完成
                if (cd.ready(self)) {
                    // 增加護盾
                    self.addShield(shieldAmount);
                    // 消耗冷卻
                    cd.consume(self);
                    // 記錄日誌
                    // 理想情況下，時間戳 't' 應該來自全局時間管理器
                    // 這裡使用 0 作為占位符
                    // 日誌格式："t:1,evt: OnCrit,ability:CRIT_SHIELD,effect:100 shield"
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
        // 此技能不修改屬性，只是觸發效果
        return stats;
    }
}
