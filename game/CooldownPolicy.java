package game;

import java.util.HashMap;
import java.util.Map;

// Helper to manage cooldowns per ability instance or type?
// Diagram shows CooldownPolicy as a class.
// Requirements: "CritShield(100, 10s)", "Events... OnCrit...".
// "暴擊觸盾（有冷卻）：10s 冷卻".
// So usually the Ability instance holds the CooldownPolicy.
public class CooldownPolicy {
    private long lastTriggerTime = -1;
    private final long cooldownDurationMs; // Simplified to ms or separate ticks

    public CooldownPolicy(long cooldownDurationMs) {
        this.cooldownDurationMs = cooldownDurationMs;
    }

    public boolean ready(Entity e) {
        // e is unused in this simple policy, but diagram has it signature: ready(e:
        // Entity)
        // We might use "Game Time" from context?
        // But here we'll just track system time or simulated time.
        // Let's rely on System.currentTimeMillis() for simplicty unless 't' in logs
        // implies logic steps.
        // The logs use 't:1', 't:2'. This implies abstract time steps.
        // I should probably track 'lastTriggerTime' in terms of ticks if provided, or
        // ms.
        // The "10s" suggests real time or game time units.
        // I will assume for this exercise we can allow passing current time or simple
        // check.
        // But wait, the method signature is `ready(e: Entity)`. The Entity doesn't
        // carry time.
        // Maybe Entity or Context provides time.
        // Let's use System.currentTimeMillis for the mock implementation in Main.
        if (lastTriggerTime == -1)
            return true;
        // In a real system, we'd pass 't' or 'GameContext'.
        // Since I can't change the signature easily if I stick strictly to diagram,
        // I'll rely on static clock or stored state.
        // Strict adherence to diagram: ready(e: Entity).
        // I'll stick to wall clock for simplicity in Main demo.
        return (System.currentTimeMillis() - lastTriggerTime) >= cooldownDurationMs;
    }

    public void consume(Entity e) {
        this.lastTriggerTime = System.currentTimeMillis();
    }

    // Extra method for testing with manual time
    public void setLastTriggerTime(long t) {
        this.lastTriggerTime = t;
    }
}
