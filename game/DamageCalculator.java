package game;

/**
 * 傷害計算器
 * 負責計算戰鬥中的實際傷害
 */
public class DamageCalculator {

    /**
     * 計算傷害
     * 公式：(攻擊者攻擊力 - 目標防禦力) * 元素倍率
     * 最低傷害為 1
     * 
     * @param attacker 攻擊者
     * @param target   目標
     * @return 最終傷害值
     */
    public static int calculateDamage(Entity attacker, Entity target) {
        StatContext atkStats = attacker.getFinalStats();
        StatContext defStats = target.getFinalStats();

        double rawDamage = Math.max(1.0, atkStats.attack - defStats.defense);

        // 應用元素剋制
        double multiplier = attacker.element.getCounterMultiplier(target.element);

        return (int) (rawDamage * multiplier);
    }
}
