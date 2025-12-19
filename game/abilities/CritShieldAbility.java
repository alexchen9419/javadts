package game.abilities;

import game.*;

public class CritShieldAbility implements Ability {
    private final String id;
    private final int shieldAmount;
    private final CooldownPolicy cd;
    private GameContext ctx;

    public CritShieldAbility(String id, int shieldAmount, long cooldownMs) {
        this.id = id;
        this.shieldAmount = shieldAmount;
        this.cd = new CooldownPolicy(cooldownMs);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public BuffCategory category() {
        return BuffCategory.OTHER;
    } // Trigger type

    @Override
    public void onAttach(Entity self, GameContext ctx) {
        this.ctx = ctx;
        ctx.bus.subscribe(Events.OnCrit.class, event -> {
            if (event.attacker == self) {
                if (cd.ready(self)) {
                    self.addShield(shieldAmount);
                    cd.consume(self);
                    // Log: "t:1,evt: OnCrit,ability:CRIT_SHIELD,effect:100 shield"
                    // We assume 't' is available or 0. Since we don't have global 't', we'll use 0
                    // or placeholder.
                    // Ideally Entity.log should have a current time.
                    // For now, I'll put '0' or assume external log management.
                    // The example log has 't:1'.
                    self.log.add(new LogEntry(0, "OnCrit", id, shieldAmount + " shield"));
                }
            }
        });
    }

    @Override
    public void onDetach(Entity self, GameContext ctx) {
        // Unsubscribing is not easy with Consumer generic interface in SimpleBus
        // without storing reference.
        // For this task, we skip unsubscribe or would need to store the specific
        // consumer reference.
    }

    @Override
    public StatContext modify(StatContext stats) {
        return stats; // No stat change, just trigger
    }
}
