package com.chess.ui.web;

import com.chess.Global;
import com.chess.domain.Chess;
import com.chess.message.ChessMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;


/**
 * 单个用户界面
 *
 * @author Mark
 * @date 2023/10/13
 */
public class WebUI extends Stage {
    private final int width = 560;//棋盘的宽度
    private final int height = 600;//棋盘的高度
    private final int padding = 40;//棋盘中线与线之间的距离
    private final int margin = 20;//棋盘中边线距离棋盘边的距离
    private final int lineCount = 14;//棋牌中水平线和垂直线的个数
    private boolean isBlack = true;
    private Chess[] chesses = new Chess[lineCount*lineCount];
    private int count=0;
    Pane pane = null;
    Stage stage = null;
    private boolean isWin = false;
    private int isWinCount=1;
    private boolean canPlay = true;//判断是否可以下棋

    public WebUI(){
        this.pane = getPane();
        this.stage = this;
        moveInChess();
        //创建场景对象，并将画板放到场景上
        Scene scene = new Scene(pane, width, height);

        //将场景设置到舞台上
        stage.setScene(scene);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
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
        //展示大舞台
        stage.show();
    }



    /**
     * 在国际象棋中移动
     */
    private void moveInChess() {
        //给画板对象，绑定鼠标点击事件，我们用一点击画板，就会执行某些动作
        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(isWin || !canPlay){
                    return;
                }
                //获取鼠标点击的那个位置的x和y的坐标
                double x = event.getX();
                double y = event.getY();
//                System.out.println(x+"..."+y);
                if(!(x>=20&&x<=540&&y>=20&&y<=540)){
                    return;
                }
                int _x = ((int) x - margin + padding/2)/padding;
                int _y = ((int) y - margin + padding/2)/padding;
                System.out.println(_x+"..."+_y);
                //判断是否有棋子
                if((isChess(_x,_y))){
                    System.out.println("有棋子");
                    return;
                }
                //创建圆圈对象，并且设置一些参数
                Circle circle = null;
                //创建棋子对象
                Chess chess = null;
                if(isBlack){
                    circle = new Circle(_x * padding +margin,_y*padding + margin,10, Color.BLACK);
                    isBlack = false;
                    chess = new Chess(_x,_y,Color.BLACK);
                }else{
                    circle = new Circle(_x * padding +margin,_y*padding + margin,10, Color.WHITE);
                    isBlack = true;
                    chess = new Chess(_x,_y,Color.WHITE);
                }
                pane.getChildren().add(circle);
                //将棋子放入容器
                chesses[count] = chess;
                count++;

                canPlay = false;

                //发送端
                Socket s = null;
                try {
                    s = new Socket(Global.oip,Global.oport);
                    ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                    oos.writeObject(new ChessMessage(_x,_y,!isBlack));

                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (s != null){
                        try {
                            s.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(isWin(chess)){
                    //弹出框
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    //设置内容
                    alert.setTitle("Win");//标题
                    alert.setHeaderText("恭喜你胜利了");//头内容
                    alert.setContentText("Win");//文本内容
                    //展示弹框
                    alert.showAndWait();
                    isWin=true;
                }
            }
        });
    }


    /**
     * 是否赢
     *
     * @param chess 棋
     * @return boolean
     */
    private boolean isWin(Chess chess){
        int x = chess.getX();
        int y = chess.getY();
        //向上
        for (int i = y-1;i>=y-4 && i>0;i--){
            Chess chess1 = getChess(x,i);
            if(chess1!=null&&chess.getColor().equals(chess1.getColor())){
                isWinCount++;
            }else {
                break;
            }
        }
        //向下
        for (int i = y+1;i<=y+4 && i<13;i++){
            Chess chess1 = getChess(x,i);
            if(chess1!=null&&chess.getColor().equals(chess1.getColor())){
                isWinCount++;
            }else {
                break;
            }
        }
        //右边
        for (int i = x+1;i<=x+4&&i<13;i++){
            Chess chess1 = getChess(i,y);
            if(chess1!=null&&chess.getColor().equals(chess1.getColor())){
                isWinCount++;
            }else {
                break;
            }
        }
        //左边
        for (int i = x-1;i>=x-4&&i>0;i--){
            Chess chess1 = getChess(i,y);
            if(chess1!=null&&chess.getColor().equals(chess1.getColor())){
                isWinCount++;
            }else {
                break;
            }
        }
        //右上左下
        for (int i = x + 1, h = y - 1; i <= 13 && h >= 0; i++, h--) {
            Chess chess1 = getChess(i,h);
            if (chess1!=null&&chess.getColor().equals(chess1.getColor())) {
                isWinCount++;
            }
            else {
                break;
            }
        }
        for (int i = x - 1, h = y + 1; i >= 0 && h <= 13; i--, h++) {
            Chess chess1 = getChess(i,h);
            if (chess1!=null&&chess.getColor().equals(chess1.getColor())) {
                isWinCount++;
            }
            else {
                break;
            }
        }
        //左上右下
        for (int i = x - 1, h = y - 1; i <= 13 && h <= 13; i--, h--) {
            Chess chess1 = getChess(i,h);
            if (chess1!=null&&chess.getColor().equals(chess1.getColor())) {
                isWinCount++;
            }
            else {
                break;
            }
        }
        for (int i = x + 1, h = y + 1; i >= 0 && h >= 0; i++, h++) {
            Chess chess1 = getChess(i,h);
            if (chess1!=null&&chess.getColor().equals(chess1.getColor())) {
                isWinCount++;
            }
            else {
                break;
            }
        }

        //判断是否超过五个
        if(isWinCount>=5){
            isWinCount = 1;
            return true;
        }
        isWinCount=1;
        return false;
    }

    /**
     * 获取国际象棋
     *
     * @param x x
     * @param y y
     * @return {@link Chess}
     */
    private Chess getChess(int x,int y){
        for(int i = 0;i<count;i++){
            Chess chess = chesses[i];
            if(chess.getX()==x&&chess.getY()==y){
                return chess;
            }
        }
        return null;
    }

    /**
     * 判断是否有落子
     *
     * @param x x
     * @param y y
     * @return boolean
     */
    private boolean isChess(int x,int y){
        for(int i = 0;i<count;i++){
            Chess chess = chesses[i];
            if(chess!=null&&chess.getX()==x&&chess.getY()==y){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取窗格
     *
     * @return {@link Pane}
     */
    private Pane getPane() {
        //创建画板
        Pane pane = new Pane();
        //画板背景
        pane.setBackground(new Background(new BackgroundFill(Color.ORANGE,null,null)));
        //创建线条对象
        int increment = 0;
        for(int i = 0; i < lineCount; i++) {
            Line rowLine = new Line(margin, margin + increment, width - margin, margin + increment);
            Line colLine = new Line(margin + increment, margin, margin + increment,  width - margin);

            //将线条放到画板中
            pane.getChildren().add(rowLine);
            pane.getChildren().add(colLine);

            increment += padding;
        }

        //创建再来一局画板对象
        Button startButton = getStartButton();
        //添加到画板上面
        pane.getChildren().add(startButton);

        //创建悔棋画板对象
        Button regretButton = getRegretButton();
        //添加到画板上面
        pane.getChildren().add(regretButton);

        //创建保存画板对象
        Button saveButton = getSaveButton();
        //添加到画板上面
        pane.getChildren().add(saveButton);

        //创建退出画板对象
        Button quitButton = getQuitButton();
        //添加到画板上面
        pane.getChildren().add(quitButton);


        //创建打谱画板对象
        Button scoreButton = getScoreButton();
        //添加到画板上面
        pane.getChildren().add(scoreButton);

        //创建文本标签
        Label lable = new Label();
        pane.getChildren().add(lable);
        Timer timer = new Timer();
        lable.setLayoutX(200);
        lable.setLayoutY(0);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //获取当前日期
                LocalDateTime localDateTime =LocalDateTime.now();
                //格式化日期
                DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String time = localDateTime.format(pattern);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lable.setText(time);
                    }
                });
            }
        },0,1000);

        return pane;
    }


    /**
     * “获取开始”按钮
     *
     * @return {@link Button}
     */
    private Button getStartButton() {
        //创建再来一局
        Button startButton = new Button("再来一局");
        startButton.setPrefSize(80,30);
        //放好地方,并放入画板
        startButton.setLayoutX(20);
        startButton.setLayoutY(550);


        //给按钮绑定点击事件
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!isWin){
                    return;
                }
                pane.getChildren().removeIf(new Predicate<Object>() {
                    @Override
                    public boolean test(Object t) {
                        return t instanceof Circle;
                    }
                });
                //清空数组
                chesses = new Chess[lineCount*lineCount];
                //清空计数器
                count = 0;
                //胜负标记改为false
                isWin = false;
                //改回默认黑棋
                isBlack = true;
            }
        });
        return startButton;
    }


    /**
     * “后悔”按钮
     *
     * @return {@link Button}
     */
    private Button getRegretButton() {
        //创建悔棋
        Button regretButton = new Button("悔棋");
        regretButton.setPrefSize(80,30);
        //放好地方,并放入画板
        regretButton.setLayoutX(130);
        regretButton.setLayoutY(550);
        regretButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (isWin){
                    return;
                }
                Iterator it=pane.getChildren().iterator();
                Object obj=null;
                while (it.hasNext()){
                    obj=it.next();
                }
                if (obj instanceof Circle){
                    it.remove();
                }
                if (!isBlack)
                {
                    isBlack=true;
                }
                else{
                    isBlack=false;
                }
                chesses =new Chess[lineCount*lineCount];
                count = 0;
                isWin=false;
            }

        });
//        pane.getChildren().add(regretButton);
        return regretButton;
    }

    /**
     * “获取保存”按钮
     *
     * @return {@link Button}
     */
    private Button getSaveButton() {
        //创建保存
        Button saveButton = new Button("保存");
        saveButton.setPrefSize(80,30);
        //放好地方,并放入画板
        saveButton.setLayoutX(245);
        saveButton.setLayoutY(550);

        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!isWin){
                    return;
                }

                //创建保存框对象
                FileChooser fileChooser = new FileChooser();
                //展示
                File file = fileChooser.showSaveDialog(stage);

                if (file != null){
                    //创建高效字符输出流对象
                    BufferedWriter bw = null;
                    try {
                        bw = new BufferedWriter(new FileWriter(file));
                        for (int i = 0;i<count;i++){
                            Chess chess = chesses[i];
                            bw.write(chess.getX()+","+chess.getY()+","+chess.getColor());
                            bw.newLine();
                            bw.flush();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }finally{
                        if (bw != null){
                            try {
                                bw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        return saveButton;
    }


    //未完成
    /**
     * “打谱按钮”按钮
     *
     * @return {@link Button}
     */
    private Button getScoreButton() {
        //创建打谱
        Button scoreButton = new Button("打谱");
        scoreButton.setPrefSize(80,30);
        //放好地方,并放入画板
        scoreButton.setLayoutX(350);
        scoreButton.setLayoutY(550);
        scoreButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //创建保存框对象
                FileChooser fileChooser = new FileChooser();
                //展示
                File file = fileChooser.showOpenDialog(stage);
                BufferedReader br = null;
                BufferedWriter bw = null;
                try {
                    br =new BufferedReader(new FileReader(file));
                    bw = new BufferedWriter(new FileWriter(file));
                    int ch = 0;
                    while((ch = br.read())!=-1){
                        bw.write(ch);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (br!=null){
                        try {
                            br.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        return scoreButton;
    }


    /**
     * “获取退出”按钮
     *
     * @return {@link Button}
     */
    private Button getQuitButton() {
        //创建退出
        Button quitButton = new Button("退出");
        quitButton.setPrefSize(80,30);
        //放好地方,并放入画板
        quitButton.setLayoutX(450);
        quitButton.setLayoutY(550);

        //给按钮绑定点击事件
        quitButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
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

    public  void updateUI(ChessMessage chessMessage) {
        canPlay = true;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(isWin){
                    return;
                }

                int _x = chessMessage.get_x();
                int _y = chessMessage.get_y();

                //创建圆圈对象，并且设置一些参数
                Circle circle = null;
                //创建棋子对象
                Chess chess = null;
                if(chessMessage.isBlack()){
                    circle = new Circle(_x * padding +margin,_y*padding + margin,10, Color.BLACK);
                    isBlack = false;
                    chess = new Chess(_x,_y,Color.BLACK);
                }else{
                    circle = new Circle(_x * padding +margin,_y*padding + margin,10, Color.WHITE);
                    isBlack = true;
                    chess = new Chess(_x,_y,Color.WHITE);
                }
                pane.getChildren().add(circle);
                //将棋子放入容器
                chesses[count] = chess;
                count++;


                if(isWin(chess)){
                    //弹出框
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    //设置内容
                    alert.setTitle("Win");//标题
                    alert.setHeaderText("恭喜你胜利了");//头内容
                    alert.setContentText("Win");//文本内容
                    //展示弹框
                    alert.showAndWait();
                    isWin=true;
                }
            }
        });
    }


}
