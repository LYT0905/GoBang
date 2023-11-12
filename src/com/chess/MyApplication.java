package com.chess;

import com.chess.ui.single.SingleUI;
import com.chess.ui.web.EnrollUI;
import com.chess.ui.web.LoginUI;
import com.chess.ui.web.MessageUI;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.application.Application;

import java.sql.*;
import java.util.Optional;
import java.util.Scanner;

public class MyApplication extends Application{
    private final int width = 560;//棋盘的宽度
    private final int height = 600;//棋盘的高度
    @Override
    public void start(Stage stage) throws Exception {
        Pane pane = new Pane();
        pane.setBackground(new Background(new BackgroundFill(Color.ORANGE,null,null)));


        //创建登录画板
        Button loginButton = new Button("登录");
        loginButton.setPrefSize(100,50);
        loginButton.setLayoutX(90);
        loginButton.setLayoutY(350);
        loginButton.setBackground(new Background(new BackgroundFill(Color.WHITE,null,null)));

        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                LoginUI lu = new LoginUI();
                lu.show();
                stage.close();

            }
        });


        //创建注册画板
        Button enrollButton = new Button("注册");
        enrollButton.setPrefSize(100,50);
        enrollButton.setLayoutX(225);
        enrollButton.setLayoutY(350);
        enrollButton.setBackground(new Background(new BackgroundFill(Color.WHITE,null,null)));
        enrollButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                EnrollUI enrollUI = new EnrollUI();
                enrollUI.show();
                stage.close();
            }
        });

        Button singleButton = sButton(stage);

        Button quitButton = qButton();

        pane.getChildren().addAll(singleButton,quitButton,enrollButton,loginButton);
        Scene scene = new Scene(pane,width,height);
        stage.setScene(scene);
        stage.show();
    }

    private Button qButton() {
        //创建退出画板对象
        Button quitButton = new Button("退出");
        quitButton.setPrefSize(100,50);
        quitButton.setLayoutX(360);
        quitButton.setLayoutY(350);
        quitButton.setBackground(new Background(new BackgroundFill(Color.WHITE,null,null)));


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
    private Button sButton(Stage stage) {
        //      创建单机版画板对象
        Button singleButton = new Button("单机版");
        singleButton.setPrefSize(150,100);
        singleButton.setLayoutX(180);
        singleButton.setLayoutY(150);
        singleButton.setBackground(new Background(new BackgroundFill(Color.WHITE,null,null)));

        singleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SingleUI singleUI = new SingleUI();
                singleUI.show();
                stage.close();
            }
        });
        return singleButton;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
