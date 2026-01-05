package gui;

import game.*;
import game.abilities.*;
import javax.swing.*;
import java.awt.*;

/**
 * 遊戲主視窗
 *
 * 整合所有 GUI 組件，提供互動式的戰鬥模擬環境。
 * 允許使用者透過按鈕觸發各種遊戲事件，實時查看角色狀態變化。
 *
 * 界面佈局：
 * - 上方：兩個玩家的狀態面板（HP、護盾、屬性、Buff）
 * - 中央：戰鬥日誌區，顯示所有遊戲事件
 * - 下方：控制面板，包含攻擊、技能、模式切換等按鈕
 *
 * 功能：
 * - 執行角色攻擊，自動計算傷害和元素倍率
 * - 觸發暴擊事件以測試護盾技能
 * - 設置攻擊光環，觀察光環和互斥機制
 * - 切換 PVE/PVP 模式，觀察光環上限限制
 * - 手動觸發 Tick 事件以更新光環效果
 *
 * @author RuneRise UI System
 */
public class GameWindow extends JFrame {
    private final GameContext ctx;
    private final Entity p1;
    private final Entity p2;

    // UI 組件
    private EntityPanel p1Panel;
    private EntityPanel p2Panel;
    private JTextArea logArea;  // 戰鬥日誌顯示區

    public GameWindow(GameContext ctx, Entity p1, Entity p2, BattleLog p1Log, BattleLog p2Log) {
        this.ctx = ctx;
        this.p1 = p1;
        this.p2 = p2;

        setTitle("RuneRise Battle Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        // ===== 上方區域：角色狀態面板 =====
        JPanel playersPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        p1Panel = new EntityPanel("Player 1 (Fire)", p1);
        p2Panel = new EntityPanel("Player 2 (Wood)", p2);
        playersPanel.add(p1Panel);
        playersPanel.add(p2Panel);
        add(playersPanel, BorderLayout.NORTH);

        // ===== 中央區域：戰鬥日誌 =====
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Battle Log"));
        add(logScroll, BorderLayout.CENTER);

        // ===== 下方區域：控制面板 =====
        JPanel controlsPanel = new JPanel(new GridLayout(2, 3, 5, 5));

        // 按鈕：P1 攻擊 P2
        JButton atkP1Btn = new JButton("P1 Attack P2");
        atkP1Btn.addActionListener(e -> performAttack(p1, p2, "P1", "P2"));
        controlsPanel.add(atkP1Btn);

        // 按鈕：P2 攻擊 P1
        JButton atkP2Btn = new JButton("P2 Attack P1");
        atkP2Btn.addActionListener(e -> performAttack(p2, p1, "P2", "P1"));
        controlsPanel.add(atkP2Btn);

        // 按鈕：觸發 P1 暴擊（用於測試護盾技能）
        JButton critP1Btn = new JButton("Trigger P1 Crit");
        critP1Btn.addActionListener(e -> {
            ctx.bus.publish(new Events.OnCrit(p1));
            log("System: P1 triggered a critical strike event!");
            updateUI();
        });
        controlsPanel.add(critP1Btn);

        // 按鈕：為 P2 設置攻擊光環
        JButton auraP2Btn = new JButton("Setup P2 Aura (Atk +15%)");
        auraP2Btn.addActionListener(e -> {
            Ability aura = new AttackAuraAbility("AURA_P2", 1.15, 100.0);
            p2.addAbility(aura);
            aura.onAttach(p2, ctx);
            log("System: P2 activated Attack Aura (Range: 100, +15%)");
            ctx.bus.publish(new Events.Tick());  // 立即觸發 Tick 讓光環效果生效
            updateUI();
        });
        controlsPanel.add(auraP2Btn);

        // 按鈕：切換 PVE/PVP 模式
        JButton modeBtn = new JButton("Switch Mode (Current: " + ctx.mode + ")");
        modeBtn.addActionListener(e -> {
            ctx.mode = (ctx.mode == Mode.PVE) ? Mode.PVP : Mode.PVE;
            modeBtn.setText("Switch Mode (Current: " + ctx.mode + ")");
            log("System: Game mode switched to " + ctx.mode);
            ctx.bus.publish(new Events.Tick());  // 重新計算屬性
            updateUI();
        });
        controlsPanel.add(modeBtn);

        // 按鈕：手動觸發 Tick 事件
        JButton tickBtn = new JButton("Trigger Game Tick");
        tickBtn.addActionListener(e -> {
            ctx.bus.publish(new Events.Tick());
            log("System: Triggered game tick event");
            updateUI();
        });
        controlsPanel.add(tickBtn);

        add(controlsPanel, BorderLayout.SOUTH);
    }

    /**
     * 執行攻擊動作
     *
     * 1. 計算攻擊傷害（考慮攻擊力、防禦力和元素倍率）
     * 2. 目標受傷（護盾先扣，再扣 HP）
     * 3. 發布受傷事件
     * 4. 更新 UI 顯示
     *
     * @param attacker 攻擊者
     * @param target 防禦者
     * @param atkName 攻擊者顯示名稱
     * @param targetName 防禦者顯示名稱
     */
    private void performAttack(Entity attacker, Entity target, String atkName, String targetName) {
        int damage = DamageCalculator.calculateDamage(attacker, target);
        target.takeDamage(damage);

        // 發布受傷事件（供其他系統監聽）
        ctx.bus.publish(new Events.OnDamageTaken(target, damage));

        log(String.format("Battle: %s attacks %s (Elements: %s vs %s) -> %d damage",
                atkName, targetName, attacker.element.getName(), target.element.getName(), damage));

        updateUI();
    }

    /**
     * 添加一行日誌到顯示區
     * 
     * @param msg 日誌消息
     */
    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());  // 自動滾動到最新行
    }

    /**
     * 刷新所有 UI 組件的顯示
     * 
     * 重新讀取實體數據並更新角色面板的顯示。
     */
    private void updateUI() {
        p1Panel.updateDisplay();
        p2Panel.updateDisplay();
    }
}
