/**
 * 主程式類別
 * 用於展示 RuneRise 技能系統的完整功能
 * 包含多個測試場景：暴擊護盾、光環效果、互斥機制、PVP模式限制等
 */
import game.*;
import game.abilities.*;
import java.util.Arrays;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== RuneRise Ability System Demo ===\n");

        // --- 核心系統設置 ---
        // 創建事件總線，用於處理遊戲事件的發布和訂閱
        GameEventBus bus = new GameEventBus.SimpleGameEventBus();
        // 創建全局實體列表，用於追蹤所有遊戲實體
        final java.util.List<Entity> globalEntities = new ArrayList<>();
        // 創建區域服務的模擬實現，用於判斷實體之間的距離關係
        AreaService area = new AreaService.MockAreaService(globalEntities);
        // 創建遊戲上下文，封裝事件總線、區域服務和遊戲模式
        GameContext ctx = new GameContext(bus, area, Mode.PVE);

        // --- 場景 1: 暴擊觸發護盾（無冷卻時間）---
        System.out.println("--- Scenario 1: Crit -> Shield (No CD) ---");
        // 創建戰鬥日誌記錄器
        BattleLog p1Log = new BattleLog();
        // 創建玩家1實體
        Entity p1 = new Entity(p1Log);
        globalEntities.add(p1);

        // 使用 0ms 冷卻時間來測試「無冷卻」情況
        Ability critShieldMsg = new CritShieldAbility("CRIT_SHIELD_NO_CD", 100, 0);
        p1.addAbility(critShieldMsg);
        critShieldMsg.onAttach(p1, ctx);

        // 觸發兩次暴擊事件
        System.out.println("Triggering OnCrit 1...");
        bus.publish(new Events.OnCrit(p1));
        System.out.println("Triggering OnCrit 2...");
        bus.publish(new Events.OnCrit(p1));

        // 輸出戰鬥日誌和結果
        p1Log.print();
        System.out.println("P1 Shield: " + p1.shield); // 預期結果：200
        System.out.println("P1 Stats: " + p1.toString());

        // --- 場景 2: 暴擊觸發護盾（有冷卻時間）---
        System.out.println("\n--- Scenario 2: Crit -> Shield (With CD 10s) ---");
        // 創建玩家2的戰鬥日誌
        BattleLog p2Log = new BattleLog();
        // 創建玩家2實體
        Entity p2 = new Entity(p2Log);
        globalEntities.add(p2);

        // 創建有 10 秒冷卻時間的暴擊護盾技能
        Ability critShieldCD = new CritShieldAbility("CRIT_SHIELD_CD", 100, 10000); // 10秒
        p2.addAbility(critShieldCD);
        critShieldCD.onAttach(p2, ctx);

        System.out.println("Triggering OnCrit 1...");
        bus.publish(new Events.OnCrit(p2)); // 應該觸發成功
        System.out.println("Triggering OnCrit 2 (Immediate)...");
        bus.publish(new Events.OnCrit(p2)); // 應該因冷卻時間而失敗

        // 輸出戰鬥日誌和結果
        p2Log.print();
        System.out.println("P2 Shield: " + p2.shield); // 預期結果：100

        // --- 場景 3: 光環邏輯 ---
        System.out.println("\n--- Scenario 3: Aura Logic ---");
        // 創建光環持有者和隊友實體（玩家持有光環，隊友在附近）
        Entity auraHolder = new Entity(new BattleLog());
        Entity teammate = new Entity(new BattleLog());
        globalEntities.add(auraHolder);
        globalEntities.add(teammate);

        // 創建攻擊光環技能，提升 10% 攻擊力，作用範圍 5.0 單位
        Ability atkAura = new AttackAuraAbility("AURA_ATK_UP", 1.10, 5.0); // +10%
        auraHolder.addAbility(atkAura);
        atkAura.onAttach(auraHolder, ctx);

        // Tick 事件觸發前的攻擊力
        System.out.println("Teammate Attack before: " + teammate.getFinalStats().attack);

        // 觸發 Tick 事件，光環效果會在此時應用
        System.out.println("Triggering Tick...");
        bus.publish(new Events.Tick());

        // Tick 事件觸發後的攻擊力
        System.out.println("Teammate Attack after: " + teammate.getFinalStats().attack); // 預期結果：110
        System.out.println("Teammate Log:");
        teammate.log.print();

        // --- 場景 4: 互斥機制 ---
        System.out.println("\n--- Scenario 4: Mutual Exclusion ---");
        // 隊友接收到另一個更強的光環效果
        // 模擬方式：新增另一個光環持有者
        Entity auraHolder2 = new Entity(new BattleLog());
        globalEntities.add(auraHolder2);

        // 創建更強的攻擊光環，提升 15% 攻擊力
        Ability strongerAura = new AttackAuraAbility("AURA_STRONG", 1.15, 5.0); // +15%
        auraHolder2.addAbility(strongerAura);
        strongerAura.onAttach(auraHolder2, ctx);

        // 觸發 Tick，兩個光環效果都會嘗試應用
        System.out.println("Triggering Tick (Both Auras)...");
        bus.publish(new Events.Tick());

        // 隊友的技能列表中會有兩個光環效果，但 getFinalStats 應該只選擇最強的
        StatContext finalStats = teammate.getFinalStats();
        System.out.println("Teammate Ability Count: " + teammate.abilities.size()); // 應該有 2 個效果
        System.out.println("Teammate Attack Final: " + finalStats.attack); // 預期結果：115 (基礎 100 * 1.15)
        // 驗證不是 100 * 1.10 * 1.15 或其他錯誤計算

        // --- 場景 5: PVP 模式上限 ---
        System.out.println("\n--- Scenario 5: PVP Mode Cap ---");
        // 切換到 PVP 模式
        ctx.mode = Mode.PVP;
        System.out.println("Switched to PVP Mode.");

        // 在 PVP 模式中，光環效果有 5% 的上限
        // 所以 15% 應該被限制為 5%，10% 也應該被限制為 5%
        // 最終解析的最大值應該是 5%
        StatContext pvpStats = teammate.getFinalStats();
        System.out.println("Teammate Attack PVP: " + pvpStats.attack); // 預期結果：105.0

        System.out.println("\n=== verification complete ===");
    }
}
