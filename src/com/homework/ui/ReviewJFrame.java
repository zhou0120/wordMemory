package com.homework.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.homework.Service.ReviewService;
import com.homework.domain.Word;

/**
 * 错题复习页。
 *
 * 视觉沿用拼写页（WordJFrame）那一套：同一张深色背景图 + 一张悬浮白卡片，
 * 卡片内部按 BorderLayout 分三块：
 *   顶部  ——「← 返回」+ 标题「错题复习」+ 错题条数胶囊
 *   中部  —— 斑马纹表格（无数据时切成“空状态”提示）
 *   底部  ——「清空列表」（次要） + 「开始背诵」（主按钮）
 *
 * 业务逻辑仍然只有三条：读错题、清空错题、带着错题列表进拼写页复习。
 */
public class ReviewJFrame extends JFrame implements ActionListener {

    /* ================= 数据 / 服务 ================= */
    ReviewService reviewService = new ReviewService();   // 在字段处初始化：initPanel 里要用，晚赋值就会 NPE
    List<Word> words;                                    // 当前错题列表，进拼写页复习时直接传过去
    String username;

    /* ================= 菜单（保留原来那套） ================= */
    JMenuBar jMenuBar;
    JMenu jMenu;
    JMenu aboutmenu;
    JMenuItem jMenuItem1;
    JMenuItem jMenuItem2;
    JMenuItem aboutItem;

    /* ================= 界面组件 ================= */
    JButton jButton;        // 开始背诵（主按钮）
    JButton ClearButton;    // 清空列表（次要按钮）
    JButton backButton;     // ← 返回
    JLabel nameLabel;       // 标题下的欢迎语
    JLabel countLabel;      // 右上角条数胶囊

    JPanel rootPanel;       // 白卡片本体
    JPanel topPanel;        // 卡片头部
    JPanel centerPanel;     // 表格 / 空状态 的切换容器
    CardLayout centerLayout;

    DefaultTableModel model;
    JTable table;
    JScrollPane scrollPane;

    String[] columns = {"单词", "音标", "释义"};

    /* 表格配色：斑马纹只做极浅的错行，避免抢内容；选中行用品牌蓝的浅色调 */
    private static final Color STRIPE_BG = new Color(0xF8FBFF);
    private static final Color SELECT_BG = new Color(0xD9E8FB);
    private static final Color HEADER_BG = new Color(0xF2F7FD);

    private static final String FONT_FAMILY = "PingFang SC";

    /* ================= 构造 ================= */

    public ReviewJFrame() {
    }

    public ReviewJFrame(int width, int height, String username) {
        this.username = (username == null) ? "" : username;
        init_JFrame(width, height, this.username);
    }

    private void init_JFrame(int width, int height, String username) {
        this.username = username;

        setTitle("错题复习 · 单词背诵APP");
        setSize(width, height);
        setMinimumSize(new Dimension(720, 520));
        setLocationRelativeTo(null);                     // 居中弹出，不再写死坐标
        // 关掉这一页只关这扇窗，主页面还在（原来用 EXIT_ON_CLOSE 会把整个程序杀掉）
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initMenu();
        initPanel(username);
        setVisible(true);
    }

    /* ================= 页面 ================= */

    private void initPanel(String username) {

        /* ---------- 白卡片：圆角 + 细边，内部上中下三块 ---------- */
        rootPanel = new CardPanel(20, new Color(255, 255, 255, 250), UiKit.LINE);
        rootPanel.setPreferredSize(new Dimension(700, 450));
        rootPanel.setLayout(new BorderLayout());
        rootPanel.setBorder(BorderFactory.createEmptyBorder(22, 32, 24, 32));

        /* ---------- 顶部：返回 + 标题 + 条数胶囊 ---------- */
        backButton = new GhostButton("← 返回");
        backButton.setPreferredSize(new Dimension(92, 32));
        backButton.setMaximumSize(new Dimension(92, 32));
        backButton.addActionListener(this);

        // 用 FlowLayout 包一层：BorderLayout 的 WEST 会把按钮拉满整个头部高度
        JPanel backWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backWrap.setOpaque(false);
        backWrap.add(backButton);

        JLabel titleLabel = new JLabel("错题复习");
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 20));
        titleLabel.setForeground(UiKit.TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameLabel = new JLabel("欢迎回来，" + username);
        nameLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        nameLabel.setForeground(UiKit.TEXT_SUB);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(titleLabel);
        titleBox.add(Box.createRigidArea(new Dimension(0, 4)));
        titleBox.add(nameLabel);

        // 条数胶囊：白底蓝字的小圆角标签，条数在 refreshTable() 里更新
        countLabel = new UiKit.RoundedLabel("共 0 条", new Color(0xEAF2FE), 16, UiKit.PRIMARY);
        countLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 12));
        countLabel.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        // GridBagLayout 只做一件事：让胶囊保持自己的 preferredSize 并垂直居中，不被拉成整列高
        JPanel badgeWrap = new JPanel(new GridBagLayout());
        badgeWrap.setOpaque(false);
        badgeWrap.add(countLabel);

        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);
        header.add(backWrap, BorderLayout.WEST);
        header.add(titleBox, BorderLayout.CENTER);
        header.add(badgeWrap, BorderLayout.EAST);

        // 头部下面一条 1px 分割线，把导航区和内容区分开（同拼写页的处理）
        JPanel divider = new JPanel();
        divider.setBackground(UiKit.LINE);
        divider.setPreferredSize(new Dimension(0, 1));

        JPanel dividerWrap = new JPanel(new BorderLayout());
        dividerWrap.setOpaque(false);
        dividerWrap.setBorder(BorderFactory.createEmptyBorder(16, 0, 14, 0));
        dividerWrap.add(divider, BorderLayout.CENTER);

        topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(header, BorderLayout.CENTER);
        topPanel.add(dividerWrap, BorderLayout.SOUTH);
        rootPanel.add(topPanel, BorderLayout.NORTH);

        /* ---------- 中部：表格 + 空状态，用 CardLayout 二选一 ---------- */
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;                 // 只读：点单元格不会掉进编辑态
            }
        };
        table = createTable(model);

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());        // 边框交给外层圆角卡片画
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);         // 滚轮一次两行，手感不飘

        JPanel tableCard = new CardPanel(14, Color.WHITE, UiKit.LINE);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        JLabel emptyTitle = new JLabel("错题集是空的");
        emptyTitle.setFont(new Font(FONT_FAMILY, Font.BOLD, 16));
        emptyTitle.setForeground(UiKit.TEXT);

        JLabel emptyTip = new JLabel("在拼写页答错的单词会自动收进这里");
        emptyTip.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        emptyTip.setForeground(UiKit.TEXT_SUB);

        JPanel emptyBox = new JPanel();
        emptyBox.setOpaque(false);
        emptyBox.setLayout(new BoxLayout(emptyBox, BoxLayout.Y_AXIS));
        emptyTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyTip.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyBox.add(emptyTitle);
        emptyBox.add(Box.createRigidArea(new Dimension(0, 6)));
        emptyBox.add(emptyTip);

        JPanel emptyPanel = new JPanel(new GridBagLayout());   // GridBagLayout = 水平垂直都居中
        emptyPanel.setOpaque(false);
        emptyPanel.add(emptyBox);

        centerLayout = new CardLayout();
        centerPanel = new JPanel(centerLayout);
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        centerPanel.add(tableCard, "table");
        centerPanel.add(emptyPanel, "empty");
        rootPanel.add(centerPanel, BorderLayout.CENTER);

        /* ---------- 底部：清空（次要） + 开始背诵（主按钮） ---------- */
        ClearButton = new GhostButton("清空列表", true);   // true = 危险色，提醒这是破坏性操作
        ClearButton.addActionListener(this);

        jButton = new UiKit.PrimaryButton("开始背诵");
        jButton.setPreferredSize(new Dimension(150, 42));
        jButton.setMaximumSize(new Dimension(150, 42));
        jButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jButton.addActionListener(this);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));
        bottomPanel.add(ClearButton);
        bottomPanel.add(jButton);
        rootPanel.add(bottomPanel, BorderLayout.SOUTH);

        /* ---------- 背景图：Images/bg_word_dark.png，与拼写页同一张 ---------- */
        JPanel outer = new JPanel(new GridBagLayout());   // 只负责把卡片放到窗口正中
        outer.setOpaque(false);                          // 必须透明，才露得出背景
        outer.add(rootPanel);

        final Image bgImage = new ImageIcon("Images/bg_word_dark.png").getImage();
        JPanel bgPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);   // 铺满内容区
                g2.dispose();
            }
        };
        bgPanel.add(outer, BorderLayout.CENTER);
        setContentPane(bgPanel);

        refreshTable();   // 读数据 → 填表 → 更新胶囊 → 决定显示表格还是空状态
    }

    /** 表格外观：表头浅蓝灰底、无网格线、行高 38、斑马纹。 */
    private JTable createTable(DefaultTableModel model) {
        JTable t = new JTable(model);

        t.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        t.setForeground(UiKit.TEXT);
        t.setBackground(Color.WHITE);
        t.setRowHeight(38);
        t.setShowGrid(false);                                  // 去掉默认网格线，靠斑马纹分行
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFillsViewportHeight(true);                         // 行数少时白底也铺满整块区域
        t.setSelectionBackground(SELECT_BG);
        t.setSelectionForeground(UiKit.TEXT);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 表头：浅蓝灰底 + 加粗小字 + 底部一条分割线
        JTableHeader header = t.getTableHeader();
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);                     // 列不允许拖动，避免列顺序错乱
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setBackground(HEADER_BG);
                label.setForeground(UiKit.TEXT_LABEL);
                label.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, UiKit.LINE),
                        BorderFactory.createEmptyBorder(0, 14, 0, 14)));
                return label;
            }
        });

        // 单元格：左内边距 14，斑马纹，音标列用次一级灰度
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                label.setBackground(isSelected ? SELECT_BG : (row % 2 == 0 ? Color.WHITE : STRIPE_BG));
                label.setForeground(column == 1 ? UiKit.TEXT_LABEL : UiKit.TEXT);   // 音标弱化一档
                return label;
            }
        });

        // 列宽：音标最长，给多 40px；三列按比例自适应剩余宽度
        t.getColumnModel().getColumn(0).setPreferredWidth(180);
        t.getColumnModel().getColumn(1).setPreferredWidth(220);
        t.getColumnModel().getColumn(2).setPreferredWidth(200);
        return t;
    }

    /**
     * 重新读错题文件并刷新页面：表格、条数胶囊、空状态、主按钮可用性一次改到位。
     * 增删错题后都调它，不要各自 model.addRow / setText，否则很容易只改一半。
     */
    private void refreshTable() {
        words = reviewService.LoadWrongWords();

        model.setRowCount(0);
        for (Word word : words) {
            // 数据文件的语义是 “中文,音标,英文”：word=中文释义，translate=英文单词
            model.addRow(new Object[]{word.getTranslate(), word.getPhonetic(), word.getWord()});
        }

        countLabel.setText("共 " + words.size() + " 条");

        boolean empty = words.isEmpty();
        centerLayout.show(centerPanel, empty ? "empty" : "table");
        jButton.setEnabled(!empty);            // 没有错题就没必要点“开始背诵”
        ClearButton.setEnabled(!empty);
    }

    /* ================= 菜单 ================= */

    private void initMenu() {
        jMenuBar = new JMenuBar();
        jMenu = new JMenu("菜单");
        aboutmenu = new JMenu("关于我们");
        jMenuItem1 = new JMenuItem("退出");
        jMenuItem2 = new JMenuItem("重新登录");
        aboutItem = new JMenuItem("制作团队");
        jMenuItem1.addActionListener(this);
        jMenuItem2.addActionListener(this);
        aboutItem.addActionListener(this);

        jMenu.add(jMenuItem1);
        jMenu.add(jMenuItem2);
        aboutmenu.add(aboutItem);
        jMenuBar.add(jMenu);
        jMenuBar.add(aboutmenu);
        this.setJMenuBar(jMenuBar);
        UiKit.styleMenuBar(jMenuBar);   // 与登录/主页/拼写页统一的菜单字体与分割线
    }

    /* ================= 事件 ================= */

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == jMenuItem1) {
            System.exit(0);
        } else if (obj == jMenuItem2) {
            this.dispose();
            new LoginJFrame(600, 450);
        } else if (obj == aboutItem) {
            JOptionPane.showMessageDialog(this, "制作团队：周博浩、苏立浩、刘明欢");
        } else if (obj == backButton) {                  // 返回主页面，带上原用户名
            this.dispose();
            new MainJFrame(1000, 700, username);
        } else if (obj == jButton) {
            if (words == null || words.isEmpty()) {
                return;                                  // 按钮置灰时点不到，这里再兜一层
            }
            this.dispose();
            new WordJFrame(800, 600, username, words);
        } else if (obj == ClearButton) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "确定清空全部错题记录吗？", "清空错题集",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.OK_OPTION) {
                return;
            }
            if (reviewService.clearWrongWord()) {
                refreshTable();               // 表格清空 + 胶囊归零 + 切到空状态，不再单独弹“成功”
            } else {
                JOptionPane.showMessageDialog(this, "清空失败，详情看控制台日志",
                        "清空失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /* ================= 自绘小组件 ================= */

    /** 圆角底 + 1px 描边的面板：既当外层白卡片，也当表格的圆角容器。 */
    private static class CardPanel extends JPanel {
        private final int arc;
        private final Color fill;
        private final Color line;

        CardPanel(int arc, Color fill, Color line) {
            this.arc = arc;
            this.fill = fill;
            this.line = line;
            setOpaque(false);              // 背景自己画，必须关掉 opaque，否则会盖一层灰底
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            if (line != null) {
                g2.setColor(line);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * 次要按钮：白底 + 细描边 + 彩色字，悬停只加深底色和描边。
     * danger=true 时换成红色系，用于「清空列表」这种破坏性操作。
     */
    private static class GhostButton extends JButton {
        private static final Color DANGER_TEXT = new Color(0xC0392B);

        private final Color baseText;
        private final Color baseLine;
        private final Color hoverFill;
        private final Color hoverLine;

        GhostButton(String text) {
            this(text, false);
        }

        GhostButton(String text, boolean danger) {
            super(text);
            this.baseText = danger ? DANGER_TEXT : UiKit.PRIMARY;
            this.baseLine = danger ? new Color(0xF0C8C2) : new Color(0xC9DEF7);
            this.hoverFill = danger ? new Color(0xFDF2F0) : new Color(0xEAF2FE);
            this.hoverLine = baseText;

            setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
            setForeground(baseText);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
            setPreferredSize(new Dimension(104, 42));
            setMaximumSize(new Dimension(104, 42));

            // 置灰时文字颜色靠这个监听切：不能在 paintComponent 里 setForeground，
            // 那会触发属性变更 → 重绘 → 又变色，形成无限重绘
            addPropertyChangeListener("enabled", evt ->
                    setForeground(isEnabled() ? baseText : new Color(0xA8B4C0)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fill;
            Color line;
            if (!isEnabled()) {                                     // 置灰：浅底浅边（文字颜色由 enabled 监听切换）
                fill = new Color(0xF6F8FB);
                line = UiKit.LINE;
            } else if (getModel().isRollover()) {
                fill = hoverFill;
                line = hoverLine;
            } else {
                fill = Color.WHITE;
                line = baseLine;
            }

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.setColor(line);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
