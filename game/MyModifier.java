package game;

/**
 * 屬性修飾器類
 * 用於對實體的屬性進行永久性的修改
 * 可以透過乘法修改政擊力等屬性
 */
public class MyModifier {
    /** 攻擊力乘數（預設為 1.0，即不修改） */
    public double attackMultiplier = 1.0;

    /**
     * 設置攻擊力倍率
     * 透過乘法累積修改攻擊力
     * @param rate 要乘上的倍率（如 1.1 表示 +10%）
     */
    public void attackMul(double rate) {
        this.attackMultiplier *= rate;
    }

    /**
     * 將修飾器應用到屬性上下文
     * @param stats 原始屬性上下文
     * @return 修改後的新屬性上下文
     */
    public StatContext apply(StatContext stats) {
        // 創建屬性的副本以避免修改原始屬性
        StatContext newStats = new StatContext(stats);
        // 應用攻擊力乘數
        newStats.attack *= attackMultiplier;
        return newStats;
    }
}
