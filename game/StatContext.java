package game;

import java.util.HashMap;
import java.util.Map;

/**
 * 屬性上下文類
 *
 * 儲存實體的各項屬性值，作為屬性計算的基本單位。
 * 支持複製構造以方便進行屬性修改計算（函數式編程風格）。
 *
 * 預設屬性值：
 * - 攻擊力：100.0
 * - 防禦力：0.0
 *
 * 使用模式：
 * - 創建副本進行修改，保持原始對象不變
 * - 在技能的 modify() 方法中使用
 * - 屬於不可變風格的數據結構
 */
public class StatContext {
    /** 攻擊力屬性：決定造成傷害的基礎數值 */
    public double attack;
    
    /** 防禦力屬性：減少受到的傷害 */
    public double defense;

    /**
     * 預設構造函數
     * 
     * 初始化為遊戲預設的屬性值：
     * - 攻擊力 100.0
     * - 防禦力 0.0
     */
    public StatContext() {
        this.attack = 100.0;  // 預設基礎攻擊力
        this.defense = 0.0;   // 預設基礎防禦力
    }

    /**
     * 複製構造函數
     * 
     * 創建一個屬性副本，用於在修改者模式中創建新的屬性對象。
     * 這樣可以在函數式風格的運算中保持原始對象不變。
     * 
     * @param other 要複製的屬性上下文
     */
    public StatContext(StatContext other) {
        this.attack = other.attack;
        this.defense = other.defense;
    }
}
