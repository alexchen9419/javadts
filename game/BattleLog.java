package game;

import java.util.ArrayList;
import java.util.List;

/**
 * 戰鬥日誌類
 *
 * 記錄遊戲過程中發生的所有事件，用於調試、重放和UI顯示。
 * 每個實體都有自己的戰鬥日誌，追蹤該實體經歷的所有事件。
 *
 * 儲存格式：
 * 時間戳 -> 事件類型 -> 觸發技能 -> 效果描述
 * 例：t:1,evt:OnCrit,ability:CRIT_SHIELD,effect:100 shield
 */
public class BattleLog {
    // 日誌條目列表，按時間順序儲存
    private final List<LogEntry> entries = new ArrayList<>();

    /**
     * 添加一條日誌記錄
     * 
     * @param entry 要添加的日誌條目
     */
    public void add(LogEntry entry) {
        entries.add(entry);
    }

    /**
     * 獲取所有日誌條目
     * 
     * @return 日誌條目列表（按時間順序）
     */
    public List<LogEntry> getEntries() {
        return entries;
    }

    /**
     * 打印所有日誌記錄到控制台
     * 
     * 格式化顯示每一條日誌條目，方便調試查看。
     */
    public void print() {
        System.out.println("Battle Log:");
        for (LogEntry e : entries) {
            System.out.println("  " + e.toString());
        }
    }
}
