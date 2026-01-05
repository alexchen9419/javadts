package game;

/**
 * 屬性修飾器類
 *
 * 用於對實體的屬性進行永久性的修改。
 * 採用乘法修改方式，可以堆疊多個修飾器。
 *
 * 設計模式：Builder + Function
 * - 支持鏈式調用設置多個修飾
 * - apply() 方法採用函數式風格（輸入屬性，返回新的屬性）
 *
 * 使用例子：
 * <pre>
 * MyModifier mod = new MyModifier();
 * mod.attackMul(1.1);  // 攻擊力 +10%
 * mod.attackMul(1.2);  // 再乘以 1.2，相當於 +32% 總計
 * StatContext newStats = mod.apply(originalStats);  // 應用修飾
 * </pre>
 */
public class MyModifier {
    /** 攻擊力乘數：累積儲存所有攻擊力修改的乘積（預設 1.0，不修改） */
    public double attackMultiplier = 1.0;

    /**
     * 設置攻擊力倍率
     *
     * 透過乘法方式修改攻擊力。多次調用時會累積乘積。
     * 
     * @param rate 要乘上的倍率
     *             - 1.0：不變
     *             - 1.1：增加 10%
     *             - 0.9：減少 10%
     *             - 2.0：翻倍
     * 
     * @return this（支持鏈式調用）
     */
    public MyModifier attackMul(double rate) {
        this.attackMultiplier *= rate;
        return this;
    }

    /**
     * 將修飾器應用到屬性上下文
     *
     * 創建原始屬性的副本，應用此修飾器的所有修改，並返回新的屬性對象。
     * 原始屬性對象保持不變。
     * 
     * @param stats 原始屬性上下文
     * @return 修改後的新屬性上下文
     */
    public StatContext apply(StatContext stats) {
        // 創建屬性的副本以避免修改原始對象
        StatContext newStats = new StatContext(stats);
        // 應用攻擊力乘數
        newStats.attack *= attackMultiplier;
        return newStats;
    }
}
