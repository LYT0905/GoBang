package com.chess.ui.web;

import com.chess.MyApplication;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;


public class LoginUI extends Stage {
    private Stage stage = this;
    private final int width = 500;//棋盘的宽度
    private final int height = 500;//棋盘的高度
    public LoginUI() {
        Pane pane = new Pane();
        pane.setBackground(new Background(new BackgroundFill(Color.ORANGE, null, null)));
        Button quitButton = qButton();
        //创建文本标签
        Label username = new Label("请输入用户名:");
        username.setLayoutX(120);
        username.setLayoutY(50);
        Label password = new Label("请输入密码:");
        password.setLayoutX(120);
        password.setLayoutY(90);
        //创建文本框对象，并将它们与相应的标签关联起来
        TextField usernameText = new TextField();
        usernameText.setLayoutX(230);
        usernameText.setLayoutY(50);
        usernameText.setPromptText("请输入用户名"); // 设置提示文本
        TextField passwordText = new TextField();
        passwordText.setLayoutX(230);
        passwordText.setLayoutY(90);
        passwordText.setPromptText("请输入密码"); // 设置提示文本
        Button login = new Button("登录");
        login.setLayoutX(120);
        login.setLayoutY(140);
        login.setOnAction(new EventHandler<ActionEvent>() {
            // 获取用户输入的值
            @Override
            public void handle(ActionEvent event) {
                extracted(usernameText, passwordText, pane);
            }
        });

        Button enrollButton = new Button("注册");
        enrollButton.setLayoutX(250);
        enrollButton.setLayoutY(140);
        enrollButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                EnrollUI eu = new EnrollUI();
                eu.show();
                stage.close();
            }
        });

        pane.getChildren().addAll(username, password, usernameText, passwordText,login,quitButton,enrollButton);
        Scene scene = new Scene(pane, width, height);

        this.setScene(scene);
    }

    //登录
    private void extracted(TextField usernameText, TextField passwordText, Pane pane) {
        String uname = usernameText.getText();
        String pword = passwordText.getText();
        System.out.println(uname + " " + pword);
        if (uname != null && pword != null) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            Connection conn = null;
            try {
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chess", "root", "root");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement("select *from user where username = ? and password = ?");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                ps.setString(1, uname);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                ps.setString(2, pword);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            ResultSet rs = null;
            try {
                rs = ps.executeQuery();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            boolean b = false;
            try {
                b = rs.next();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            if (b) {
                System.out.println("登录成功");
                //      创建网络版画板对象
                Button webButton = new Button("网络版");
                webButton.setPrefSize(150,100);
                webButton.setLayoutX(300);
                webButton.setLayoutY(150);
                webButton.setBackground(new Background(new BackgroundFill(Color.WHITE,null,null)));
                pane.getChildren().add(webButton);
                MessageUI messageUI = new MessageUI();
                messageUI.show();
                stage.close();
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("登录失败");
            }
        }
    }
    //退出
    private Button qButton() {
        //创建退出画板对象
        Button quitButton = new Button("退出");
        quitButton.setLayoutX(380);
        quitButton.setLayoutY(140);


        quitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("退出");
                alert.setContentText("你确定需要退出吗？");
                alert.setHeaderText("是否确认退出？");


                Optional optional = alert.showAndWait();

                if (optional.get() == ButtonType.OK){
                    System.out.println("退出");
                    System.exit(0);
                }else {
                    event.consume();
                }
            }
        });
        return quitButton;
    }
}

