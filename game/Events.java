package game;

public class Events {
    public static class OnCrit {
        public final Entity attacker;

        public OnCrit(Entity attacker) {
            this.attacker = attacker;
        }
    }

    public static class Tick {
    }

    public static class OnDamageTaken {
        public final Entity target;
        public final int amount;

        public OnDamageTaken(Entity target, int amount) {
            this.target = target;
            this.amount = amount;
        }
    }
}
