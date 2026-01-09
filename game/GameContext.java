package game;

/**
 * 遊戲上下文類
 * 封裝了遊戲執行時所需的核心服務和狀態
 * 包括事件總線、區域服務和遊戲模式
 */
public class GameContext {
    /** 事件總線，用於發布和訂閱遊戲事件 */
    public final GameEventBus bus;
    
    /** 區域服務，用於查詢實體之間的空間關係 */
    public final AreaService area;
    
    /** 遊戲模式（PVE 或 PVP），可動態調整 */
    public Mode mode;

    /**
     * 構造函數
     * @param bus 事件總線
     * @param area 區域服務
     * @param mode 遊戲模式
     */
    public GameContext(GameEventBus bus, AreaService area, Mode mode) {
        this.bus = bus;
        this.area = area;
        this.mode = mode;
    }
}
