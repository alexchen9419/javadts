package game.abilities;

import game.*;
import java.util.List;

/**
 * 攻擊光環技能
 *
 * 為範圍內的盟友提供攻擊力加成。光環每次 Tick 事件時檢查範圍內的盟友，
 * 並為他們應用攻擊力倍率修飾。
 *
 * 核心機制：
 * 1. 訂閱 Tick 事件，每次遊戲時刻都檢查一次
 * 2. 查詢範圍內的盟友，為其動態生成光環效果
 * 3. 在 PVP 模式下限制光環效果上限（最多 +5%）
 * 4. 多個攻擊光環的互斥機制由 Entity.getFinalStats() 實現
 *
 * 使用範例：
 * - AttackAura("AURA_KNIGHT", 1.10, 5.0) - +10% 攻擊，範圍 5 米
 * - 在 PVP 下自動限制為 +5%
 *
 * @author RuneRise Ability System
 */
public class AttackAuraAbility implements Ability {
    /** 技能唯一識別碼 */
    private final String id;
    
    /** 攻擊力乘數（如 1.10 表示 +10% 攻擊力） */
    private final double multiplier;
    
    /** 光環作用範圍（半徑，決定盟友是否能受到光環效果） */
    private final double radius;
    
    /** 遊戲上下文：用於訪問事件總線和區域服務 */
    private GameContext ctx;

    /**
     * 構造函數
     * @param id 技能的唯一識別碼
     * @param multiplier 攻擊力乘數（如 1.10 表示 +10%）
     * @param radius 光環作用範圍（距離單位）
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
        // 此實例是光環源，分類為 OTHER（不參與互斥比較）
        // 實際的光環效果由內部 AuraEffectAbility 提供
        return BuffCategory.OTHER;
    }

    @Override
    public void onAttach(Entity self, GameContext ctx) {
        this.ctx = ctx;
        // 訂閱遊戲 Tick 事件，每個時刻都檢查盟友
        ctx.bus.subscribe(Events.Tick.class, event -> {
            // 查找範圍內的所有盟友
            List<Entity> allies = ctx.area.alliesWithin(self, radius);
            for (Entity ally : allies) {
                // 檢查該盟友是否已經有來自此光環源的效果
                String effectId = "EFFECT_" + id;
                boolean hasEffect = ally.abilities.stream()
                        .anyMatch(a -> a.id().equals(effectId));

                if (!hasEffect) {
                    // 盟友還沒有此光環效果，創建並應用
                    AuraEffectAbility effect = new AuraEffectAbility(effectId, multiplier);
                    ally.addAbility(effect);
                    effect.onAttach(ally, ctx);  // 初始化效果

                    // 記錄到戰鬥日誌
                    ally.log.add(new LogEntry(0, "Tick", id, "Applied " + effectId));
                }
            }
        });
    }

    @Override
    public void onDetach(Entity self, GameContext ctx) {
        // 此簡單實現中無法取消訂閱，留空
    }

    @Override
    public StatContext modify(StatContext stats) {
        // 光環源本身不修改屬性，只控制光環效果的發放
        return stats;
    }

    /**
     * 光環效果內部類
     * 
     * 代表實際應用在盟友身上的光環效果。由光環源動態生成並附加到盟友。
     * 在 PVP 模式下會自動限制效果上限（最多 +5%）以平衡遊戲。
     *
     * 注意：此類分類為 ATTACK_AURA，會參與互斥機制的比較。
     * 效果 ID 通常為 "EFFECT_" + 光環源 ID。
     */
    public static class AuraEffectAbility implements Ability {
        /** 效果識別碼（通常為 "EFFECT_" + 光環源 ID） */
        private final String id;
        
        /** 基礎攻擊力乘數（未經 PVP 限制） */
        private final double baseMultiplier;
        
        /** 遊戲上下文：用於判斷當前遊戲模式 */
        private GameContext ctx;

        /**
         * 構造函數
         * @param id 效果識別碼
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
            // 光環效果移除時的清理邏輯（此實現中無需特別處理）
        }

        @Override
        public StatContext modify(StatContext stats) {
            // 計算實際應用的攻擊力乘數
            double effectiveMultiplier = baseMultiplier;
            
            // PVP 模式限制：光環效果最多 +5%（乘數 1.05）
            if (ctx != null && ctx.mode == Mode.PVP) {
                if (effectiveMultiplier > 1.05) {
                    effectiveMultiplier = 1.05;
                }
            }

            // 使用修飾器應用最終的攻擊力乘數
            MyModifier modifier = new MyModifier();
            modifier.attackMul(effectiveMultiplier);
            return modifier.apply(stats);
        }
    }
}
