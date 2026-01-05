package game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 實體類 - 遊戲中的角色或單位
 *
 * 代表遊戲中的任何可交互單位（玩家、敵人等），擁有以下職責：
 * - 管理生命值、護盾和屬性等基礎數據
 * - 維護技能列表並管理技能的生命週期
 * - 計算最終屬性（應用技能修飾）
 * - 處理傷害邏輯（護盾優先吸收）
 * - 支持光環互斥機制（同類光環只保留最強的）
 *
 * 屬性系統：
 * - baseStats：未經任何修飾的基礎屬性
 * - getFinalStats()：應用所有技能效果後的最終屬性
 */
public class Entity {
    /** 護盾值：優先吸收傷害，在受到傷害時先扣除護盾值 */
    public int shield;

    /** 最大生命值：實體的生命值上限 */
    public int maxHp;

    /** 當前生命值：實體當前剩餘的生命值 */
    public int currentHp;

    /** 元素屬性：決定與其他元素的剋制關係（火、水、木、無） */
    public Element element;

    /** 增益效果列表：用於 UI 顯示，從技能列表動態衍生（冗餘設計用於展示） */
    public List<String> buffs = new ArrayList<>();

    /** 附加在此實體上的所有技能：包括被動光環、觸發技能等 */
    public List<Ability> abilities = new ArrayList<>();

    /** 戰鬥日誌記錄器：記錄此實體發生的所有事件 */
    public BattleLog log;

    /** 基礎屬性（未經技能修改）：所有屬性計算的起點 */
    private StatContext baseStats;

    /**
     * 主構造函數
     * 
     * @param log 戰鬥日誌記錄器，用於記錄此實體的所有事件
     * @param maxHp 最大生命值（初始化時等於當前 HP）
     * @param element 元素屬性（決定剋制關係）
     */
    public Entity(BattleLog log, int maxHp, Element element) {
        this.log = log;
        this.baseStats = new StatContext();  // 初始化為預設屬性值
        this.shield = 0;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.element = element;
    }

    /**
     * 便利構造函數 - 為了相容舊代碼
     * 
     * 預設值：1000 HP，無屬性
     * @param log 戰鬥日誌記錄器
     */
    public Entity(BattleLog log) {
        this(log, 1000, Element.NONE);
    }

    /**
     * 添加技能到實體
     *
     * 注意：此方法僅將技能添加到列表中，不自動觸發初始化。
     * 調用者必須在外部單獨調用 {@code ability.onAttach(entity, ctx)} 來完成初始化。
     * 這樣的設計遵循責任分離原則，方便調用者控制初始化時機。
     * 
     * @param a 要添加的技能
     * 
     * @see Ability#onAttach(Entity, GameContext)
     */
    public void addAbility(Ability a) {
        abilities.add(a);
        // 設計說明：onAttach 需要 GameContext，但此方法不接收。
        // 這強制調用者在外部顯式調用 onAttach，避免隱藏的初始化邏輯。
    }

    /**
     * 從實體移除技能
     * 
     * @param a 要移除的技能
     */
    public void removeAbility(Ability a) {
        abilities.remove(a);
    }

    /*
     * 永久性地修改實體的基礎屬性值。修飾器可能來自技能或環境效果。
     * 修改後的屬性會影響後續的 getFinalStats() 計算結果。
     * 
     * @param m 要應用的修飾器（通常包含屬性乘數）
     */
    public void applyModifier(MyModifier m) {
        // 將修飾器應用到基礎屬性，產生永久性的屬性變化
        this.baseStats = m.apply(this.baseStats);
    }

    /**
     *
     * 護盾值無上限，可以無限堆疊。日誌記錄由技能負責。
     * 
     * @param amount 要增加的護盾量（通常為正數）
     */
    public void addShield(int amount) {
        this.shield += amount;
        // 日誌記錄在技能層面處理（見 CritShieldAbility）
        // 這樣保證日誌的語義正確性和完整性
        // 示例："evt: OnCrit, ability: CRIT_SHIELD, effect: 100 shield"
    }

    /**
     * 受到傷害
     * 優先扣除護盾，剩餘傷害扣除 HP
     * 
     * @param amount 傷害量
     */
    public void takeDamage(int amount) {
        int remainingDamage = amount;

        // 先扣盾
        if (shield > 0) {
            if (shield >= remainingDamage) {
                shield -= remainingDamage;
                remainingDamage = 0;
            } else {
                remainingDamage -= shield;
                shield = 0;
            }
        }

        // 再扣血
        if (remainingDamage > 0) {
            currentHp = Math.max(0, currentHp - remainingDamage);
        }
    }

    /*
     * 將所有附加的技能效果應用到基礎屬性上，產生最終屬性值。
     * 實現了攻擊光環的互斥機制：同類型光環中只有效果最強的被應用。
     *
     * 互斥機制詳解：
     * - 對所有攻擊光環進行試運行計算
     * - 比較試運行結果，找出能產生最高攻擊力的光環
     * - 只應用效果最強的光環，其他同類光環被跳過
     * - 非攻擊光環全部應用（無互斥限制）
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
     *
     * 顯示實體的關鍵狀態信息：生命值、護盾、元素屬性和當前生效的技能。
     * 格式方便在日誌和調試中快速查看實體狀態。
     * 
     * @return 格式化的實體狀態字符串
     *         示例："HP: 1200/1500, Shield: 200, Element: 火, Buffs:ATTACK_AURA,OTHER"
     */
    @Override
    public String toString() {
        // 從技能列表中提取增益分類名稱，去重後用逗號連接
        String buffList = abilities.stream()
                .map(a -> a.category().name())
                .distinct()
                .collect(Collectors.joining(","));

        return String.format("HP: %d/%d, Shield: %d, Element: %s, Buffs:%s",
                currentHp, maxHp, shield, element.getName(), buffList);
    }
}
