# RuneRise 遊戲能力系統

![Status](https://img.shields.io/badge/Build-Passing-brightgreen) ![Tests](https://img.shields.io/badge/Tests-5%2F5-brightgreen) ![Java](https://img.shields.io/badge/Java-21-blue) ![License](https://img.shields.io/badge/License-MIT-green)

> 一個完整的事件驅動 ARPG 戰鬥系統，展示高階物件導向設計和遊戲開發最佳實踐。

**最新版本**: v2.1 (2026-01-05)  
**編譯狀態**: ✅ 成功 (0 錯誤)  
**測試狀態**: ✅ 全部通過 (5/5)

---

## 🎯 專案概述

RuneRise 是一個基於 **事件驅動架構 (Event-Driven Architecture)** 和 **組合模式 (Composition Pattern)** 的完整遊戲能力系統。該專案演示如何使用現代 Java 特性和設計模式構建可擴展、易維護的遊戲機制。

### 核心特性

- ✅ **事件驅動架構**：解耦的發布-訂閱模式，支援動態事件通訊
- ✅ **動態能力系統**：支援技能的動態掛載與移除，無需修改核心代碼
- ✅ **元素剋制系統**：完整的火水木三角剋制關係，支援倍率計算
- ✅ **Buff 互斥機制**：自動處理同類型增益衝突，只取最強效果
- ✅ **冷卻時間管理**：精確的毫秒級時間追蹤系統
- ✅ **PVE/PVP 雙模式**：動態遊戲平衡，支援模式切換
- ✅ **完整 GUI 介面**：Swing 圖形化戰鬥介面，即時狀態更新
- ✅ **專業級註解**：超過 500 行 JavaDoc 文檔
- ✅ **完整測試覆蓋**：單元測試與功能測試全部通過

---

## 📋 快速開始

### 系統需求

| 項目 | 版本 |
|------|------|
| **Java** | 21+ (建議使用 JDK-21) |
| **OS** | Windows / macOS / Linux |
| **RAM** | 512MB+ |
| **顯示解析度** | 1280×720+ |

### 1️⃣ 編譯專案

在專案根目錄執行：

```bash
javac -encoding UTF-8 -d bin game/*.java game/*/*.java gui/*.java Main.java SimpleTest.java
```

**編譯參數說明**:
- `-encoding UTF-8`: 支援中文註解和字符
- `-d bin`: 指定輸出目錄

**預期結果**:
```
✅ 編譯成功
✅ 生成 26 個 class 檔案
✅ 0 編譯錯誤
```

### 2️⃣ 執行 GUI 程式

```bash
java -cp bin Main
```

程式將啟動 **RuneRise Game System** 圖形化介面。

### 3️⃣ 運行單元測試

```bash
java -cp bin SimpleTest
```

將執行 5 項核心功能測試並顯示結果。

---

## 🎮 使用指南

### GUI 介面說明

程式啟動後顯示兩個角色：

| 角色 | 元素 | 初始技能 | 說明 |
|------|------|---------|------|
| **Player 1** | 🔥 火 | 暴擊護盾 | 暴擊時增加護盾 (5s 冷卻) |
| **Player 2** | 🌲 木 | 無 | 可手動添加光環效果 |

### 可用操作按鈕

| 按鈕名稱 | 功能 | 預期效果 |
|---------|------|---------|
| **P1 Attack P2** | Player 1 攻擊 Player 2 | 計算傷害並應用元素倍率 |
| **Trigger P1 Crit** | 觸發 Player 1 暴擊事件 | 增加護盾 |
| **Setup P2 Aura** | 為 Player 2 啟用光環 | 應用 1.20x 攻擊加成 |
| **Switch Mode** | PVE ↔ PVP 模式 | PVP 模式光環上限 5% |
| **Game Tick** | 手動觸發時刻事件 | 刷新光環效果與狀態 |

---

## 📂 專案結構

```
javadts/
├── Main.java                          # 程式入口點 & 系統初始化
├── SimpleTest.java                    # 單元測試 (5 個測試用例)
├── README.md                          # 本文件
├── 程式詳細說明文件.md                 # 詳細技術文檔 (400+ 行)
├── TEST_REPORT.md                     # 完整測試報告
├── COMMENT_IMPROVEMENTS.md            # 代碼改善記錄
├── bin/                               # 編譯輸出目錄
├── game/                              # 核心遊戲邏輯 (13 個類)
│   ├── Ability.java                  # 技能介面
│   ├── Entity.java                   # 角色實體
│   ├── StatContext.java              # 屬性上下文
│   ├── Element.java                  # 元素系統
│   ├── DamageCalculator.java         # 傷害計算
│   ├── GameContext.java              # 服務定位器
│   ├── GameEventBus.java             # 事件匯流排
│   ├── Events.java                   # 事件定義
│   ├── CooldownPolicy.java           # 冷卻管理
│   ├── BattleLog.java                # 戰鬥日誌
│   ├── BuffCategory.java             # Buff 分類
│   ├── Mode.java                     # 遊戲模式
│   ├── MyModifier.java               # 屬性修飾器
│   ├── AreaService.java              # 範圍服務
│   └── abilities/                    # 具體技能實作
│       ├── CritShieldAbility.java   # 暴擊護盾
│       └── AttackAuraAbility.java   # 攻擊光環
├── gui/                               # 圖形使用介面 (2 個類)
│   ├── GameWindow.java               # 主視窗
│   └── EntityPanel.java              # 角色面板
└── topic/                             # 教材資源
    ├── topic.txt                     # 主題說明
    └── GameAbility_TeachingGuide.pdf # 教學指南
```

---

## ✅ 測試結果

### 編譯狀況

```
Java 版本: JDK-21
編譯結果: ✅ 成功
- 總檔案: 18 個 Java 原始檔
- 輸出: 26 個 class 檔案
- 錯誤: 0
- 警告: 0
```

### 功能測試 (5/5 通過)

| 測試項目 | 狀態 | 說明 |
|---------|------|------|
| **Event Bus** | ✅ | 事件發布-訂閱正常運作 |
| **Attribute System** | ✅ | 屬性複製和修改正常 |
| **Cooldown System** | ✅ | 毫秒級時間追蹤正確 |
| **Damage Calculation** | ✅ | 傷害公式和元素倍率正確 |
| **Entity System** | ✅ | 護盾吸收優先正確 |

詳細測試報告見 [TEST_REPORT.md](TEST_REPORT.md)

---

## 🔍 主要功能深入

### 1. 元素剋制系統

```
火 (Fire)
 ├─ 剋制 > 木 (Wood)  [1.5x 傷害]
 └─ 被剋制 < 水 (Water)

木 (Wood)
 ├─ 剋制 > 水 (Water)  [1.5x 傷害]
 └─ 被剋制 < 火 (Fire)

水 (Water)
 ├─ 剋制 > 火 (Fire)   [1.5x 傷害]
 └─ 被剋制 < 木 (Wood)
```

### 2. Buff 互斥機制

當角色同時受到多個同類型 Buff 時，系統自動選擇最強效果。

### 3. 冷卻時間系統

基於系統時鐘的精確時間管理，支援多個角色獨立計時。

### 4. PVP 平衡機制

在 PVP 模式下，所有光環效果自動限制為 5% (1.05x)。

---

## 📖 文檔資源

| 文件 | 用途 | 內容 |
|------|------|------|
| **程式詳細說明文件.md** | 完整技術文檔 | 架構、設計模式、代碼詳解 (400+ 行) |
| **TEST_REPORT.md** | 測試報告 | 編譯結果、測試用例、品質指標 |
| **COMMENT_IMPROVEMENTS.md** | 改善記錄 | 代碼註解和結構優化 |

---

## 🐛 已知問題與修復

### v2.1 修復清單 (2026-01-05)

所有 6 個主要檔案中的結構問題已修復，編譯成功，所有測試通過。

詳見：[程式詳細說明文件.md](程式詳細說明文件.md)

---

## 🎓 學習價值

本專案適合用於學習：

- ✅ 事件驅動架構設計
- ✅ Java 遊戲開發基礎
- ✅ 設計模式實踐應用
- ✅ GUI 編程 (Swing)
- ✅ 物件導向設計原則
- ✅ 遊戲平衡機制
- ✅ 單元測試編寫

---

## 📝 版本資訊

**v2.1 (2026-01-05)**
- ✅ 編譯成功 (0 錯誤，26 個 class)
- ✅ 功能測試全部通過 (5/5)
- ✅ 修復 6 個主要檔案結構問題

**v2.0 (2026-01-03)**
- 初始發布版本

---

## 📄 授權

MIT License - 自由使用和修改

---

**⭐ 如果本專案對您有幫助，請給個 Star！**
