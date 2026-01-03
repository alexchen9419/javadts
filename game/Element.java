package game;

/**
 * 元素屬性枚舉
 * 定義遊戲中的元素類型及其剋制關係
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
     * 規則：火剋木，木剋水，水剋火 (1.5倍)
     * 
     * @param target 目標元素
     * @return 傷害倍率 (1.0 或 1.5)
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
