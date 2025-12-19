package game;

public interface Ability {
    String id();

    BuffCategory category();

    void onAttach(Entity self, GameContext ctx);

    void onDetach(Entity self, GameContext ctx);

    StatContext modify(StatContext stats);
}
