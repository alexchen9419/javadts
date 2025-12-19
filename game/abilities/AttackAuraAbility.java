package game.abilities;

import game.*;
import java.util.List;

public class AttackAuraAbility implements Ability {
    private final String id;
    private final double multiplier; // e.g. 1.10 for +10%
    private final double radius;
    private GameContext ctx;

    public AttackAuraAbility(String id, double multiplier, double radius) {
        this.id = id;
        this.multiplier = multiplier;
        this.radius = radius;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public BuffCategory category() {
        return BuffCategory.OTHER;
    } // This is the source, not the buff itself

    @Override
    public void onAttach(Entity self, GameContext ctx) {
        this.ctx = ctx;
        ctx.bus.subscribe(Events.Tick.class, event -> {
            // Logic: Find allies, apply AuraEffect
            List<Entity> allies = ctx.area.alliesWithin(self, radius);
            for (Entity ally : allies) {
                // Check if ally already has this effect from THIS source?
                // Or just add/ensure it exists.
                // For simplicity, we assume we just add it if missing, or do nothing if
                // present.
                // We need a unique ID for the effect: "EFFECT_" + id
                String effectId = "EFFECT_" + id;
                boolean hasIt = ally.abilities.stream().anyMatch(a -> a.id().equals(effectId));

                if (!hasIt) {
                    AuraEffectAbility effect = new AuraEffectAbility(effectId, multiplier);
                    ally.addAbility(effect);
                    // Manually call onAttach because Entity.addAbility doesn't do it in our
                    // simplified Entity
                    effect.onAttach(ally, ctx);

                    // Log? "BattlLog: ... 何時套光環"
                    ally.log.add(new LogEntry(0, "Tick", id, "Applied " + effectId));
                }
            }
        });
    }

    @Override
    public void onDetach(Entity self, GameContext ctx) {
    }

    @Override
    public StatContext modify(StatContext stats) {
        return stats;
    }

    // The Effect Class
    public static class AuraEffectAbility implements Ability {
        private final String id;
        private final double baseMultiplier;
        private GameContext ctx;

        public AuraEffectAbility(String id, double multiplier) {
            this.id = id;
            this.baseMultiplier = multiplier;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public BuffCategory category() {
            return BuffCategory.ATTACK_AURA;
        }

        @Override
        public void onAttach(Entity self, GameContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onDetach(Entity self, GameContext ctx) {
        }

        @Override
        public StatContext modify(StatContext stats) {
            // Logic: PVP Cap
            double rate = baseMultiplier;
            if (ctx.mode == Mode.PVP) {
                // "PVP 模式（光環上限 5% -> 1.05）"
                // Assuming baseMultiplier is like 1.10.
                if (rate > 1.05) {
                    rate = 1.05;
                }
            }

            MyModifier mod = new MyModifier();
            mod.attackMul(rate);
            return mod.apply(stats);
        }
    }
}
