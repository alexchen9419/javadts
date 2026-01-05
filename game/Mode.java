package game;

/**
 * 遊戲模式枚舉
 *
 * 定義了遊戲的不同模式，影響技能效果和平衡性。
 * 不同模式下相同的技能可能有不同的表現。
 */
public enum Mode {
    /** 玩家對戰環境（Player vs Environment） */
    PVE,
    
    /** 玩家對戰玩家（Player vs Player）
     *  在 PVP 模式下，攻擊光環的效果被限制在 5% 內以保證平衡
     */
    PVP
}
