package gui;

import game.Entity;
import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

/**
 * 實體狀態面板
 *
 * 顯示單個實體（玩家或敵人）的詳細狀態資訊。
 * 包括：
 * - 名稱和元素屬性
 * - HP 進度條（當前/最大）
 * - 護盾值、攻擊力、防禦力等數值屬性
 * - 當前生效的技能和增益效果
 *
 * 每次調用 updateDisplay() 時都會重新從實體讀取最新數據，
 * 確保 UI 始終反映實體的當前狀態。
 *
 * @author RuneRise UI System
 */
public class EntityPanel extends JPanel {
    private final String name;
    private final Entity entity;

    // UI 組件
    private JLabel nameLabel;          // 名稱和元素顯示
    private JProgressBar hpBar;        // HP 進度條
    private JLabel statsLabel;         // 數值屬性（護盾、攻擊、防禦）
    private JTextArea buffArea;        // 技能和增益效果列表

    /**
     * 構造函數
     * 
     * @param name 面板顯示的名稱（如 "Player 1"）
     * @param entity 要顯示狀態的實體
     */
    public EntityPanel(String name, Entity entity) {
        this.name = name;
        this.entity = entity;

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder(name));
        setPreferredSize(new Dimension(300, 200));

        initUI();
        updateDisplay();
    }

    private void initUI() {
        // ===== 頂部：名稱和元素屬性 =====
        JPanel topPanel = new JPanel(new BorderLayout());
        nameLabel = new JLabel(name + " [" + entity.element.getName() + "]");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        topPanel.add(nameLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // ===== 中央：HP 條和數值屬性 =====
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // HP 進度條
        hpBar = new JProgressBar(0, entity.maxHp);
        hpBar.setStringPainted(true);  // 顯示數字
        hpBar.setForeground(Color.RED);
        centerPanel.add(new JLabel("HP:"));
        centerPanel.add(hpBar);
        centerPanel.add(Box.createVerticalStrut(5));

        // 數值屬性標籤 (護盾、攻擊、防禦)
        statsLabel = new JLabel();
        centerPanel.add(statsLabel);
        centerPanel.add(Box.createVerticalStrut(5));

        add(centerPanel, BorderLayout.CENTER);

        // ===== 底部：技能和增益效果列表 =====
        buffArea = new JTextArea(4, 20);
        buffArea.setEditable(false);
        buffArea.setLineWrap(true);
        buffArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(buffArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Active Abilities"));
        add(scrollPane, BorderLayout.SOUTH);
    }

    /**
     * 更新面板顯示
     *
     * 重新讀取實體的當前數據並刷新所有 UI 組件。
     * 這個方法應該在遊戲狀態改變時調用（如受傷、技能觸發等）。
     *
     * 注意：此方法會調用 getFinalStats()，可能有輕微的性能開銷，
     * 但在 GUI 場景中通常可以接受。
     */
    public void updateDisplay() {
        // 更新 HP 進度條
        hpBar.setValue(entity.currentHp);
        hpBar.setString(entity.currentHp + " / " + entity.maxHp);

        // 更新護盾和屬性值顯示
        // 計算最終屬性（應用所有技能修飾）
        var finalStats = entity.getFinalStats();
        String statsText = String.format("<html>Shield: <font color='blue'>%d</font><br>" +
                "Attack: %.1f<br>" +
                "Defense: %.1f</html>",
                entity.shield, finalStats.attack, finalStats.defense);
        statsLabel.setText(statsText);

        // 更新技能和增益效果列表
        // 遍歷技能列表，顯示每個技能的分類和 ID
        String abilitiesText = entity.abilities.stream()
                .map(a -> String.format("[%s] %s", a.category().name(), a.id()))
                .collect(Collectors.joining("\n"));
        buffArea.setText(abilitiesText.isEmpty() ? "(None)" : abilitiesText);
    }
}
