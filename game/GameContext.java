package game;

public class GameContext {
    public final GameEventBus bus;
    public final AreaService area;
    public Mode mode; // Mutable as per requirement (switch mode)

    public GameContext(GameEventBus bus, AreaService area, Mode mode) {
        this.bus = bus;
        this.area = area;
        this.mode = mode;
    }
}
