
import game.*;
import game.abilities.*;
import java.util.Arrays;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== RuneRise Ability System Demo ===\n");

        // --- Setup Core ---
        GameEventBus bus = new GameEventBus.SimpleGameEventBus();
        // We will set entities later for the mock area service
        final java.util.List<Entity> globalEntities = new ArrayList<>();
        AreaService area = new AreaService.MockAreaService(globalEntities);
        GameContext ctx = new GameContext(bus, area, Mode.PVE);

        // --- Scenario 1: Crit -> Shield (No CD) ---
        System.out.println("--- Scenario 1: Crit -> Shield (No CD) ---");
        BattleLog p1Log = new BattleLog();
        Entity p1 = new Entity(p1Log);
        globalEntities.add(p1);

        // Use 0ms CD for "No CD" test
        Ability critShieldMsg = new CritShieldAbility("CRIT_SHIELD_NO_CD", 100, 0);
        p1.addAbility(critShieldMsg);
        critShieldMsg.onAttach(p1, ctx);

        // Trigger Crit twice
        System.out.println("Triggering OnCrit 1...");
        bus.publish(new Events.OnCrit(p1));
        System.out.println("Triggering OnCrit 2...");
        bus.publish(new Events.OnCrit(p1));

        p1Log.print();
        System.out.println("P1 Shield: " + p1.shield); // Expect 200
        System.out.println("P1 Stats: " + p1.toString());

        // --- Scenario 2: Crit -> Shield (With CD) ---
        System.out.println("\n--- Scenario 2: Crit -> Shield (With CD 10s) ---");
        BattleLog p2Log = new BattleLog();
        Entity p2 = new Entity(p2Log);
        globalEntities.add(p2);

        Ability critShieldCD = new CritShieldAbility("CRIT_SHIELD_CD", 100, 10000); // 10s
        p2.addAbility(critShieldCD);
        critShieldCD.onAttach(p2, ctx);

        System.out.println("Triggering OnCrit 1...");
        bus.publish(new Events.OnCrit(p2)); // Should trigger
        System.out.println("Triggering OnCrit 2 (Immediate)...");
        bus.publish(new Events.OnCrit(p2)); // Should fail due to CD

        p2Log.print();
        System.out.println("P2 Shield: " + p2.shield); // Expect 100

        // --- Scenario 3: Aura logic ---
        System.out.println("\n--- Scenario 3: Aura Logic ---");
        // Player has Aura. Teammate is nearby.
        Entity auraHolder = new Entity(new BattleLog());
        Entity teammate = new Entity(new BattleLog());
        globalEntities.add(auraHolder);
        globalEntities.add(teammate);

        Ability atkAura = new AttackAuraAbility("AURA_ATK_UP", 1.10, 5.0); // +10%
        auraHolder.addAbility(atkAura);
        atkAura.onAttach(auraHolder, ctx);

        // Before Tick
        System.out.println("Teammate Attack before: " + teammate.getFinalStats().attack);

        System.out.println("Triggering Tick...");
        bus.publish(new Events.Tick());

        // After Tick
        System.out.println("Teammate Attack after: " + teammate.getFinalStats().attack); // Expect 110
        System.out.println("Teammate Log:");
        teammate.log.print();

        // --- Scenario 4: Mutual Exclusion ---
        System.out.println("\n--- Scenario 4: Mutual Exclusion ---");
        // Teammate receives another stronger aura
        // We simulate this by applying another aura effect to teammate manually
        // OR adhering to the system: Another holder comes in.
        Entity auraHolder2 = new Entity(new BattleLog());
        globalEntities.add(auraHolder2);

        Ability strongerAura = new AttackAuraAbility("AURA_STRONG", 1.15, 5.0); // +15%
        auraHolder2.addAbility(strongerAura);
        strongerAura.onAttach(auraHolder2, ctx);

        System.out.println("Triggering Tick (Both Auras)...");
        bus.publish(new Events.Tick());

        // Teammate should have both effects in list, but getFinalStats should pick
        // best.
        StatContext finalStats = teammate.getFinalStats();
        System.out.println("Teammate Ability Count: " + teammate.abilities.size()); // Should be 2 effects
        System.out.println("Teammate Attack Final: " + finalStats.attack); // Expect 115 (Base 100 * 1.15)
        // Verify it's not 100 * 1.10 * 1.15 or something else.

        // --- Scenario 5: PVP Mode Cap ---
        System.out.println("\n--- Scenario 5: PVP Mode Cap ---");
        ctx.mode = Mode.PVP;
        System.out.println("Switched to PVP Mode.");

        // In PVP, max aura is 5%.
        // So 15% should become 5%. 10% should become 5%.
        // Resolved max should be 5%.
        StatContext pvpStats = teammate.getFinalStats();
        System.out.println("Teammate Attack PVP: " + pvpStats.attack); // Expect 105.0

        System.out.println("\n=== verification complete ===");
    }
}
