package game;

import java.util.ArrayList;
import java.util.List;

/**
 * 戰鬥日誌類
 * 記錄遊戲過程中發生的各種事件
 * 包括技能觸發、效果應用等資訊
 */
public class BattleLog {
    // 日誌條目列表
    private final List<LogEntry> entries = new ArrayList<>();

    /**
     * 添加一條日誌記錄
     * @param entry 要添加的日誌條目
     */
    public void add(LogEntry entry) {
        entries.add(entry);
    }

    /**
     * 獲取所有日誌條目
     * @return 日誌條目列表
     */
    public List<LogEntry> getEntries() {
        return entries;
    }

    /**
     * 打印所有日誌記錄到控制台
     * 格式化顯示每一條日誌條目
     */
    public void print() {
        System.out.println("log:");
        for (LogEntry e : entries) {
            System.out.println("  " + e.toString());
        }
    }
}
