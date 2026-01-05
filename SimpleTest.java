/**
 * 簡易功能測試類
 * 驗證核心遊戲系統是否正常運作
 */
public class SimpleTest {
    public static void main(String[] args) {
        System.out.println("=== RuneRise System Test Suite ===\n");
        
        // 測試 1: 事件總線
        testEventBus();
        
        // 測試 2: 屬性系統
        testStats();
        
        // 測試 3: 冷卻系統
        testCooldown();
        
        // 測試 4: 傷害計算
        testDamageCalculation();
        
        // 測試 5: 實體系統
        testEntity();
        
        System.out.println("\n✅ All tests completed successfully!");
    }
    
    private static void testEventBus() {
        System.out.println("Test 1: Event Bus System");
        game.GameEventBus bus = new game.GameEventBus.SimpleGameEventBus();
        
        final boolean[] eventFired = {false};
        
        bus.subscribe(game.Events.Tick.class, event -> {
            eventFired[0] = true;
            System.out.println("  ✓ Event received: Tick");
        });
        
        bus.publish(new game.Events.Tick());
        assert eventFired[0] : "Event was not fired";
        System.out.println("  ✓ Event bus working correctly\n");
    }
    
    private static void testStats() {
        System.out.println("Test 2: Attribute System (StatContext)");
        game.StatContext stats = new game.StatContext();
        stats.attack = 100;
        stats.defense = 50;
        
        System.out.println("  ✓ Created stats: Attack=" + stats.attack + ", Defense=" + stats.defense);
        
        game.StatContext copy = new game.StatContext(stats);
        assert copy.attack == stats.attack : "Copy constructor failed";
        assert copy.defense == stats.defense : "Copy constructor failed";
        System.out.println("  ✓ Copy constructor working correctly\n");
    }
    
    private static void testCooldown() {
        System.out.println("Test 3: Cooldown System");
        game.CooldownPolicy cd = new game.CooldownPolicy(1000);  // 1秒冷卻
        game.BattleLog log = new game.BattleLog();
        game.Entity dummy = new game.Entity(log, 1000, game.Element.FIRE);
        
        assert cd.ready(dummy) : "Should be ready initially";
        System.out.println("  ✓ Cooldown ready on first check");
        
        cd.consume(dummy);
        assert !cd.ready(dummy) : "Should not be ready after consume";
        System.out.println("  ✓ Cooldown triggered after consume");
        
        // 設置為過去時間，模擬冷卻完成
        cd.setLastTriggerTime(System.currentTimeMillis() - 2000);
        assert cd.ready(dummy) : "Should be ready after time passes";
        System.out.println("  ✓ Cooldown ready after time passes\n");
    }
    
    private static void testDamageCalculation() {
        System.out.println("Test 4: Damage Calculation");
        game.BattleLog log = new game.BattleLog();
        game.Entity attacker = new game.Entity(log, 1000, game.Element.FIRE);
        game.Entity target = new game.Entity(log, 1000, game.Element.WOOD);
        
        // 無法直接設置基礎屬性，使用默認值
        int damage = game.DamageCalculator.calculateDamage(attacker, target);
        System.out.println("  ✓ Calculated damage: " + damage);
        assert damage > 0 : "Damage should be positive";
        System.out.println("  ✓ Damage calculation working correctly\n");
    }
    
    private static void testEntity() {
        System.out.println("Test 5: Entity System");
        game.BattleLog log = new game.BattleLog();
        game.Entity entity = new game.Entity(log, 1000, game.Element.FIRE);
        
        System.out.println("  ✓ Created entity with 1000 HP (Element: " + entity.element.getName() + ")");
        
        // 測試護盾
        entity.addShield(50);
        assert entity.shield == 50 : "Shield not added correctly";
        System.out.println("  ✓ Shield system working: " + entity.shield);
        
        // 測試傷害
        entity.takeDamage(30);
        assert entity.shield == 20 : "Shield should absorb damage";
        System.out.println("  ✓ Damage absorption working: shield=" + entity.shield);
        
        // 測試元素系統
        game.Element fire = game.Element.FIRE;
        game.Element wood = game.Element.WOOD;
        double ratio = fire.getCounterMultiplier(wood);
        System.out.println("  ✓ Element multiplier (FIRE vs WOOD): " + ratio);
        assert ratio > 1.0 : "Fire should have advantage over wood";
        System.out.println("\n");
    }
}
