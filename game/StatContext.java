package game;

import java.util.HashMap;
import java.util.Map;

public class StatContext {
    // Storing stats in a map for flexibility, or could use fields.
    // Based on 'attackMul(rate: double)' in MyModifier, we likely need 'attack'.
    public double attack;
    public double defense;

    // Default values
    public StatContext() {
        this.attack = 100.0; // Base value assumption
        this.defense = 0.0;
    }

    // Copy constructor for modifiers
    public StatContext(StatContext other) {
        this.attack = other.attack;
        this.defense = other.defense;
    }
}
