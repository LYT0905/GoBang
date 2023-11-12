package com.chess.ui.web;

import com.chess.MyApplication;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Objects;
import java.util.Optional;

public class EnrollUI extends Stage {
    private Stage stage = this;
    private final int width = 500;//棋盘的宽度
    private final int height = 500;//棋盘的高度
    public EnrollUI(){
        Pane pane = new Pane();
        pane.setBackground(new Background(new BackgroundFill(Color.ORANGE, null, null)));
        Button quitButton = qButton();
        //创建文本标签
        Label username = new Label("请输入用户名:");
        username.setLayoutX(120);
        username.setLayoutY(50);
        //输入密码
        Label password = new Label("请输入密码:");
        password.setLayoutX(120);
        password.setLayoutY(90);
        //二次确认
        Label ensurepassword = new Label("请确认密码:");
        ensurepassword.setLayoutX(120);
        ensurepassword.setLayoutY(130);
        //创建文本框对象，并将它们与相应的标签关联起来
        TextField usernameText = new TextField();
        usernameText.setLayoutX(230);
        usernameText.setLayoutY(50);
        usernameText.setPromptText("请输入用户名"); // 设置提示文本

        TextField passwordText = new TextField();
        passwordText.setLayoutX(230);
        passwordText.setLayoutY(90);
        passwordText.setPromptText("请输入密码"); // 设置提示文本

        TextField ensurepwText = new TextField();
        ensurepwText.setLayoutX(230);
        ensurepwText.setLayoutY(130);
        ensurepwText.setPromptText("请确认密码"); // 设置提示文本


        //确认注册按钮
        Button enroll = new Button("注册");
        enroll.setLayoutX(120);
        enroll.setLayoutY(180);
        enroll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String uname = usernameText.getText();
                String pword = passwordText.getText();
                String espword = ensurepwText.getText();
                if (pword.equals(espword)){
                    System.out.println(uname+"..."+pword+"..."+espword);;
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    Connection conn = null;
                    try {
                        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chess","root","root");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    PreparedStatement ps = null;
                    try {
                        ps = conn.prepareStatement("insert into user(username,password) values (?,?)");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        ps.setString(1,uname);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        ps.setString(2,pword);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        ps.executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    LoginUI lu = new LoginUI();
                    lu.show();
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
                }
            }
        });
        pane.getChildren().addAll(username, usernameText, password, passwordText,ensurepwText,ensurepassword,enroll,quitButton);
        Scene scene = new Scene(pane, width, height);
        this.setScene(scene);

        this.close();
    }
    private Button qButton() {
        //创建退出画板对象
        Button quitButton = new Button("退出");
        quitButton.setLayoutX(380);
        quitButton.setLayoutY(180);


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
