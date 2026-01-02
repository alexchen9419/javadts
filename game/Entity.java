package game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 實體類
 * 代表遊戲中的一個角色或單位
 * 擁有護盾值、技能列表、增益效果列表和戰鬥日誌
 * 能夠計算最終屬性並管理技能的附加與移除
 */
public class Entity {
    /** 護盾值 */
    public int shield;
    
    /** 增益效果列表（用於顯示，從技能列表衍生） */
    public List<String> buffs = new ArrayList<>();

    /** 附加在此實體上的所有技能 */
    public List<Ability> abilities = new ArrayList<>();
    
    /** 戰鬥日誌記錄器 */
    public BattleLog log;

    /** 基礎屬性（未經技能修改） */
    private StatContext baseStats;

    /**
     * 構造函數
     * @param log 戰鬥日誌記錄器
     */
    public Entity(BattleLog log) {
        this.log = log;
        this.baseStats = new StatContext();
        this.shield = 0;
    }

    /**
     * 添加技能到實體
     * 注意：此方法只將技能添加到列表中
     * 調用者需要單獨調用 ability.onAttach(entity, ctx) 來完成技能的初始化
     * @param a 要添加的技能
     */
    public void addAbility(Ability a) {
        abilities.add(a);
        // 注意：onAttach 需要 GameContext 參數
        // 但此方法簽名不包含 GameContext
        // 這意味著調用者（如 Main 中的場景設置）需要在外部調用 onAttach
        // 這樣的設計遵循了類圖規範，保持方法簽名簡潔
    }

    /**
     * 從實體移除技能
     * @param a 要移除的技能
     */
    public void removeAbility(Ability a) {
        abilities.remove(a);
    }

    /**
     * 應用修飾器到基礎屬性
     * 永久性地修改實體的基礎屬性值
     * @param m 要應用的修飾器
     */
    public void applyModifier(MyModifier m) {
        // 將修飾器應用到基礎屬性，產生永久性的屬性變化
        this.baseStats = m.apply(this.baseStats);
    }

    /**
     * 增加護盾值
     * @param amount 要增加的護盾量
     * 注意：日誌記錄由技能負責（見 CritShieldAbility）
     */
    public void addShield(int amount) {
        this.shield += amount;
        // 日誌記錄在技能層面處理，不在此處記錄
        // 示例："evt: OnCrit, ability: CRIT_SHIELD, effect: 100 shield"
    }

    /**
     * 計算最終屬性
     * 將所有技能效果應用到基礎屬性上，產生最終屬性值
     * 
     * 特殊處理：攻擊光環互斥機制
     * - 同類型的攻擊光環只會應用效果最強的那個
     * - 其他類型的技能效果會全部應用
     * 
     * @return 計算後的最終屬性上下文
     */
    public StatContext getFinalStats() {
        // 從基礎屬性開始計算
        StatContext current = new StatContext(baseStats);

        // 處理攻擊光環的互斥機制
        // 規則：只保留效果最大的光環
        // 由於我們無法在不實際應用的情況下預測效果大小，
        // 因此採用試運行的方式：對每個光環分別應用到基礎屬性，
        // 比較結果，選擇產生最高攻擊力的那個

        List<Ability> allAbilities = new ArrayList<>(abilities);
        // 篩選出所有攻擊光環類型的技能
        List<Ability> attackAuras = allAbilities.stream()
                .filter(a -> a.category() == BuffCategory.ATTACK_AURA)
                .collect(Collectors.toList());

        // 找出效果最強的攻擊光環
        Ability bestAura = null;
        double bestAttack = -1.0;

        if (!attackAuras.isEmpty()) {
            for (Ability a : attackAuras) {
                // 試運行：將光環應用到基礎屬性的副本上
                StatContext test = a.modify(new StatContext(baseStats));
                if (test.attack > bestAttack) {
                    bestAttack = test.attack;
                    bestAura = a;
                }
            }
        }

        // 應用所有技能效果
        for (Ability a : allAbilities) {
            if (a.category() == BuffCategory.ATTACK_AURA) {
                // 攻擊光環：只應用最強的那個
                if (a == bestAura) {
                    current = a.modify(current);
                }
                // 其他攻擊光環被跳過
            } else {
                // 非攻擊光環：全部應用
                current = a.modify(current);
            }
        }
        return current;
    }

    /**
     * 轉換為字符串表示
     * @return 格式化的實體狀態字符串，包含護盾值和增益效果列表
     *         示例："shield: 200, buffs:AURA_ATK_UP"
     */
    @Override
    public String toString() {
        // 從技能列表中提取增益效果分類名稱
        // 去重後用逗號連接
        String buffList = abilities.stream()
                .map(a -> a.category().name())
                .distinct()
                .collect(Collectors.joining(","));

        return String.format("shield: %d, buffs:%s", shield, buffList);
    }
}
