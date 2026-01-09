package game;

/**
 * 技能接口
 * 定義了遊戲中所有技能必須實現的基本方法
 * 技能可以響應事件、修改屬性、並管理自身的生命週期
 */
public interface Ability {
    /**
     * 獲取技能的唯一識別碼
     * @return 技能ID字符串
     */
    String id();

    /**
     * 獲取技能的分類
     * @return 技能分類（如攻擊光環、護盾、減益效果等）
     */
    BuffCategory category();

    /**
     * 當技能附加到實體時調用
     * 用於設置事件監聽器和初始化技能狀態
     * @param self 擁有此技能的實體
     * @param ctx 遊戲上下文，包含事件總線和區域服務
     */
    void onAttach(Entity self, GameContext ctx);

    /**
     * 當技能從實體移除時調用
     * 用於清理資源和取消事件訂閱
     * @param self 移除此技能的實體
     * @param ctx 遊戲上下文
     */
    void onDetach(Entity self, GameContext ctx);

    /**
     * 修改實體的屬性
     * 將此技能的效果應用到給定的屬性上下文
     * @param stats 原始屬性上下文
     * @return 修改後的屬性上下文
     */
    StatContext modify(StatContext stats);
}
