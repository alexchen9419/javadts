package gui;

import game.*;
import game.abilities.*;
import javax.swing.*;
import java.awt.*;

/**
 * 遊戲主視窗
 * 整合所有 GUI 元件並處理用戶交互
 */
public class GameWindow extends JFrame {
    private final GameContext ctx;
    private final Entity p1;
    private final Entity p2;

    // UI Components
    private EntityPanel p1Panel;
    private EntityPanel p2Panel;
    private JTextArea logArea;

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
        // 上方：角色狀態區
        JPanel playersPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        p1Panel = new EntityPanel("Player 1 (Fire)", p1);
        p2Panel = new EntityPanel("Player 2 (Wood)", p2);
        playersPanel.add(p1Panel);
        playersPanel.add(p2Panel);
        add(playersPanel, BorderLayout.NORTH);

        // 中間：日誌區
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Battle Log"));
        add(logScroll, BorderLayout.CENTER);

        // 下方：控制區
        JPanel controlsPanel = new JPanel(new GridLayout(2, 3, 5, 5));

        // 按鈕：P1 攻擊 P2
        JButton atkP1Btn = new JButton("P1 Attack P2");
        atkP1Btn.addActionListener(e -> performAttack(p1, p2, "P1", "P2"));
        controlsPanel.add(atkP1Btn);

        // 按鈕：P2 攻擊 P1
        JButton atkP2Btn = new JButton("P2 Attack P1");
        atkP2Btn.addActionListener(e -> performAttack(p2, p1, "P2", "P1"));
        controlsPanel.add(atkP2Btn);

        // 按鈕：P1 暴擊 (觸發護盾)
        JButton critP1Btn = new JButton("Trigger P1 Crit");
        critP1Btn.addActionListener(e -> {
            ctx.bus.publish(new Events.OnCrit(p1));
            log("系統: P1 觸發暴擊事件！");
            updateUI();
        });
        controlsPanel.add(critP1Btn);

        // 按鈕：P2 開啟光環
        JButton auraP2Btn = new JButton("Setup P2 Aura (Atk +15%)");
        auraP2Btn.addActionListener(e -> {
            Ability aura = new AttackAuraAbility("AURA_P2", 1.15, 100.0);
            p2.addAbility(aura);
            aura.onAttach(p2, ctx);
            log("系統: P2 開啟攻擊光環 (範圍 100, +15%)");
            ctx.bus.publish(new Events.Tick()); // 立即觸發一次 Tick 讓效果生效
            updateUI();
        });
        controlsPanel.add(auraP2Btn);

        // 按鈕：切換模式
        JButton modeBtn = new JButton("Switch Mode (Current: " + ctx.mode + ")");
        modeBtn.addActionListener(e -> {
            ctx.mode = (ctx.mode == Mode.PVE) ? Mode.PVP : Mode.PVE;
            modeBtn.setText("Switch Mode (Current: " + ctx.mode + ")");
            log("系統: 遊戲模式切換為 " + ctx.mode);
            // 重新計算屬性
            ctx.bus.publish(new Events.Tick());
            updateUI();
        });
        controlsPanel.add(modeBtn);

        // 按鈕：Tick
        JButton tickBtn = new JButton("Trigger Game Tick");
        tickBtn.addActionListener(e -> {
            ctx.bus.publish(new Events.Tick());
            log("系統: 觸發遊戲 Tick");
            updateUI();
        });
        controlsPanel.add(tickBtn);

        add(controlsPanel, BorderLayout.SOUTH);
    }

    private void performAttack(Entity attacker, Entity target, String atkName, String targetName) {
        int damage = DamageCalculator.calculateDamage(attacker, target);
        target.takeDamage(damage);

        // 觸發受傷事件 (可選)
        ctx.bus.publish(new Events.OnDamageTaken(target, damage));

        log(String.format("戰鬥: %s 攻擊 %s (元素: %s vs %s) -> 造成 %d 傷害",
                atkName, targetName, attacker.element.getName(), target.element.getName(), damage));

        updateUI();
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void updateUI() {
        p1Panel.updateDisplay();
        p2Panel.updateDisplay();

        // 同步內部日誌到顯示區 (這裡簡單處理，實際可能需要更好的日誌系統集成)
        // 由於 BattleLog 目前只是存儲在 List 中，我們這裡只顯示最新的交互日誌
    }
}
