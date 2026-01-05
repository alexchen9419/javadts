package game;

/**
 * 元素屬性枚舉
 *
 * 定義遊戲中的元素類型及其相互之間的剋制關係。
 * 元素剋制形成一個迴圈：火 > 木 > 水 > 火（> 表示剋制）
 *
 * 剋制規則：
 * - 火剋木：火系攻擊對木系目標造成 1.5 倍傷害
 * - 木剋水：木系攻擊對水系目標造成 1.5 倍傷害
 * - 水剋火：水系攻擊對火系目標造成 1.5 倍傷害
 * - NONE 元素不參與剋制（攻擊和被攻擊時都是 1.0 倍）
 *
 * @author RuneRise Game System
 */
public enum Element {
    FIRE("火"),
    WATER("水"),
    WOOD("木"),
    NONE("無");

    private final String name;

    Element(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * 獲取對目標元素的傷害倍率
     * 
     * 根據元素剋制規則計算傷害倍率。
     * 自己的元素不被考慮（此方法在 this 上調用），只考慮 target。
     * 
     * @param target 目標元素
     * @return 傷害倍率：1.0（無倍率）或 1.5（剋制）
     */
    public double getCounterMultiplier(Element target) {
        if (target == null || target == NONE || this == NONE) {
            return 1.0;
        }

        if (this == FIRE && target == WOOD)
            return 1.5;
        if (this == WOOD && target == WATER)
            return 1.5;
        if (this == WATER && target == FIRE)
            return 1.5;

        return 1.0;
    }
}
