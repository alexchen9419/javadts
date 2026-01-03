package gui;

import game.Entity;
import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

/**
 * 實體狀態面板
 * 顯示單個實體（玩家/敵人）的詳細狀態
 * 包括名稱、生命值、護盾值、元素屬性和當前 Buff
 */
public class EntityPanel extends JPanel {
    private final String name;
    private final Entity entity;

    // UI 元件
    private JLabel nameLabel;
    private JProgressBar hpBar;
    private JLabel statsLabel;
    private JTextArea buffArea;

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
        // 頂部：名稱與元素
        JPanel topPanel = new JPanel(new BorderLayout());
        nameLabel = new JLabel(name + " [" + entity.element.getName() + "]");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        topPanel.add(nameLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // 中部：數值條與屬性
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // HP 條
        hpBar = new JProgressBar(0, entity.maxHp);
        hpBar.setStringPainted(true);
        hpBar.setForeground(Color.RED);
        centerPanel.add(new JLabel("HP:"));
        centerPanel.add(hpBar);
        centerPanel.add(Box.createVerticalStrut(5));

        // 數值標籤 (護盾, 攻擊, 防禦)
        statsLabel = new JLabel();
        centerPanel.add(statsLabel);
        centerPanel.add(Box.createVerticalStrut(5));

        add(centerPanel, BorderLayout.CENTER);

        // 底部：Buff 列表
        buffArea = new JTextArea(4, 20);
        buffArea.setEditable(false);
        buffArea.setLineWrap(true);
        buffArea.setWrapStyleWord(true); // 修正為 setWrapStyleWord
        JScrollPane scrollPane = new JScrollPane(buffArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Buffs"));
        add(scrollPane, BorderLayout.SOUTH);
    }

    /**
     * 更新面板顯示
     * 重新讀取實體數據並刷新界面
     */
    public void updateDisplay() {
        // 更新 HP
        hpBar.setValue(entity.currentHp);
        hpBar.setString(entity.currentHp + " / " + entity.maxHp);

        // 更新護盾與屬性
        // 注意：這裡調用 getFinalStats 會觸發屬性計算，可能會有輕微性能開銷，但在 GUI 中通常可接受
        var finalStats = entity.getFinalStats();
        String statsText = String.format("<html>護盾: <font color='blue'>%d</font><br>" +
                "攻擊: %.1f<br>" +
                "防禦: %.1f</html>",
                entity.shield, finalStats.attack, finalStats.defense);
        statsLabel.setText(statsText);

        // 更新 Buff 列表
        // 這裡我們直接遍歷 abilities 來顯示名稱，或者用 Entity 的 buffs 字段
        // 為了更詳細，我們顯示 Ability ID 和 Category
        String buffsText = entity.abilities.stream()
                .map(a -> String.format("[%s] %s", a.category().name(), a.id()))
                .collect(Collectors.joining("\n"));
        buffArea.setText(buffsText.isEmpty() ? "(無)" : buffsText);
    }
}
