package com.homework.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 登录 / 注册两页共用的视觉组件。
 *
 * 只负责“长什么样”：统一色板（与主页面一致的蓝白配）、字体、圆角输入框、主按钮、
 * 链接按钮和左侧品牌条。业务逻辑仍然留在各自的 JFrame 里，互不影响。
 */
final class UiKit {

    /* ================= 色板：白 + 一档蓝，干净利落 ================= */
    static final Color PRIMARY         = new Color(0x1A6FD4); // 主色
    static final Color PRIMARY_HOVER   = new Color(0x1560BE); // 悬停加深
    static final Color PRIMARY_PRESSED = new Color(0x11529F); // 按下再加深
    static final Color BRAND_TOP       = new Color(0x2379DF); // 品牌条渐变起
    static final Color BRAND_BOTTOM    = new Color(0x1459B8); // 品牌条渐变止
    static final Color BG              = Color.WHITE;
    static final Color LINE            = new Color(0xE2EAF3); // 分割线 / 输入框描边
    static final Color TEXT            = new Color(0x1B2733); // 主文字
    static final Color TEXT_SUB        = new Color(0x8A98A8); // 辅助文字
    static final Color TEXT_LABEL      = new Color(0x5A6B7C); // 表单标签

    /* ================= 字体 ================= */
    private static final String FONT_FAMILY = "PingFang SC"; // macOS 自带；其他系统自动回退
    static final Font TITLE_FONT  = new Font(FONT_FAMILY, Font.BOLD, 26);
    static final Font FIELD_FONT  = new Font(FONT_FAMILY, Font.PLAIN, 14);
    static final Font LABEL_FONT  = new Font(FONT_FAMILY, Font.PLAIN, 12);
    static final Font SUB_FONT    = new Font(FONT_FAMILY, Font.PLAIN, 13);
    static final Font BTN_FONT    = new Font(FONT_FAMILY, Font.BOLD, 15);
    static final Font BRAND_FONT  = new Font(FONT_FAMILY, Font.BOLD, 21);

    /** 左侧品牌条宽度、表单宽度：两页保持一致，切换时不会跳。 */
    static final int BRAND_WIDTH = 208;
    static final int FORM_WIDTH  = 300;

    private UiKit() {
    }

    /* ================= 表单骨架 ================= */

    /** 标题：大字、深色、左对齐。 */
    static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /** 标题下的一句说明，弱化处理，只做点缀。 */
    static JLabel subtitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SUB_FONT);
        label.setForeground(TEXT_SUB);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(8, 0, 0, 0));
        return label;
    }

    /** 输入框上方的小标签，比正文小一号，避免抢视线。 */
    static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_LABEL);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 4, 0));
        return label;
    }

    /** 固定高度的纵向留白；最大宽度放开，方便跟着表单一起撑满。 */
    static Component gap(int height) {
        Box.Filler filler = new Box.Filler(
                new Dimension(0, height), new Dimension(0, height),
                new Dimension(Integer.MAX_VALUE, height));
        filler.setAlignmentX(Component.LEFT_ALIGNMENT);
        return filler;
    }

    /** 表单盒子：纵向堆叠，所有子项左对齐，宽度由输入框的 300px 决定。 */
    static JPanel formBox() {
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        return box;
    }

    /** 右侧表单容器：GridBagLayout 天然把表单盒子放到可用区域的正中间。 */
    static JPanel formCenter(JPanel box) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG);
        outer.setBorder(new EmptyBorder(12, 32, 12, 32));
        outer.add(box);
        return outer;
    }

    /** 底部“还没有账号？立即注册”这类一行：前缀灰字 + 蓝色链接按钮。 */
    static JPanel linkRow(String prefix, JButton link) {
        JLabel text = new JLabel(prefix);
        text.setFont(SUB_FONT);
        text.setForeground(TEXT_SUB);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(text);
        row.add(link);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
        return row;
    }

    /** 回车提交：直接触发主按钮，逻辑与鼠标点击完全一致，不另写一份。 */
    static void submitOnEnter(JTextField field, JButton submit) {
        field.addActionListener(e -> submit.doClick());
    }

    /* ================= 输入框 ================= */

    static JTextField textField() {
        RoundedTextField field = new RoundedTextField();
        styleField(field);
        return field;
    }

    static JPasswordField passwordField() {
        RoundedPasswordField field = new RoundedPasswordField();
        styleField(field);
        return field;
    }

    private static void styleField(JTextField field) {
        field.setFont(FIELD_FONT);
        field.setForeground(TEXT);
        field.setCaretColor(PRIMARY);
        field.setSelectionColor(new Color(0xD9E8FB));
        field.setSelectedTextColor(TEXT);
        field.setOpaque(false);                                     // 背景由我们自己画圆角
        field.setBorder(new EmptyBorder(0, 13, 0, 13));             // 文字左右内缩
        field.setPreferredSize(new Dimension(FORM_WIDTH, 38));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);              // 与标签、按钮统一左对齐
        field.addFocusListener(new FocusAdapter() {                 // 聚焦时描边变蓝
            @Override public void focusGained(FocusEvent e) { field.repaint(); }
            @Override public void focusLost(FocusEvent e)   { field.repaint(); }
        });
    }

    /** 输入框圆角底 + 1px 描边，聚焦时描边换成主色。 */
    private static void paintField(Graphics g, JComponent c, boolean focused) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 12, 12);

        g2.setColor(focused ? PRIMARY : LINE);
        g2.setStroke(new BasicStroke(focused ? 1.5f : 1f));
        g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 12, 12);
        g2.dispose();
    }

    private static class RoundedTextField extends JTextField {
        @Override
        protected void paintComponent(Graphics g) {
            paintField(g, this, isFocusOwner());
            super.paintComponent(g);                                 // 只画文字，不画背景
        }
    }

    private static class RoundedPasswordField extends JPasswordField {
        @Override
        protected void paintComponent(Graphics g) {
            paintField(g, this, isFocusOwner());
            super.paintComponent(g);
        }
    }

    /* ================= 按钮 ================= */

    /** 主按钮：整块圆角色块 + 白字，悬停 / 按下只改深浅，不加描边。 */
    static class PrimaryButton extends JButton {
        PrimaryButton(String text) {
            super(text);
            setFont(BTN_FONT);
            setForeground(Color.WHITE);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 16, 0, 16));
            setPreferredSize(new Dimension(FORM_WIDTH, 40));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fill;
            if (!isEnabled())                 fill = new Color(0xB9C7D6);
            else if (getModel().isPressed())  fill = PRIMARY_PRESSED;
            else if (getModel().isRollover()) fill = PRIMARY_HOVER;
            else                              fill = PRIMARY;

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();

            super.paintComponent(g);
        }
    }

    /** 文字链接按钮：无底色无边框，悬停时颜色加深。 */
    static JButton linkButton(String text) {
        JButton button = new JButton(text);
        button.setFont(SUB_FONT);
        button.setForeground(PRIMARY);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(0, 2, 0, 2));
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setForeground(PRIMARY_HOVER); }
            @Override public void mouseExited(MouseEvent e)  { button.setForeground(PRIMARY); }
        });
        return button;
    }

    /* ================= 左侧品牌条 ================= */

    /**
     * 左侧品牌条：一档蓝的轻微渐变 + 白色字标。
     * 用固定宽度，登录 / 注册两页共用，视觉上像同一套产品。
     */
    static JPanel brandPanel(String title, String latin, String footer) {
        JPanel panel = new GradientPanel(BRAND_TOP, BRAND_BOTTOM);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(BRAND_WIDTH, 0));
        panel.setBorder(new EmptyBorder(38, 28, 30, 24));

        RoundedLabel mark = new RoundedLabel("W", Color.WHITE, 12, PRIMARY);
        mark.setFont(new Font(FONT_FAMILY, Font.BOLD, 21));
        mark.setPreferredSize(new Dimension(42, 42));
        mark.setMinimumSize(new Dimension(42, 42));
        mark.setMaximumSize(new Dimension(42, 42));
        mark.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(BRAND_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel latinLabel = new JLabel(latin);
        latinLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        latinLabel.setForeground(new Color(255, 255, 255, 190));
        latinLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel footerLabel = new JLabel(footer);
        footerLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 11));
        footerLabel.setForeground(new Color(255, 255, 255, 150));
        footerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(mark);
        panel.add(gap(18));
        panel.add(titleLabel);
        panel.add(gap(6));
        panel.add(latinLabel);
        panel.add(Box.createVerticalGlue());   // 把 footer 压到底部
        panel.add(footerLabel);
        return panel;
    }

    /** 一档蓝的斜向渐变底，比纯色多一点层次，但仍然克制。 */
    static class GradientPanel extends JPanel {
        private final Color top;
        private final Color bottom;

        GradientPanel(Color top, Color bottom) {
            this.top = top;
            this.bottom = bottom;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, top, getWidth(), getHeight(), bottom));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** 圆角色块标签（品牌条里的 “W” 标记）：先画底，再画字。 */
    static class RoundedLabel extends JLabel {
        private final Color fill;
        private final Color border;
        private final int arc;

        RoundedLabel(String text, Color fill, int arc, Color textColor) {
            this(text, fill, arc, textColor, null);
        }

        RoundedLabel(String text, Color fill, int arc, Color textColor, Color border) {
            super(text, SwingConstants.CENTER);
            this.fill = fill;
            this.border = border;
            this.arc = arc;
            setForeground(textColor);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            if (border != null) {
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /* ================= 菜单栏：保留原菜单，只把字体和分割线收拾干净 ================= */

    static void styleMenuBar(JMenuBar bar) {
        bar.setBackground(BG);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINE));
        for (Component c : bar.getComponents()) {
            if (c instanceof JMenu) {
                JMenu menu = (JMenu) c;
                menu.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
                menu.setForeground(TEXT_LABEL);
                for (Component item : menu.getMenuComponents()) {
                    if (item instanceof JMenuItem) {
                        ((JMenuItem) item).setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
                    }
                }
            }
        }
    }
}
