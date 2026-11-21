package com.homework.ui;
import com.homework.domain.User;

import javax.swing.*;
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
        this.setVisible(true);
    }
    private void init_all(){
        jButton1 = new JButton("注册");
        jButton2 = new JButton("返回");
        jButton1.addActionListener(this);
        jButton2.addActionListener(this);
        jButton1.setBounds(200, 350, 100, 30);
        jButton2.setBounds(300, 350, 100, 30);

        jTextField = new JTextField();
        jPasswordField1 = new JPasswordField();
        jPasswordField2 = new JPasswordField();
        jTextField.setBounds(200, 200, 200, 30);
        jPasswordField1.setBounds(200, 250, 200, 30);
        jPasswordField2.setBounds(200, 300, 200, 30);
        this.add(jTextField);
        this.add(jPasswordField1);
        this.add(jPasswordField2);
        this.add(jButton1);
        this.add(jButton2);
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
        BufferedReader br = new BufferedReader(new FileReader("/Users/zhoubohao/Code/JAVAcode/Wordmemory/UserInfor.txt"));
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
            BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/zhoubohao/Code/JAVAcode/Wordmemory/UserInfor.txt",true));
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
