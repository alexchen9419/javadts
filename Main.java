
/**
 * 主程式類別 - RuneRise 技能系統演示
 *
 * 此程序展示了遊戲引擎的核心功能：
 * <ul>
 *   <li>暴擊觸發護盾機制</li>
 *   <li>攻擊光環效果（帶範圍和乘數修改）</li>
 *   <li>多個光環的互斥比較機制</li>
 *   <li>PVP 模式下的效果限制（光環上限 5%）</li>
 *   <li>元素剋制系統（火剋木、木剋水、水剋火 - 1.5 倍傷害）</li>
 * </ul>
 *
 * 初始化步驟：
 * 1. 創建事件總線（遊戲系統通信樞紐）
 * 2. 創建區域服務（處理實體距離判定）
 * 3. 初始化 P1 和 P2 兩個角色及其技能
 * 4. 啟動 GUI 界面進行互動式演示
 */
import game.*;
import game.abilities.*;
import java.util.Arrays;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== RuneRise Ability System Demo ===\n");

        // ===== 核心系統設置 =====
        // 事件總線：發布-訂閱機制實現，使各遊戲系統解耦合
        GameEventBus bus = new GameEventBus.SimpleGameEventBus();
        
        // 全局實體列表：追蹤遊戲中的所有實體（玩家、敵人、環境物件等）
        final java.util.List<Entity> globalEntities = new ArrayList<>();
        
        // 區域服務：計算實體間的空間關係，決定光環範圍內的盟友
        AreaService area = new AreaService.MockAreaService(globalEntities);
        
        // 遊戲上下文：統一容器，持有事件總線、區域服務和當前遊戲模式
        GameContext ctx = new GameContext(bus, area, Mode.PVE);

        // ===== GUI 初始化與啟動 =====
        System.out.println("Initializing RuneRise GUI...");

        // 角色 P1：火屬性，1500 HP
        // - 火屬性對木屬性造成 1.5 倍傷害（元素剋制）
        BattleLog log1 = new BattleLog();
        Entity p1GUI = new Entity(log1, 1500, Element.FIRE);
        globalEntities.add(p1GUI);

        // 為 P1 配置「暴擊觸發護盾」技能
        // - 每次發動暴擊時增加 200 點護盾
        // - 冷卻時間 5000 毫秒（5 秒），防止頻繁觸發
        Ability p1Shield = new CritShieldAbility("P1_Shield", 200, 5000);
        p1GUI.addAbility(p1Shield);
        p1Shield.onAttach(p1GUI, ctx);  // 初始化技能並訂閱相關事件

        // 角色 P2：木屬性，2000 HP
        // - 木屬性被火屬性剋制，受火屬性攻擊時傷害加倍
        BattleLog log2 = new BattleLog();
        Entity p2GUI = new Entity(log2, 2000, Element.WOOD);
        globalEntities.add(p2GUI);

        // 在 Swing 事件分派線程（EDT）中啟動 GUI 窗口
        // 確保所有 UI 操作在正確的線程上執行
        javax.swing.SwingUtilities.invokeLater(() -> {
            gui.GameWindow window = new gui.GameWindow(ctx, p1GUI, p2GUI, log1, log2);
            window.setVisible(true);
        });
    }
}
