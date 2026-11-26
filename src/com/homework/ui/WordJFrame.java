package com.homework.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import com.homework.Service.SpellService;
import com.homework.Service.ReviewService;
import com.homework.domain.Word;
public class WordJFrame extends JFrame implements ActionListener,KeyListener {
    ReviewService reviewService = new ReviewService();   // 错题读写统一交给 ReviewService，格式只用维护一份
    ArrayList<Word> list = new ArrayList<Word>();
    static int count = 0;
    static int ErrCount = 0;
    JMenuBar jMenuBar;
    JMenu aboutmenu;
    JMenu jMenu;
    JMenuItem jMenuItem1;
    JMenuItem jMenuItem2;
    JMenuItem aboutItem;

    JPanel root;
    JPanel topPanel;
    JPanel centerPanel;
    JPanel bottomPanel;
    JLabel chineseLabel;
    JLabel phoneticLabel;
    JTextField textField;
    JButton jbutton;
    JButton back;
    String userName = "";                                     // 从主页面带过来的用户名，点“返回”时要带回主页面
    SpellService sp;                                          // 词库路径由构造函数决定
    public WordJFrame(){
        this("data/word1.txt");
    }
    public WordJFrame(String wordFilePath){
        sp = new SpellService(wordFilePath);
        initMenu();
        this.addKeyListener(this);   // 添加KeyListener

    }
    public WordJFrame(int width,int height){
        this(width, height, "", "data/word1.txt");
    }
    public WordJFrame(int width,int height,String userName){
        this(width, height, userName, "data/word1.txt");
    }
    /** 完整构造：可指定词库文件路径。导入用户自定义词库后通过此构造直接开背。 */
    public WordJFrame(int width,int height,String userName, String wordFilePath){
        this.userName = (userName == null) ? "" : userName;
        sp = new SpellService(wordFilePath);
        initJFrame(width,height);
        this.addKeyListener(this);   // 添加KeyListener

    }
    /** 用合并好的单词列表开背（主页勾选多个词库时用）。 */
    public WordJFrame(int width,int height,String userName, List<Word> words){
        this.userName = (userName == null) ? "" : userName;
        sp = new SpellService(words);
        initJFrame(width,height);
        this.addKeyListener(this);   // 添加KeyListener
    }
    private void initJFrame(int width,int height){
        // count/ErrCount 是 static，切换词库重新进入时如果不归零，会用旧下标越界
        count = 0;
        ErrCount = 0;
        list.clear();
        this.setTitle("单词拼写");
        this.setSize(width,height);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        initMenu();
        initPanel();
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
        UiKit.styleMenuBar(jMenuBar);   // 和登录/主页统一菜单字体、底色、分割线
    }
    private void initPanel(){
        Word word = sp.findWord(count);
        String chinese = word.getWord();      // 数据文件格式：中文,音标,英文，所以第一段是中文释义
        String phonetic = word.getPhonetic();

        /* ---------- 卡片：圆角白底 + 细边，内部用 BorderLayout 分上中下三块 ---------- */
        root = new RoundedCardPanel(20);
        root.setPreferredSize(new Dimension(600, 500));
        root.setLayout(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(24, 40, 28, 40));

        /* ---------- 顶部：返回按钮（左） + 标题（中） ---------- */
        back = new GhostButton("← 返回");
        back.addActionListener(this);

        JLabel titleLabel = new JLabel("单词拼写", SwingConstants.CENTER);
        titleLabel.setFont(new Font("PingFang SC", Font.BOLD, 20));
        titleLabel.setForeground(UiKit.TEXT);

        // 右侧放一个和返回按钮同宽的占位，标题才会真正居中
        JPanel headSpacer = new JPanel();
        headSpacer.setOpaque(false);
        headSpacer.setPreferredSize(new Dimension(92, 32));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(back, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);
        header.add(headSpacer, BorderLayout.EAST);

        // 标题下面一条 1px 浅色分割线，把“导航区”和“答题区”分开
        JPanel divider = new JPanel();
        divider.setBackground(UiKit.LINE);
        divider.setPreferredSize(new Dimension(0, 1));
        JPanel dividerWrap = new JPanel(new BorderLayout());
        dividerWrap.setOpaque(false);
        dividerWrap.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        dividerWrap.add(divider, BorderLayout.CENTER);

        JPanel wrongPanel = new JPanel();// 错误次数提示区
        JLabel wrongLabel = new JLabel("错误次数："+ErrCount, SwingConstants.CENTER);
        wrongLabel.setFont(new Font("PingFang SC", Font.PLAIN, 12));
        wrongLabel.setForeground(UiKit.TEXT_SUB);
        wrongPanel.add(wrongLabel);

        topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(dividerWrap, BorderLayout.SOUTH);
        topPanel.add(wrongPanel, BorderLayout.EAST);
        root.add(topPanel, BorderLayout.NORTH);

        /* ---------- 中部：提示语 + 中文释义 + 音标 + 输入框，整体垂直居中 ---------- */
        JLabel tipLabel = new JLabel("根据中文提示，写出对应的英文单词", SwingConstants.CENTER);
        tipLabel.setFont(new Font("PingFang SC", Font.PLAIN, 12));
        tipLabel.setForeground(UiKit.TEXT_SUB);
        tipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        chineseLabel = new JLabel(chinese, SwingConstants.CENTER);
        chineseLabel.setFont(new Font("PingFang SC", Font.BOLD, 34));
        chineseLabel.setForeground(UiKit.TEXT);
        chineseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        phoneticLabel = new JLabel(phonetic, SwingConstants.CENTER);
        phoneticLabel.setFont(new Font("PingFang SC", Font.PLAIN, 15));
        phoneticLabel.setForeground(UiKit.TEXT_SUB);
        phoneticLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        textField = UiKit.textField();                     // 复用 UiKit 的圆角输入框（聚焦时描边变蓝）
        textField.setFont(new Font("PingFang SC", Font.PLAIN, 16));
        textField.setPreferredSize(new Dimension(420, 46));
        textField.setMaximumSize(new Dimension(420, 46));
        textField.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField.addKeyListener(this);

        centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalGlue());         // 上方弹性留白 → 内容整体居中
        centerPanel.add(tipLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        centerPanel.add(chineseLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        centerPanel.add(phoneticLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 26)));
        centerPanel.add(textField);
        centerPanel.add(Box.createVerticalGlue());         // 下方弹性留白
        root.add(centerPanel, BorderLayout.CENTER);

        //按Enter键下一个单词
        jbutton = new UiKit.PrimaryButton("下一个(enter)");


        jbutton.setPreferredSize(new Dimension(180, 42));
        jbutton.setMaximumSize(new Dimension(180, 42));
        jbutton.addActionListener(this);



        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.add(jbutton);
        root.add(bottomPanel, BorderLayout.SOUTH);

        /* ---------- 卡片在窗口里水平垂直居中 ---------- */
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);                    // 必须透明，才能露出背景图
        outer.add(root);

        // ===== 背景图：Images/bg_word_dark.png（深色版，相对路径以项目根目录为基准） =====
        Image bgImage = new ImageIcon("Images/bg_word_dark.png").getImage();

        // 背景面板：铺满整个窗口，把图片拉伸绘制到面板大小，再叠原有内容
        JPanel bgPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);   // 铺满整个内容区
                g2.dispose();
            }
        };
        bgPanel.add(outer, BorderLayout.CENTER);
        this.setContentPane(bgPanel);   // 用背景面板作为窗体内容区

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
        }
        else if (obj == back) {                       // 返回按钮：关掉拼写页，带原用户名回到主页面
            this.dispose();
            new MainJFrame(1000, 700, userName);
        }
        else if(obj == jbutton){
            String ans = textField.getText();
            if(!ans.equals(sp.findWord(count).getTranslate())){
                // 错题交给 ReviewService 落盘：统一逗号格式（中文,音标,英文），写完立即关流
                reviewService.saveWrongWord(sp.findWord(count));
                ErrCount++;
                list.add(new Word(sp.findWord(count)));
                JOptionPane.showMessageDialog(this, sp.findWord(count).getTranslate());
            }
            count++;

            // 背完所有单词：弹出统计结果，不再渲染下一个单词
            if(count >= sp.getTotalCount()){
                StringBuilder sb = new StringBuilder();
                sb.append("背诵结束！共 ").append(sp.getTotalCount())
                        .append(" 个单词，错误 ").append(ErrCount).append(" 个");
                if(!list.isEmpty()){
                    sb.append("：\n");
                    for(Word w : list){
                        sb.append(w.getWord()).append("  ").append(w.getTranslate()).append("\n");
                    }
                }
                JOptionPane.showMessageDialog(this, sb.toString());

                // 二选一：重新开始 or 关闭窗口返回上层
                count = 0;
                ErrCount = 0;
                list.clear();
                initPanel();
                revalidate();
                repaint();
                return;
            }

            textField.setText("");
            initPanel();   // 按新的 count 重新渲染中文/音标/输入框/按钮（内部会 setContentPane 替换旧内容）
            revalidate();
            repaint();
        }
    }

    /**
     * 圆角白卡片：自己画白底 + 圆角描边，比默认方角面板更像“浮在背景上的卡片”。
     */
    private static class RoundedCardPanel extends JPanel {
        private final int arc;   // 圆角弧度

        RoundedCardPanel(int arc) {
            this.arc = arc;
            setOpaque(false);    // 背景由 paintComponent 画，必须关掉 opaque
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 250));               // 近白、略带一点透
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.setColor(UiKit.LINE);                                  // 浅色描边
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * 次要按钮（返回）：浅底 + 细蓝边 + 蓝字，悬停时底色加深一点，
     * 和主按钮 PrimaryButton 形成主次对比，不抢“下一个”的视线。
     */
    private static class GhostButton extends JButton {
        GhostButton(String text) {
            super(text);
            setFont(new Font("PingFang SC", Font.PLAIN, 14));
            setForeground(UiKit.PRIMARY);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            setPreferredSize(new Dimension(92, 32));
            setMaximumSize(new Dimension(92, 32));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() ? new Color(0xEAF2FE) : new Color(0xF7FAFE));  // 悬停加深
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.setColor(getModel().isRollover() ? UiKit.PRIMARY : new Color(0xC9DEF7));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }
    @Override
    public void keyPressed(KeyEvent e) {

    }
    @Override
    public void keyReleased(KeyEvent e) {
        int obj = e.getKeyCode();
        // 如果是按下的键是回车键
        if (obj == KeyEvent.VK_ENTER) {
            jbutton.doClick();
        }
    }
}

