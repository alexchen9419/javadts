package game;

import java.util.ArrayList;
import java.util.List;

/**
 * 區域服務接口
 * 用於查詢指定範圍內的盟友實體
 * 在實際遊戲中會考慮座標位置和隊伍關係
 */
public interface AreaService {
    /**
     * 查找指定範圍內的盟友
     * @param self 查詢的中心實體
     * @param radius 搜索半徑
     * @return 範圍內的盟友實體列表
     */
    List<Entity> alliesWithin(Entity self, double radius);

    /**
     * 模擬區域服務實現
     * 用於測試和演示目的
     * 假設所有實體都是盟友且都在範圍內
     */
    class MockAreaService implements AreaService {
        private final List<Entity> allEntities;

        /**
         * 構造函數
         * @param allEntities 遊戲中的所有實體列表
         */
        public MockAreaService(List<Entity> allEntities) {
            this.allEntities = allEntities;
        }

        @Override
        public List<Entity> alliesWithin(Entity self, double radius) {
            // 簡單的模擬實現，假設列表中除了自己以外的所有實體都是「盟友」且「在範圍內」
            // 在真實遊戲中，這裡會檢查座標位置和隊伍關係
            // 需求說明：「Tick 時兩名隊友在 5m 內」
            // 為了模擬目的，我們返回除自己以外的所有實體
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
