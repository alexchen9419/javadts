package game;

/**
 * 遊戲模式枚舉
 * 定義了遊戲的不同模式，影響技能效果的計算
 */
public enum Mode {
    /** 玩家對戰環境（Player vs Environment） */
    PVE,
    
    /** 玩家對戰玩家（Player vs Player）
     *  在 PVP 模式下，攻擊光環有 5% 的效果上限
     */
    PVP
}
