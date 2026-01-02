package game;

/**
 * 日誌條目類
 * 代表一條戰鬥日誌記錄
 * 記錄了事件發生的時間、事件類型、技能名稱和效果描述
 */
public class LogEntry {
    /** 時間戳（時間步） */
    public int t;
    
    /** 事件類型（如 OnCrit, Tick 等） */
    public String evt;
    
    /** 觸發該效果的技能 ID */
    public String ability;
    
    /** 效果描述（如 "100 shield", "Applied EFFECT_AURA_ATK_UP" 等） */
    public String effect;

    /**
     * 構造函數
     * @param t 時間戳
     * @param evt 事件類型
     * @param ability 技能 ID
     * @param effect 效果描述
     */
    public LogEntry(int t, String evt, String ability, String effect) {
        this.t = t;
        this.evt = evt;
        this.ability = ability;
        this.effect = effect;
    }

    /**
     * 轉換為字符串表示
     * @return 格式化的日誌字符串
     *         示例："t:1,evt: OnCrit,ability:CRIT_SHIELD,effect:100 shield"
     */
    @Override
    public String toString() {
        return String.format("t:%d,evt: %s,ability:%s,effect:%s", t, evt, ability, effect);
    }
}
