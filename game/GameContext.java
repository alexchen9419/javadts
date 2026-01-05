package game;

/**
 * 遊戲上下文類
 *
 * 統一的遊戲狀態容器，持有所有核心遊戲系統的參考。
 * 這樣各個系統和對象可以透過上下文訪問其他系統，而無需直接依賴。
 *
 * 包含：
 * - 事件總線：系統間的通信媒介
 * - 區域服務：空間查詢和距離計算
 * - 遊戲模式：控制遊戲規則和平衡性
 *
 * 使用場景：在技能的 onAttach() 中接收此上下文，並在需要時訪問其成員。
 */
public class GameContext {
    /** 事件總線：發布-訂閱機制實現，系統間通信的樞紐 */
    public final GameEventBus bus;
    
    /** 區域服務：處理空間查詢和距離判定 */
    public final AreaService area;
    
    /** 遊戲模式（PVE 或 PVP）：可動態調整以改變遊戲規則 */
    public Mode mode;

    /**
     * 構造函數
     * 
     * @param bus 事件總線實現
     * @param area 區域服務實現
     * @param mode 初始遊戲模式
     */
    public GameContext(GameEventBus bus, AreaService area, Mode mode) {
        this.bus = bus;
        this.area = area;
        this.mode = mode;
    }
}
