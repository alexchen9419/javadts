# RuneRise 遊戲能力系統 (Game Ability System)

這是一個基於 **事件驅動 (Event-Driven)** 與 **組合模式 (Composition)** 的遊戲能力系統展示專案。本專案展示了如何構建可擴展的 ARPG 戰鬥系統，並包含完整的 GUI 圖形介面。

## ✨ 專案特色

*   **圖形化使用介面 (Swing GUI)**: 
    *   直觀顯示角色狀態 (HP, 護盾, Buff, 屬性)。
    *   主要操作面板 (攻擊, 暴擊, 模式切換)。
    *   即時戰鬥日誌 (Battle Log)。
*   **元素剋制系統 (Element System)**:
    *   實作 **火 (Fire) > 木 (Wood) > 水 (Water) > 火 (Fire)** 的剋制循環。
    *   屬性剋制時造成 **1.5倍** 傷害。
*   **靈活的能力系統 (Ability System)**:
    *   **暴擊護盾**: 暴擊時自動增加護盾 (支援冷卻時間)。
    *   **攻擊光環**: 為範圍內隊友提供攻擊加成。
    *   **PVE/PVP 雙模式**: 支援動態切換遊戲模式，影響技能效果 (如 PVP 模式下光環上限 5%)。
    *   **互斥機制**: 自動處理同類型 Buff 的堆疊衝突，只取最強效果。

## 🛠️ 系統需求

*   Java Development Kit (JDK) 8 或更高版本。

## 🚀 如何執行

### 1. 編譯專案

請在專案根目錄下打開終端機 (Terminal/CMD)，執行以下指令：

```bash
javac -encoding UTF-8 -d bin Main.java game/*.java game/abilities/*.java gui/*.java
```

### 2. 啟動程式

編譯成功後，執行 Main 類別啟動 GUI 介面：

```bash
java -cp bin Main
```

## 🎮 操作說明

程式啟動後會顯示兩個角色：
*   **Player 1 (Fire)**: 火屬性戰士，初始擁有「暴擊護盾」能力。
*   **Player 2 (Wood)**: 木屬性遊俠。

你可以透過下方按鈕進行測試：
1.  **P1 Attack P2**: 測試元素剋制 (火剋木 -> 1.5倍傷害) 與護盾抵擋機制。
2.  **Trigger P1 Crit**: 觸發暴擊事件，驗證護盾增加與冷卻時間。
3.  **Setup P2 Aura**: 讓 P2 開啟光環，測試 Buff 施加與互斥。
4.  **Switch Mode**: 切換 PVP/PVE 模式，觀察數值變化 (光環效果是否被壓制)。

## 📂 專案結構

```
src/
├── Main.java               # 程式入口
├── gui/                    #圖形介面
│   ├── GameWindow.java     # 主視窗
│   └── EntityPanel.java    # 角色狀態面板
└── game/                   # 核心邏輯
    ├── Ability.java        # 能力介面
    ├── Element.java        # 元素枚舉 (Fire, Water, Wood)
    ├── Entity.java         # 角色實體 (HP, Shield, Stats)
    ├── DamageCalculator.java # 傷害計算 (含元素剋制)
    └── abilities/          # 具體能力實作
        ├── CritShieldAbility.java
        └── AttackAuraAbility.java
```

詳細技術文件請參閱 `程式詳細說明文件.md`。
