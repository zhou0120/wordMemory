package com.homework.ui;
import com.homework.domain.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LoginJFrame extends JFrame implements ActionListener, KeyListener {
    ArrayList<User> users = new ArrayList<>();
    JButton jButton1;
    JButton jButton2;
    JMenuBar jMenuBar;
    JMenu aboutmenu;
    JMenu jMenu;
    JMenuItem jMenuItem1;
    JMenuItem jMenuItem2;
    JMenuItem aboutItem;
    JTextField jTextField;
    JPasswordField jPasswordField;
    public LoginJFrame(){
    }
    public LoginJFrame(int width, int height){
        // 读取用户信息
        try{
            readUserInfo();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        init_JFrame(width, height);
    }

    private void readUserInfo() throws IOException {
        List<String> userinfolist = readUsersbyline();
        for(String str : userinfolist){
            String[] parts = str.split("&");
            String userstr = parts[0];
            String passwordstr = parts[1];
            String username = userstr.split("=")[1];
            String password = passwordstr.split("=")[1];
            users.add(new User(username, password));
        }
        System.out.println(users);
    }
    private List<String> readUsersbyline() throws IOException {
        List<String> strlist = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("src/com/homework/data/UserInfor.txt"));
        String line;
        while((line = br.readLine())!=null){
            if(line.trim().isEmpty())continue;
            strlist.add(line);
//            String[] parts = line.split("&");
//            String userstr = parts[0];
//            String passwordstr = parts[1];
//            String username = userstr.split("=")[1];
//            String password = passwordstr.split("=")[1];
//            users.add()
        }
        return strlist;
    }

    private void init_JFrame(int width, int height){
        this.setSize(width, height);
        this.setTitle("登录");
        this.setAlwaysOnTop(true);//设置窗口总在最上层
        this.setDefaultCloseOperation(3);//设置窗口关闭操作,关闭一个窗口，虚拟机就停止
        this.setLayout(null);
        init_all();
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
    private void init_all(){
        jButton1 = new JButton("登陆");
        jButton2 = new JButton("注册");
        jButton1.setBounds(100, 300, 100, 30);
        jButton2.setBounds(400, 300, 100, 30);
        jButton1.addActionListener(this);
        jButton2.addActionListener(this);
        initMenu();
        jTextField = new JTextField();
        jPasswordField = new JPasswordField();
        jTextField.setBounds(200, 200, 200, 30);
        jPasswordField.setBounds(200, 250, 200, 30);
        this.add(jTextField);
        this.add(jPasswordField);
        this.add(jButton1);
        this.add(jButton2);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == jButton1) {
            String username = jTextField.getText();
            if(username.equals("")) {
                JOptionPane.showMessageDialog(this, "用户名不能为空");
                return;
            }
            String password = new String(jPasswordField.getPassword());
            if(password.equals("")) {
                JOptionPane.showMessageDialog(this, "密码不能为空");
                return;
            }
            if(users.contains(new User(username, password))){

                JOptionPane.showMessageDialog(this, "登录成功");
                this.dispose();
                new MainJFrame(1000, 700, username);

            }
            else{
                JOptionPane.showMessageDialog(this, "用户名或密码错误");
                return;
            }
        } else if (obj == jButton2) {
            this.dispose();
            new RegisterJFrame(600, 450);
            System.out.println("注册");
        }
        else if(obj == jMenuItem1){
            System.exit(0);
            System.out.println("退出");
        } else if (obj == jMenuItem2) {
            this.dispose();
            new LoginJFrame(600, 450);
            System.out.println("重新登录");
        }
        else if(obj == aboutItem){
            System.out.println("公众号");
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
    }
}
