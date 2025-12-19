package game;

import java.util.ArrayList;
import java.util.List;

public interface AreaService {
    List<Entity> alliesWithin(Entity self, double radius);

    class MockAreaService implements AreaService {
        private final List<Entity> allEntities;

        public MockAreaService(List<Entity> allEntities) {
            this.allEntities = allEntities;
        }

        @Override
        public List<Entity> alliesWithin(Entity self, double radius) {
            // For this simple mock, we assume everyone in the provided list is an "ally"
            // and "within range".
            // In a real game, this would check coordinates and team.
            // Requirement says: "Tick 時兩名隊友在 5m 內" -> we can just return all other entities
            // for simulation purposes
            // or we filter out 'self'.
            List<Entity> result = new ArrayList<>();
            for (Entity e : allEntities) {
                if (e != self) {
                    result.add(e);
                }
            }
            return result;
        }
    }
}
