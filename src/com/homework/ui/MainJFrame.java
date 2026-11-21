package com.homework.ui;
import javax.swing.*;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class MainJFrame extends JFrame implements ActionListener{
    JMenuBar jMenuBar;
    JMenu aboutmenu;
    JMenu jMenu;
    JMenuItem jMenuItem1;
    JMenuItem jMenuItem2;
    JMenuItem aboutItem;
    public MainJFrame(){}
    public MainJFrame(int width,int height){
        init_JFrame(width,height);
    }
    private void init_JFrame(int width,int height){
        this.setSize(width, height);
        this.setTitle("单词背诵");
        this.setLayout(null);
        this.setAlwaysOnTop(true);
        this.setDefaultCloseOperation(3);
        initMenu();
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
    }
}
