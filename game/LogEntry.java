package game;

public class LogEntry {
    public int t;
    public String evt;
    public String ability;
    public String effect;

    public LogEntry(int t, String evt, String ability, String effect) {
        this.t = t;
        this.evt = evt;
        this.ability = ability;
        this.effect = effect;
    }

    @Override
    public String toString() {
        return String.format("t:%d,evt: %s,ability:%s,effect:%s", t, evt, ability, effect);
    }
}
