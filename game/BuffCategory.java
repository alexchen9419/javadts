package game;

/**
 * 增益/減益效果分類枚舉
 * 用於標識不同類型的技能效果
 * 某些分類（如攻擊光環）有特殊的互斥規則
 */
public enum BuffCategory {
    /** 攻擊光環效果 - 同類型效果會進行互斥比較，只保留最強的 */
    ATTACK_AURA,
    
    /** 護盾效果 */
    SHIELD,
    
    /** 減益效果 */
    DEBUFF,
    
    /** 其他類型效果 */
    OTHER
}
