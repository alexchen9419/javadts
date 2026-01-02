package game;

import java.util.HashMap;
import java.util.Map;

/**
 * 屬性上下文類
 * 儲存實體的各項屬性值
 * 包括攻擊力、防禦力等
 * 支持複製構造以方便進行屬性修改計算
 */
public class StatContext {
    /** 攻擊力屬性 */
    public double attack;
    
    /** 防禦力屬性 */
    public double defense;

    /**
     * 預設構造函數
     * 初始化為預設屬性值
     */
    public StatContext() {
        this.attack = 100.0;  // 預設基礎攻擊力
        this.defense = 0.0;   // 預設基礎防禦力
    }

    /**
     * 複製構造函數
     * 用於創建屬性的副本，方便進行修改者運算
     * @param other 要複製的屬性上下文
     */
    public StatContext(StatContext other) {
        this.attack = other.attack;
        this.defense = other.defense;
    }
}
