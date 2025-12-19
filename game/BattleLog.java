package game;

import java.util.ArrayList;
import java.util.List;

public class BattleLog {
    private final List<LogEntry> entries = new ArrayList<>();

    public void add(LogEntry entry) {
        entries.add(entry);
    }

    public List<LogEntry> getEntries() {
        return entries;
    }

    public void print() {
        System.out.println("log:");
        for (LogEntry e : entries) {
            System.out.println("  " + e.toString());
        }
    }
}
