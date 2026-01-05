package game;

import java.util.ArrayList;
import java.util.List;

/**
 * 區域服務接口
 *
 * 負責空間查詢邏輯，計算實體間的位置和距離關係。
 * 在光環和其他範圍技能中被使用，以判斷誰能受到效果。
 *
 * 在實際遊戲中應考慮：
 * - 3D/2D 座標系統
 * - 隊伍/陣營關係
 * - 視野遮擋和地形障礙
 * - 性能優化（四叉樹、空間分割）
 */
public interface AreaService {
    /**
     * 查找指定範圍內的盟友
     *
     * 查詢以 self 為中心、半徑為 radius 的球形區域內的所有盟友實體。
     * 
     * @param self 查詢的中心實體
     * @param radius 搜索半徑（距離單位）
     * @return 範圍內的盟友實體列表，不包括 self 本身
     */
    List<Entity> alliesWithin(Entity self, double radius);

    /**
     * 模擬區域服務實現
     *
     * 用於測試和演示目的的簡單實現。
     * 假設所有實體都是盟友且都在範圍內（忽略實際座標和隊伍關係）。
     */
    class MockAreaService implements AreaService {
        private final List<Entity> allEntities;

        /**
         * 構造函數
         * 
         * @param allEntities 遊戲中的所有實體列表
         */
        public MockAreaService(List<Entity> allEntities) {
            this.allEntities = allEntities;
        }

        @Override
        public List<Entity> alliesWithin(Entity self, double radius) {
            // 簡單模擬實現：假設除了自己以外的所有實體都是「盟友」且「在範圍內」
            // 在真實遊戲中，這裡會：
            // 1. 查詢座標系統，找出距離在 radius 內的實體
            // 2. 檢查隊伍關係，過濾出隊友
            // 3. 檢查視野關係，排除被遮擋的實體
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
