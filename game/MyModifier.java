package game;

public class MyModifier {
    // "attackMul(rate : double)" in diagram
    public double attackMultiplier = 1.0;

    public void attackMul(double rate) {
        this.attackMultiplier *= rate;
    }

    public StatContext apply(StatContext stats) {
        StatContext newStats = new StatContext(stats);
        newStats.attack *= attackMultiplier;
        return newStats;
    }
}
