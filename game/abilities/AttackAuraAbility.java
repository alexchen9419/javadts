package game.abilities;

import game.*;
import java.util.List;

/**
 * 攻擊光環技能
 * 為範圍內的盟友提供攻擊力加成
 * 在每次 Tick 事件時，檢查範圍內的盟友並應用光環效果
 * 
 * 特殊機制：
 * - PVP 模式下，光環效果有 5% 的上限
 * - 多個攻擊光環效果會互斥，只有最強的那個會生效
 */
public class AttackAuraAbility implements Ability {
    /** 技能唯一識別碼 */
    private final String id;
    /** 攻擊力乘數（如 1.10 表示 +10%） */
    private final double multiplier;
    /** 光環作用範圍（半徑） */
    private final double radius;
    /** 遊戲上下文 */
    private GameContext ctx;

    /**
     * 構造函數
     * @param id 技能 ID
     * @param multiplier 攻擊力乘數（如 1.10 表示 +10%）
     * @param radius 光環作用範圍
     */
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
        // 此技能本身是光環源，分類為 OTHER
        // 實際的光環效果由 AuraEffectAbility 提供
        return BuffCategory.OTHER;
    }

    @Override
    public void onAttach(Entity self, GameContext ctx) {
        this.ctx = ctx;
        // 訂閱 Tick 事件，每次 Tick 時檢查範圍內的盟友並應用光環效果
        ctx.bus.subscribe(Events.Tick.class, event -> {
            // 邏輯：查找範圍內的盟友，為他們應用光環效果
            List<Entity> allies = ctx.area.alliesWithin(self, radius);
            for (Entity ally : allies) {
                // 檢查盟友是否已經有來自此源的效果
                // 為了簡化，我們假設如果缺少就添加，如果已存在則不做任何事
                // 效果的唯一 ID："EFFECT_" + 技能 ID
                String effectId = "EFFECT_" + id;
                boolean hasIt = ally.abilities.stream().anyMatch(a -> a.id().equals(effectId));

                if (!hasIt) {
                    // 創建並添加光環效果
                    AuraEffectAbility effect = new AuraEffectAbility(effectId, multiplier);
                    ally.addAbility(effect);
                    // 手動調用 onAttach，因為 Entity.addAbility 不會自動調用
                    effect.onAttach(ally, ctx);

                    // 記錄日誌：光環效果的應用
                    ally.log.add(new LogEntry(0, "Tick", id, "Applied " + effectId));
                }
            }
        });
    }

    @Override
    public void onDetach(Entity self, GameContext ctx) {
        // 由於簡單實現中不容易取消訂閱，這裡留空
    }

    @Override
    public StatContext modify(StatContext stats) {
        // 此技能本身不修改屬性，只是控制光環效果的發放
        return stats;
    }

    /**
     * 光環效果內部類
     * 代表應用在實體上的實際光環效果
     * 會修改實體的攻擊力，並在 PVP 模式下限制效果上限
     */
    public static class AuraEffectAbility implements Ability {
        /** 效果唯一識別碼 */
        private final String id;
        /** 基礎攻擊力乘數 */
        private final double baseMultiplier;
        /** 遊戲上下文 */
        private GameContext ctx;

        /**
         * 構造函數
         * @param id 效果 ID
         * @param multiplier 攻擊力乘數
         */
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
            // 此效果分類為 ATTACK_AURA，會參與互斥比較
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
            // 計算實際的攻擊力乘數
            double rate = baseMultiplier;
            if (ctx.mode == Mode.PVP) {
                // PVP 模式限制：光環效果上限 5%（即乘數 1.05）
                // 假設 baseMultiplier 的格式像 1.10（代表 +10%）
                if (rate > 1.05) {
                    rate = 1.05;
                }
            }

            // 使用修飾器應用攻擊力乘數
            MyModifier mod = new MyModifier();
            mod.attackMul(rate);
            return mod.apply(stats);
        }
    }
}
