package game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Entity {
    public int shield;
    // buffs: List_ in diagram. Requirements say "Entity: (shield, buffs:
    // AURA_ATK_UP)".
    // Buffs might be the subset of Abilities that are buffs? Or just their
    // categories/IDs.
    // I'll make it a list of Strings (Category Names or IDs) for display, or just
    // derived from abilities.
    // But diagram has a field `buffs : List_`. I'll make it List<String> for now.
    public List<String> buffs = new ArrayList<>();

    public List<Ability> abilities = new ArrayList<>();
    public BattleLog log;

    // We likely need base stats
    private StatContext baseStats;

    public Entity(BattleLog log) {
        this.log = log;
        this.baseStats = new StatContext();
        this.shield = 0;
    }

    public void addAbility(Ability a) {
        abilities.add(a);
        // Note: onAttach requires GameContext.
        // But this method signature `addAbility(a: Ability)` doesn't take GameContext.
        // This implies Entity might need access to it, or it is called from outside
        // where context is available?
        // Wait, diagram says `onAttach(self, ctx)`.
        // If `addAbility` doesn't take ctx, maybe we don't call onAttach here?
        // Or maybe we overload it? Or strict diagram adherence means the CALLER must
        // call onAttach separately?
        // "Events: ...".
        // Let's assume for now we just add it to list. The caller (Scenario setup)
        // handles onAttach if needed,
        // or we change signature to match reality (pass context).
        // Given `Main` will drive this, I can probably manage context there.
        // However, it's cleaner if addAbility does it.
        // I will stick to diagram: Entity.addAbility(Ability).
        // I will assume onAttach is called by the system (Main) when adding logic.
    }

    public void removeAbility(Ability a) {
        abilities.remove(a);
    }

    public void applyModifier(MyModifier m) {
        // Diagram: applyModifier(m : MyModifier).
        // "Entity... applyModifier".
        // This implies permanent modification of base stats?
        // Or temporary?
        // Likely purely specific use case.
        // I'll make it modify baseStats.
        this.baseStats = m.apply(this.baseStats);
    }

    public void addShield(int amount) {
        this.shield += amount;
        // Should we log here? "BattleLog: 觸發紀錄（...何時加盾...）"
        // The log example shows: "evt: OnCrit, ability: CRIT_SHIELD, effect: 100
        // shield"
        // That log comes from the Ability, not necessarily here.
    }

    // Method to calculate final stats (Output requirement: StatContext)
    public StatContext getFinalStats() {
        StatContext current = new StatContext(baseStats);

        // Handle Mutual Exclusion for ATTACK_AURA
        // Rule: Only take the one with the largest effect (we assume modify increases
        // stats, so we need a way to measure 'largest').
        // Since we can't easily dry-run modify without applying it, we might need a way
        // to query "magnitude".
        // OR, we apply each separately to base, see which gives highest Attack, then
        // use that one.
        // This is a robust way if we assume they only affect attack.

        List<Ability> allAbilities = new ArrayList<>(abilities);
        List<Ability> attackAuras = allAbilities.stream()
                .filter(a -> a.category() == BuffCategory.ATTACK_AURA)
                .collect(Collectors.toList());

        Ability bestAura = null;
        double bestAttack = -1.0;

        if (!attackAuras.isEmpty()) {
            for (Ability a : attackAuras) {
                // Test apply
                StatContext test = a.modify(new StatContext(baseStats));
                if (test.attack > bestAttack) {
                    bestAttack = test.attack;
                    bestAura = a;
                }
            }
        }

        for (Ability a : allAbilities) {
            if (a.category() == BuffCategory.ATTACK_AURA) {
                if (a == bestAura) {
                    current = a.modify(current);
                }
                // Else skip
            } else {
                current = a.modify(current);
            }
        }
        return current;
    }

    @Override
    public String toString() {
        // Expected output format: "shield: 200, buffs:AURA_ATK_UP"
        // Buffs list should probably be populated by abilities that are 'Buffs'.
        String buffList = abilities.stream()
                .map(a -> a.category().name()) // Or ID? Example says AURA_ATK_UP.
                .distinct()
                .collect(Collectors.joining(","));

        return String.format("shield: %d, buffs:%s", shield, buffList);
    }
}
