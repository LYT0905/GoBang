package com.chess.ui.web;

import com.chess.Global;
import com.chess.message.ChessMessage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

public class MessageUI extends Stage {
    private final int width = 500;//棋盘的宽度
    private final int height = 500;//棋盘的高度
    public MessageUI(){
        Pane pane = new Pane();

        //创建文本标签
        Label miplabel = new Label("我的IP");
        miplabel.setLayoutX(120);
        miplabel.setLayoutY(50);

        Label mportlabel = new Label("我的端口");
        mportlabel.setLayoutX(120);
        mportlabel.setLayoutY(90);


        Label oiplabel = new Label("对方IP");
        oiplabel.setLayoutX(120);
        oiplabel.setLayoutY(130);

        Label oportlabel = new Label("对方端口");
        oportlabel.setLayoutX(120);
        oportlabel.setLayoutY(170);

        //创建文本框对象
        TextField mipText = new TextField();
        mipText.setLayoutX(190);
        mipText.setLayoutY(50);

        TextField mportText = new TextField();
        mportText.setLayoutX(190);
        mportText.setLayoutY(90);

        TextField oipText = new TextField();
        oipText.setLayoutX(190);
        oipText.setLayoutY(130);


        TextField oportText = new TextField();
        oportText.setLayoutX(190);
        oportText.setLayoutY(170);


        //创建开始按钮
        Button startButton = new Button("开始");
        startButton.setLayoutX(120);
        startButton.setLayoutY(250);
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String mip = mipText.getText();
                int mport = Integer.parseInt(mportText.getText());

                String oip = oipText.getText();
                int oport = Integer.parseInt(oportText.getText());

                Global.mip = mip;
                Global.mport = mport;
                Global.oip = oip;
                Global.oport = oport;

                WebUI webUI = new WebUI();

                webUI.show();

                MessageUI.this.close();


                //接收端

                new Thread(){
                    @Override
                    public void run() {
                        try {
                            ServerSocket ss = new ServerSocket(Global.mport);
                            while (true){
                                Socket s = ss.accept();
                                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                                Object obj = ois.readObject();
                                if (obj instanceof ChessMessage){
                                    //为棋盘添加棋子对象
                                    ChessMessage chessMessage = (ChessMessage)obj;
                                    webUI.updateUI(chessMessage);

                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();


            }
        });

        //创建退出按钮
        Button quitButton = new Button("退出");
        quitButton.setLayoutX(300);
        quitButton.setLayoutY(250);
        quitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                WebUI.this.close();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("退出");
                alert.setContentText("你确定需要退出吗？");
                alert.setHeaderText("是否确认退出？");


                Optional optional = alert.showAndWait();

                if (optional.get() == ButtonType.OK){
                    System.out.println("退出");
                    MessageUI.this.close();
                }else {
                    event.consume();
                }

            }
        });

        pane.getChildren().addAll(miplabel,mportlabel,oiplabel,oportlabel,mipText,mportText,oipText,oportText,startButton,quitButton);

        Scene scene = new Scene(pane,width,height);

        this.setScene(scene);
    }



}
