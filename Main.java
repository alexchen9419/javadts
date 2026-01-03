
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

        // --- GUI 啟動 ---
        System.out.println("Starting GUI...");

        // 準備初始狀態
        // P1: 火屬性, 1500 HP
        BattleLog log1 = new BattleLog();
        Entity p1GUI = new Entity(log1, 1500, Element.FIRE);
        globalEntities.add(p1GUI);

        // P1 初始能力: 暴擊護盾
        Ability p1Shield = new CritShieldAbility("P1_Shield", 200, 5000);
        p1GUI.addAbility(p1Shield);
        p1Shield.onAttach(p1GUI, ctx);

        // P2: 木屬性, 2000 HP (火剋木)
        BattleLog log2 = new BattleLog();
        Entity p2GUI = new Entity(log2, 2000, Element.WOOD);
        globalEntities.add(p2GUI);

        // 在 EDT 中啟動窗口
        javax.swing.SwingUtilities.invokeLater(() -> {
            gui.GameWindow window = new gui.GameWindow(ctx, p1GUI, p2GUI, log1, log2);
            window.setVisible(true);
        });
    }
}
