# RuneRise 遊戲能力系統詳細解析 (Game Ability System Deep Dive)

本專案實作了一個基於 **事件驅動 (Event-Driven)** 與 **組合模式 (Composition)** 的遊戲能力系統。以下是程式碼架構、核心機制與設計決策的詳細說明。

## 1. 系統架構 (Architecture)

系統依照 PlantUML 類別圖設計，將「能力」與「角色實體」解耦。

- **Entity (實體)**: 
  - 代表遊戲中的角色。它不繼承特定職業（如 Warrior, Mage），而是透過持有不同的 `Ability` 來定義行為。
  - 核心職責：管理狀態（Shield, Buffs）、計算最終屬性（StatContext）。
  
- **GameEventBus (事件匯流排)**: 
  - 負責傳遞遊戲事件（如 `OnCrit`, `Tick`）。
  - **解耦關鍵**: `Ability` 不需要知道是誰觸發了事件，只需訂閱感興趣的事件類型。

- **GameContext (遊戲情境)**:
  - 包含全域資訊：`EventBus`、`AreaService` (區域查詢)、`Mode` (PVP/PVE)。
  - 讓 Ability 在執行時能獲取環境資訊。

## 2. 核心類別說明 (Core Classes)

### `game` 套件
- **StatContext**: 屬性容器（如 `attack`, `defense`）。支援複製與修改，用於計算最終數值。
- **CooldownPolicy**: 封裝冷卻時間邏輯。
  - `ready(entity)`: 檢查是否冷卻完畢。
  - `consume(entity)`: 重置冷卻時間。
- **Events**: 定義遊戲發生的事件資料結構 (`OnCrit`, `OnDamageTaken`, `Tick`)。

### `game.abilities` 套件
- **CritShieldAbility**: 
  - 展示了 **Trigger (觸發型)** 能力。
  - 邏輯：監聽 `OnCrit` -> 檢查 CD -> 加盾 -> 寫 Log。
- **AttackAuraAbility**:
  - 展示了 **Aura (光環型)** 能力。
  - 邏輯：監聽 `Tick` -> 搜尋範圍內隊友 (`AreaService`) -> 為隊友添加 `AuraEffectAbility`。
  - **AuraEffectAbility**: 這是實際作用在隊友身上的 Buff，負責屬性加成與 PVP 判斷。

## 3. 關鍵機制詳解 (Key Mechanisms)

### (1) 屬性計算與互斥 (Stat Calculation & Mutual Exclusion)
這是系統中最複雜的部分，位於 `Entity.getFinalStats()`。

- **需求**: 多個攻擊光環同時存在時，只取數值最大者（互斥），其他類型的 Buff 則可疊加。
- **實作邏輯**:
  1. 系統先將所有能力分類。
  2. 針對 `BuffCategory.ATTACK_AURA` 類型的能力，進行由 `bestAura` 變數控制的 **預先模擬 (Dry Run)**：
     - 對每個光環試算一次 `modify()`。
     - 找出攻擊力加成最高的那個光環實例。
  3. 正式計算屬性時，遍歷所有能力：
     - 若是攻擊光環且**不是**最強的那個 -> **跳過 (不套用)**。
     - 若是其他能力 -> **套用 (Apply)**。
  這確保了同類光環不疊加，但不同類能力（如基礎屬性、藥水）可疊加。

### (2) PVP 模式動態調整 (Mode-Specific logic)
位於 `AttackAuraAbility.AuraEffectAbility.modify()`。

- **需求**: 在 PVP 模式下，光環效果上限為 5%。
- **實作**:
  - 在執行屬性修飾前，檢查 `ctx.mode`。
  - 若是 `Mode.PVP`，強制將倍率 (Multiplier) 限制在 `1.05` 以下。
  - 若是 `Mode.PVE`，則使用原始倍率 (如 `1.15`)。
  - 這種設計讓數值平衡邏輯封裝在能力內部，外部系統無需干涉。

### (3) 時間與冷卻 (Time & Cooldown)
位於 `CooldownPolicy` 與 `CritShieldAbility`。

- 使用 `System.currentTimeMillis()` 進行簡單的時間追蹤。
- 當 `OnCrit` 發生時，若 `cd.ready()` 回傳 `true`，才觸發效果並呼叫 `cd.consume()` 更新上次觸發時間。這完美解決了「連續暴擊不應連續加盾」的需求。

## 4. 執行流程範例 (Walkthrough)

當您執行 `Main.java` 時：

1. **情境 1 (無 CD)**: 兩次 `bus.publish(OnCrit)` -> 盾值增加 200。
2. **情境 2 (有 CD)**: 兩次 `bus.publish(OnCrit)` (間隔極短) -> 盾值只增加 100 (第二次被 `CooldownPolicy` 擋下)。
3. **情境 3 (光環)**: 發送 `Tick` -> `AttackAura` 找到隊友 -> 隊友獲得 Buff -> 攻擊力 100 變 110。
4. **情境 4 (互斥)**: 隊友獲得更強光環 (+15%) -> `getFinalStats` 比較 +10% 與 +15% -> 選擇 +15% -> 攻擊力變 115 (而非 125 或 126.5)。
5. **情境 5 (PVP)**: 切換 `ctx.mode = PVP` -> 再計算一次 -> +15% 被壓制為 +5% -> 攻擊力變 105。

---
此文件詳細解釋了程式碼的設計思路与運作原理，可作為後續開發或教學的參考。
