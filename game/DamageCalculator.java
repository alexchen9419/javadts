package game;

/**
 * 傷害計算器
 *
 * 負責計算戰鬥中的實際傷害數值。
 * 考慮因素：攻擊力、防禦力、元素剋制等。
 *
 * 傷害公式：
 * rawDamage = max(1, 攻擊者攻擊力 - 目標防禦力)
 * finalDamage = rawDamage * 元素倍率
 *
 * 元素剋制倍率規則：
 * - 火剋木：火系攻擊對木系目標 1.5 倍
 * - 木剋水：木系攻擊對水系目標 1.5 倍
 * - 水剋火：水系攻擊對火系目標 1.5 倍
 * - 其他組合：1.0 倍（無倍率）
 *
 * @author RuneRise Combat System
 */
public class DamageCalculator {

    /**
     * 計算傷害
     * 
     * 傷害計算步驟：
     * 1. 獲取雙方的最終屬性（已應用所有技能修改）
     * 2. 計算基礎傷害 = 攻擊力 - 防禦力（最低 1）
     * 3. 應用元素剋制倍率
     * 4. 返回最終傷害值（取整）
     * 
     * @param attacker 攻擊者
     * @param target 防禦者
     * @return 最終傷害值（至少 1）
     */
    public static int calculateDamage(Entity attacker, Entity target) {
        StatContext atkStats = attacker.getFinalStats();
        StatContext defStats = target.getFinalStats();

        // 基礎傷害 = 攻擊力 - 防禦力，最低 1 點
        double rawDamage = Math.max(1.0, atkStats.attack - defStats.defense);

        // 應用元素剋制倍率
        double multiplier = attacker.element.getCounterMultiplier(target.element);

        return (int) (rawDamage * multiplier);
    }
}
