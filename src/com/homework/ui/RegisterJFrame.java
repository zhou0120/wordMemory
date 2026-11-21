package com.homework.ui;
import com.homework.domain.User;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class RegisterJFrame extends JFrame implements ActionListener{
    JButton jButton1;
    JButton jButton2;
    JMenuBar jMenuBar;
    JMenu aboutmenu;
    JMenu jMenu;
    JMenuItem jMenuItem1;
    JMenuItem jMenuItem2;
    JMenuItem aboutItem;

    JTextField jTextField;
    JPasswordField jPasswordField1;
    JPasswordField jPasswordField2;
    public RegisterJFrame() {
    }
    public RegisterJFrame(int width, int height){
        init_JFrame(width, height);
    }
    private void init_JFrame(int width, int height){
        this.setSize(width, height);
        this.setTitle("注册");
        this.setAlwaysOnTop(true);
        this.setDefaultCloseOperation(3);
        this.setLayout(null);
        init_all();
        initMenu();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    private void init_all(){
        jButton1 = new UiKit.PrimaryButton("注册");
        jButton2 = UiKit.linkButton("返回登录");
        jButton1.addActionListener(this);
        jButton2.addActionListener(this);

        jTextField = UiKit.textField();
        jPasswordField1 = UiKit.passwordField();
        jPasswordField2 = UiKit.passwordField();
        // 回车等同于点“注册”
        UiKit.submitOnEnter(jTextField, jButton1);
        UiKit.submitOnEnter(jPasswordField1, jButton1);
        UiKit.submitOnEnter(jPasswordField2, jButton1);

        JPanel box = UiKit.formBox();
        box.add(UiKit.title("创建账号"));
        box.add(UiKit.subtitle("设置用户名和密码，开始你的单词计划"));
        box.add(UiKit.gap(14));
        box.add(UiKit.fieldLabel("用户名"));
        box.add(jTextField);
        box.add(UiKit.gap(8));
        box.add(UiKit.fieldLabel("密码"));
        box.add(jPasswordField1);
        box.add(UiKit.gap(8));
        box.add(UiKit.fieldLabel("确认密码"));
        box.add(jPasswordField2);
        box.add(UiKit.gap(14));
        box.add(jButton1);
        box.add(UiKit.gap(10));
        box.add(UiKit.linkRow("已有账号？", jButton2));

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(UiKit.BG);
        content.add(UiKit.brandPanel("单词背诵", "WordMemory", "每日一背 · 积少成多"), BorderLayout.WEST);
        content.add(UiKit.formCenter(box), BorderLayout.CENTER);
        this.setContentPane(content);
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
        UiKit.styleMenuBar(jMenuBar);
        this.setJMenuBar(jMenuBar);
    }
    private boolean isDistinct(String name){
        try{
            List<String> userinfolist = readUsersbyline();
            for(String str : userinfolist){
                String[] parts = str.split("&");
                String userstr = parts[0];
                String username = userstr.split("=")[1];
                return username.equals(name);
            }

        }
        catch(IOException e){
            e.printStackTrace();
        }
        return false;
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
    private void Rigester(String username, String password) throws IOException {
        String line = "username=" + username + "&password=" + password;
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter("src/com/homework/data/UserInfor.txt",true));
            bw.newLine();
            bw.write(line);
            bw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void actionPerformed(ActionEvent e){
        Object obj = e.getSource();
        String username = jTextField.getText();
        String pass1 = jPasswordField1.getText();
        String pass2 = jPasswordField2.getText();
        if(obj == jButton1){
            if(username.equals("")){
                JOptionPane.showMessageDialog(this, "用户名不能为空");
                return;
            }

            else if(isDistinct(username)){
                JOptionPane.showMessageDialog(this,"已有当前用户名");
                return;
            }
            else if(username.length() < 3 || username.length() > 16){
                JOptionPane.showMessageDialog(this,"用户名长度应在3-16之间");
                return;
            }
            if(pass1.equals("")){
                JOptionPane.showMessageDialog(this, "密码不能为空");
                return;
            }
            else if(pass1.length() < 6|| pass1.length() > 20) {
                JOptionPane.showMessageDialog(this, "密码长度在6-20之间");
                return;
            }
            else if (!pass1.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]+$")) {
                JOptionPane.showMessageDialog(this, "密码必须由字母和数字组成");
                return;
            }
            else if(!pass1.equals(pass2)){
                JOptionPane.showMessageDialog(this, "两次输入的密码不一致");
                return;
            }
            else{
                try{
                    Rigester(username, pass1);
                    JOptionPane.showMessageDialog(this,"注册成功");
                }
                catch(IOException Err){
                    Err.printStackTrace();
                }
            }
        }
        else if(obj == jButton2){
            this.dispose();
            new LoginJFrame(600, 450);
            System.out.println("返回");
        }
        else if(obj == jMenuItem1) {
            System.exit(0);
            System.out.println("退出");
        }
        else if(obj == jMenuItem2) {
            this.dispose();
            new LoginJFrame(600, 450);
            System.out.println("重新登录");
        }
        else if(obj == aboutItem) {
            System.out.println("公众号");
        }
    }


}
