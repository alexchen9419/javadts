package game;

/**
 * 技能接口
 *
 * 定義了遊戲中所有技能必須實現的基本契約。
 * 技能可以響應遊戲事件、修改實體屬性、並管理自身的完整生命週期。
 *
 * 技能生命週期：
 * 1. onAttach() - 技能附加到實體時調用，進行初始化和事件訂閱
 * 2. modify() - 每次計算最終屬性時被調用，應用技能的屬性修改
 * 3. onDetach() - 技能從實體移除時調用，進行清理和取消訂閱
 *
 * 典型實現包括：
 * - 被動光環（修改屬性但不響應事件）
 * - 觸發技能（監聽特定事件並產生效果）
 * - 條件效果（基於遊戲狀態產生不同的修改）
 *
 * @author RuneRise Ability System
 */
public interface Ability {
    /**
     * 獲取技能的唯一識別碼
     * 
     * 用於日誌記錄、技能查詢和去重判定。
     * 同一實體上不應有重複 ID 的技能。
     * 
     * @return 技能 ID 字符串（如 "CRIT_SHIELD", "AURA_ATK_UP"）
     */
    String id();

    /**
     * 獲取技能的分類
     * 
     * 分類影響技能的互斥邏輯和 UI 顯示。
     * 例如，攻擊光環的分類會參與互斥機制的比較。
     * 
     * @return 技能分類（ATTACK_AURA, SHIELD, DEBUFF, OTHER）
     * @see BuffCategory
     */
    BuffCategory category();

    /**
     * 當技能附加到實體時調用
     * 
     * 用途：
     * - 設置事件監聽器（訂閱需要監聽的事件）
     * - 初始化技能狀態和計時器
     * - 執行技能初始化邏輯
     * 
     * @param self 擁有此技能的實體
     * @param ctx 遊戲上下文，包含事件總線和區域服務
     */
    void onAttach(Entity self, GameContext ctx);

    /**
     * 當技能從實體移除時調用
     * 
     * 用途：
     * - 取消事件訂閱
     * - 清理臨時數據和資源
     * - 執行清理邏輯
     * 
     * @param self 移除此技能的實體
     * @param ctx 遊戲上下文
     */
    void onDetach(Entity self, GameContext ctx);

    /**
     * 修改實體的屬性
     * 
     * 此方法在計算最終屬性時被調用。應用此技能的所有屬性修改。
     * 應該是無狀態的和冪等的（多次調用產生相同結果）。
     * 
     * @param stats 原始屬性上下文（此方法不應修改此對象）
     * @return 修改後的新屬性上下文
     */
    StatContext modify(StatContext stats);
}
