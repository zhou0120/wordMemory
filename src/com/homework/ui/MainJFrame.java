package com.homework.ui;
import javax.swing.*;
import javax.swing.JFrame;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.FontMetrics;
import com.homework.Service.FileLoadService;
import com.homework.domain.Word;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
public class MainJFrame extends JFrame implements ActionListener{
    JMenuBar jMenuBar;
    JMenu aboutmenu;
    JMenu jMenu;
    JMenuItem jMenuItem1;
    JMenuItem jMenuItem2;
    JMenuItem aboutItem;


    JPanel mainPanel;
    JPanel topPanel;
    JPanel centerPanel;
    JPanel bottomPanel;


    JLabel brandLabel;
    JLabel userNameLabel;
    JLabel surnameBadge;
    // 中部按钮和今日单词
    JButton centerButton;
    // 中部：词库多选下拉（放在“开始背诵”右边）
    LibrarySelector librarySelector;
    // 底部：导入卡片
    ImportCard importCard;
    JLabel centerLabel;
    // 当前登录用户名：打开拼写页时带过去，拼写页点“返回”才能回到同一个用户的主页面
    String userName = "";

    public MainJFrame(){}
    public MainJFrame(int width,int height, String username){
        init_JFrame(width,height,username);
    }
    private void init_JFrame(int width,int height, String username){
        this.setSize(width, height);
        this.setTitle("单词背诵APP");
        this.setLayout(null);
        this.setAlwaysOnTop(true);
        this.setDefaultCloseOperation(3);
        initMenu();
        init_Mainpage(username);

        this.setVisible(true);
    }
    private void initMenu(){
        jMenuBar = new JMenuBar();
        jMenu = new JMenu("菜单");
        aboutmenu = new JMenu("关于我们");
        jMenuItem1 = new JMenuItem("退出");
        jMenuItem2 = new JMenuItem("重新登录");
        aboutItem = new JMenuItem("公众号");
        jMenuItem1.addActionListener(this);
        jMenuItem2.addActionListener(this);
        aboutItem.addActionListener(this);



        jMenu.add(jMenuItem1);
        jMenu.add(jMenuItem2);
        aboutmenu.add(aboutItem);
        jMenuBar.add(jMenu);
        jMenuBar.add(aboutmenu);
        this.setJMenuBar(jMenuBar);
    }
    public void init_Mainpage(String name){       // 传入用户名
        /*
         * 上一版“给面板 setBounds 后靠 GridBagLayout 分配比例”的问题：
         * 窗体是 null 布局，根容器虽然被设置了 bounds，但没有任何东西触发它去 validate/doLayout，
         * 于是 GridBagLayout 从没执行过，三个子面板一直是 0×0，界面上就是一片空白灰底。
         *
         * 这一版的做法（可验证、比例精确）：
         * 1. 内容区改成 BorderLayout，根容器 mainPanel 放到 CENTER，
         *    这样 mainPanel 会自动铺满整个内容区，缩放窗体也跟着变，不用手工算 bounds。
         * 2. mainPanel 用 null 布局，自己在 layoutMainPage() 里按“总高 / 7”精确切成 1:1:5。
         * 3. 监听 mainPanel 的尺寸变化，每次窗口大小改变都重新切一次，比例始终是 1/7、1/7、5/7。
         * 4. 三个区域内部仍是 null 布局，后续直接 setBounds 往里放按钮、标签、图片即可。
         */
        this.userName = (name == null) ? "" : name.trim();   // 记住用户名，供“开始背诵”页返回时使用
        mainPanel = new JPanel(null);           // 根容器：自己切分

        topPanel = new JPanel(null);            // 上部 1/7
        centerPanel = new JPanel(null);         // 中部 1/7
        bottomPanel = new JPanel(null);         // 下部 5/7

        // 淡色系配色：整体偏浅、干净，下部是很淡的蓝色作为主内容区底色
        topPanel.setBackground(new Color(0xFFFFFF));       // 上部：纯白
        centerPanel.setBackground(new Color(0xF5FAFE));    // 中部：极淡蓝白
        bottomPanel.setBackground(new Color(0xE6F1FB));    // 下部：很淡的蓝色

        mainPanel.add(topPanel);
        mainPanel.add(centerPanel);
        mainPanel.add(bottomPanel);

        // 关键：把内容区布局换成 BorderLayout，让 mainPanel 自动铺满内容区
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);

        // 窗体首次显示、后续缩放时都会触发 mainPanel 尺寸变化 → 重新按 7 份切分
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutMainPage();
            }
        });
        // ===== 顶部栏：左侧“单词背诵APP”，右侧“姓(圆角框) + 用户名” =====
        // 具体坐标不在这里写死，统一交给 layoutHeader() 按面板实际宽高摆放
        initHeader(name);
        initcenter();
        initBottom();
    }

    /**
     * 创建顶部栏的三个组件（只建对象 + 设样式，位置交给 layoutHeader 计算）。
     * 这样拆开的好处：窗口缩放时只需要重新排版，不用重建组件。
     */
    private void initHeader(String name){
        String userName = (name == null) ? "" : name.trim();

        // 1) 左侧：应用名，同样放进圆角矩形里（极淡蓝底 + 细蓝边 + 蓝字）
        brandLabel = new RoundedBoxLabel("单词背诵APP",
                new Color(0xF1F8FF), 14, new Color(0x1A6FD4), new Color(0xC2DAF5));
        brandLabel.setFont(new Font("PingFang SC", Font.BOLD, 17));   // macOS 自带字体，Windows 会自动回退
        topPanel.add(brandLabel);

        // 2) 右侧：用户名
        userNameLabel = new JLabel(userName);
        userNameLabel.setFont(new Font("PingFang SC", Font.PLAIN, 14));
        userNameLabel.setForeground(new Color(0x3C4A5A));
        topPanel.add(userNameLabel);

        // 3) 用户名左侧：装“姓”的圆角矩形（浅蓝填充 + 蓝色字）
        String surname = "";
        if (!userName.isEmpty()) {
            surname = userName.substring(0, 1).toUpperCase();   // 中文取第一个字；英文用户名取首字母大写
        }
        surnameBadge = new RoundedBoxLabel(surname,
                new Color(0xDCEEFF), 12, new Color(0x1A6FD4), null);
        surnameBadge.setFont(new Font("PingFang SC", Font.BOLD, 14));
        topPanel.add(surnameBadge);
    }
    private void initcenter(){
        centerButton = new JButton("开始背诵");
        centerButton.setFont(new Font("PingFang SC", Font.BOLD, 14));
        centerButton.setFocusPainted(false);
        centerButton.addActionListener(this);
        centerPanel.add(centerButton);

        // 词库多选下拉，位置在“开始背诵”右边
        librarySelector = new LibrarySelector();
        centerPanel.add(librarySelector);
        refreshLibraryList();

        centerLabel = new JLabel("今日单词");
        centerLabel.setFont(new Font("PingFang SC", Font.PLAIN, 14));
        centerLabel.setForeground(new Color(0x1A6FD4));
        centerPanel.add(centerLabel);
    }

    /** 扫描 data 目录下的 .txt，刷新词库下拉的可选项；首次默认全选。 */
    private void refreshLibraryList(){
        List<String> names = new ArrayList<>();
        File[] files = new File("data/repositories").listFiles((d, n) -> n.toLowerCase().endsWith(".txt"));
        if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName));
            for (File f : files) {
                names.add(f.getName());
            }
        }
        librarySelector.setLibraries(names);
    }

    /**
     * 开始背诵：把勾选的词库逐个读出来合并，再进拼写页。
     * 一个都没勾就提示，避免进去以后是空列表。
     */
    private void startReciting(){
        List<String> names = librarySelector.getSelected();
        if (names.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先选择至少一个词库。",
                    "还没有选择词库", JOptionPane.WARNING_MESSAGE);
            return;
        }
        FileLoadService fls = new FileLoadService();
        List<Word> words = new ArrayList<>();
        for (String name : names) {
            words.addAll(fls.loadWords("data/repositories/" + name));
        }
        if (words.isEmpty()) {
            JOptionPane.showMessageDialog(this, "选中的词库里没有可用的单词。",
                    "词库为空", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.dispose();
        new WordJFrame(1000, 700, userName, words);
    }

    private void initBottom(){
        importCard = new ImportCard();
        bottomPanel.add(importCard);
    }


    private void layoutCenter(){
        if (centerPanel == null || centerButton == null) return;
        int w = centerPanel.getWidth();
        int h = centerPanel.getHeight();
        if (w <= 0 || h <= 0) return;

        int lblW = 90,  lblH = 22;
        int btnW = 130, btnH = 34;
        int selW = 150, selH = 34;
        int gap  = 14;

        // 今日单词(左) + 开始背诵(中) + 词库下拉(右)，整组水平居中
        int totalW = lblW + gap + btnW + gap + selW;
        int startX = (w - totalW) / 2;
        if (startX < 0) startX = 0;

        centerLabel.setBounds(startX, (h - lblH) / 2, lblW, lblH);
        centerButton.setBounds(startX + lblW + gap, (h - btnH) / 2, btnW, btnH);
        librarySelector.setBounds(startX + lblW + gap + btnW + gap, (h - selH) / 2, selW, selH);
    }

    private void layoutBottom(){
        if (bottomPanel == null || importCard == null) return;
        int w = bottomPanel.getWidth();
        int h = bottomPanel.getHeight();
        if (w <= 0 || h <= 0) return;

        // 卡片在底部空旷区域居中，稍微偏上一点，视觉重心更稳
        int cardW = 160, cardH = 140;
        importCard.setBounds((w - cardW) / 2, (h - cardH) / 2 - 10, cardW, cardH);
    }


    private void layoutHeader(){
        if (topPanel == null || brandLabel == null) return;
        int w = topPanel.getWidth();
        int h = topPanel.getHeight();
        if (w <= 0 || h <= 0) return;         // 面板还没布局好，等下一次 resize

        int margin = 22;          // 左右留白
        int badgeSize = 28;       // 姓氏圆角框的边长
        int brandHeight = 34;     // 品牌圆角框的高度
        int brandPadding = 14;    // 品牌文字与框左右边的最小间距
        int gap = 8;              // 圆角框与用户名的间距

        // 左侧品牌名：放进圆角框里，垂直居中，框宽 = 文字宽度 + 两侧内边距
        Dimension brandText = brandLabel.getPreferredSize();
        int brandX = margin - brandPadding;                                   // 让文字左边缘仍对齐 margin
        int brandY = (h - brandHeight) / 2;
        brandLabel.setBounds(brandX, brandY, brandText.width + brandPadding * 2, brandHeight);

        // 右侧用户名：右对齐到留白处，垂直居中
        Dimension nameSize = userNameLabel.getPreferredSize();
        int nameX = w - margin - nameSize.width;
        userNameLabel.setBounds(nameX, (h - nameSize.height) / 2, nameSize.width + 2, nameSize.height);

        // 姓氏圆角框：紧挨在用户名左边，与用户名同一水平中线
        surnameBadge.setBounds(nameX - gap - badgeSize, (h - badgeSize) / 2, badgeSize, badgeSize);
    }

    /**
     * 圆角矩形标签：先画一层浅色圆角底（可选细边框），再让 JLabel 正常画文字。
     * 品牌名和姓氏都用它，只是填充色、弧度和是否描边不同。
     */
    private static class RoundedBoxLabel extends JLabel {
        private final Color fillColor;      // 圆角底的填充色
        private final Color borderColor;    // 边框色，null 表示不描边
        private final int arc;              // 圆角弧度，越大越圆

        RoundedBoxLabel(String text, Color fillColor, int arc, Color textColor, Color borderColor) {
            super(text, SwingConstants.CENTER);   // 文字居中
            this.fillColor = fillColor;
            this.borderColor = borderColor;
            this.arc = arc;
            setForeground(textColor);
            setOpaque(false);                      // 自己画背景，必须关掉 opaque，否则会画成方角
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 抗锯齿，圆角才不毛糙

            // 先填圆角底
            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

            // 需要的话再描一圈 1px 圆角边（注意内缩 0.5px，避免线被裁掉一半）
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            }
            g2.dispose();
            super.paintComponent(g);               // 再画文字
        }
    }
    private void layoutMainPage(){
        if (mainPanel == null) return;
        int w = mainPanel.getWidth();
        int h = mainPanel.getHeight();
        if (w <= 0 || h <= 0) return;          // 还没完成布局时直接跳过，等下一次 resize

        int unit = h / 7;                      // 一份的高度 ≈ 总高的 1/7
        topPanel.setBounds(0, 0, w, unit);                       // 上：1/7
        centerPanel.setBounds(0, unit, w, unit);                 // 中：1/7
        bottomPanel.setBounds(0, unit * 2, w, h - unit * 2);     // 下：剩下全部(≈5/7)

        layoutHeader();                        // 三块区域尺寸定了，再排顶部栏的左右元素
        layoutCenter();                        // 中部按钮/文字同样按面板尺寸摆放
        layoutBottom();                        // 底部导入词库按钮
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == jMenuItem1) {
            System.exit(0);
        } else if (obj == jMenuItem2) {
            this.dispose();
            new LoginJFrame(600,450);
        } else if (obj == aboutItem) {
            JOptionPane.showMessageDialog(this, "公众号：程序员速成");
        } else if (obj == centerButton) {
            startReciting();
        }
    }

    /**
     * 选择词库文件 → 校验 → 进入拼写页。
     * 底部卡片点击和菜单都走这一份逻辑，不重复写。
     */
    private void doImportWords() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择你的词库文件");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(java.io.File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
            }
            public String getDescription() { return "词库文件 (*.txt)"; }
        });
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String path = chooser.getSelectedFile().getAbsolutePath();
        FileLoadService fls = new FileLoadService();
        if (fls.isValidWordFile(path)) {
            int count = fls.loadWords(path).size();

            // 复制到 data 目录，成为下拉列表里的一个可选词库（下次进来还在）
            File src = chooser.getSelectedFile();
            File dest = new File("data", src.getName());
            try {
                if (!src.getCanonicalPath().equals(dest.getCanonicalPath())) {
                    File dir = new File("data");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    java.nio.file.Files.copy(src.toPath(), dest.toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (java.io.IOException ex) {
                JOptionPane.showMessageDialog(this, "文件复制到 data 目录失败：" + ex.getMessage(),
                        "导入失败", JOptionPane.ERROR_MESSAGE);
                return;
            }
            refreshLibraryList();
            librarySelector.selectOnly(dest.getName());   // 导入完默认只背这一个

            JOptionPane.showMessageDialog(this,
                    "导入成功！共 " + count + " 个单词，已加入词库列表。",
                    "导入成功", JOptionPane.INFORMATION_MESSAGE);
            startReciting();
        } else {
            JOptionPane.showMessageDialog(this,
                    "文件格式不对，或者文件是空的。\n每行应按：中文释义,音标,英文单词 的格式提供。",
                    "导入失败", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * 词库多选下拉：外观是一个圆角胶囊（当前选择摘要 + 右侧三角），
     * 点开弹出带勾选框的菜单，可以一次勾多个词库。
     */
    private class LibrarySelector extends JComponent {
        private final List<String> libraries = new ArrayList<>();
        private final Set<String> selected = new LinkedHashSet<>();
        private boolean hover;

        LibrarySelector() {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) { showMenu(); }
            });
        }

        /** 刷新可选项：已选中的如果还存在就保留；一个都没选时默认全选。 */
        void setLibraries(List<String> names) {
            libraries.clear();
            libraries.addAll(names);
            selected.retainAll(names);
            if (selected.isEmpty()) {
                selected.addAll(names);
            }
            repaint();
        }

        /** 只选某一个（导入完成后用，避免把旧词库一起背）。 */
        void selectOnly(String name) {
            selected.clear();
            if (libraries.contains(name)) {
                selected.add(name);
            }
            repaint();
        }

        List<String> getSelected() {
            return new ArrayList<>(selected);
        }

        private String summary() {
            if (libraries.isEmpty()) return "选择词库";
            if (selected.isEmpty()) return "未选择词库";
            if (selected.size() == libraries.size()) return "全部词库";
            if (selected.size() == 1) return selected.iterator().next().replaceAll("\\.txt$", "");
            return "已选 " + selected.size() + " 个词库";
        }

        private void showMenu() {
            if (libraries.isEmpty()) {
                JOptionPane.showMessageDialog(MainJFrame.this,
                        "还没有词库，先点下面的“导入词库”添加一个吧。",
                        "暂无词库", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JPopupMenu menu = new JPopupMenu();
            menu.setBorder(BorderFactory.createLineBorder(new Color(0xD8E5F3)));
            menu.setLayout(new BorderLayout());

            // 用普通 JCheckBox 而不是 JCheckBoxMenuItem：菜单项点一下会关闭弹窗，
            // 普通复选框中途不关闭，才能连续勾选多个词库。
            JPanel box = new JPanel();
            box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
            box.setBackground(Color.WHITE);
            box.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 16));

            for (String name : libraries) {
                JCheckBox cb = new JCheckBox(name, selected.contains(name));
                cb.setFont(new Font("PingFang SC", Font.PLAIN, 13));
                cb.setOpaque(false);
                cb.setFocusPainted(false);
                cb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                cb.addActionListener(e -> {
                    if (cb.isSelected()) {
                        selected.add(name);
                    } else {
                        selected.remove(name);
                    }
                    repaint();   // 选择变了，胶囊上的摘要文字要跟着更新
                });
                box.add(cb);
            }
            menu.add(box);
            menu.show(this, 0, getHeight() + 4);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(hover ? new Color(0xF7FBFF) : Color.WHITE);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 12, 12);
            g2.setColor(hover ? UiKit.PRIMARY : new Color(0xD8E5F3));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);

            // 摘要文字：过长时截断，避免压到右侧三角
            g2.setFont(new Font("PingFang SC", Font.PLAIN, 13));
            g2.setColor(new Color(0x3C4A5A));
            FontMetrics fm = g2.getFontMetrics();
            int maxTextW = w - 14 - 16;
            String text = summary();
            if (fm.stringWidth(text) > maxTextW) {
                while (text.length() > 1 && fm.stringWidth(text + "…") > maxTextW) {
                    text = text.substring(0, text.length() - 1);
                }
                text = text + "…";
            }
            g2.drawString(text, 12, (h - fm.getHeight()) / 2 + fm.getAscent());

            // 右侧下拉三角
            int cx = w - 16;
            int cy = h / 2;
            int[] xs = {cx - 4, cx + 4, cx};
            int[] ys = {cy - 2, cy - 2, cy + 3};
            g2.setColor(new Color(0x8FA3B8));
            g2.fillPolygon(xs, ys, 3);

            g2.dispose();
        }
    }

    /**
     * 底部功能卡片：圆角白底 + 浅蓝图标底 + 蓝色导入图标 + 文字。
     * 悬停时描边变主色、底色微微泛蓝，做出可点击的反馈；图标全部用 Graphics2D 画，不依赖图片文件。
     */
    private class ImportCard extends JComponent {
        private boolean hover;

        ImportCard() {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) { doImportWords(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int w = getWidth();
            int h = getHeight();

            // 卡片：白底 + 细边；悬停时边框和底色都往蓝色偏一点
            g2.setColor(hover ? new Color(0xFBFDFF) : Color.WHITE);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 20, 20);
            g2.setColor(hover ? UiKit.PRIMARY : new Color(0xD8E5F3));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 20, 20);

            // 图标底盘：浅蓝圆角方块
            int iconSize = 54;
            int iconX = (w - iconSize) / 2;
            int iconY = 24;
            g2.setColor(hover ? new Color(0xD6E9FF) : new Color(0xEAF3FE));
            g2.fillRoundRect(iconX, iconY, iconSize, iconSize, 16, 16);

            // 导入图标：向下箭头 + 开口托盘
            g2.setColor(UiKit.PRIMARY);
            g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cx = w / 2;
            int arrowTop = iconY + 12;
            int arrowBottom = iconY + 31;
            g2.drawLine(cx, arrowTop, cx, arrowBottom);              // 箭杆
            g2.drawLine(cx - 8, arrowBottom - 8, cx, arrowBottom);   // 左箭头
            g2.drawLine(cx + 8, arrowBottom - 8, cx, arrowBottom);   // 右箭头
            int trayX = cx - 13;
            int trayTop = arrowBottom + 4;
            int trayBottom = trayTop + 9;
            g2.drawLine(trayX, trayTop, trayX, trayBottom);          // 托盘左壁
            g2.drawLine(trayX, trayBottom, trayX + 26, trayBottom);  // 托盘底
            g2.drawLine(trayX + 26, trayTop, trayX + 26, trayBottom);// 托盘右壁

            // 文字
            g2.setFont(new Font("PingFang SC", Font.BOLD, 14));
            g2.setColor(hover ? UiKit.PRIMARY : new Color(0x1B2733));
            String text = "导入词库";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text, (w - fm.stringWidth(text)) / 2, h - 22);

            g2.dispose();
        }
    }
}
