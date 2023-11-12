package com.chess.domain;


import javafx.scene.paint.Color;

public class Chess {
    int x;
    int y;
    Color color;
    public Chess(){}
    public Chess(int x,int y,Color color){
        this.x = x;
        this.y = y;
        this.color = color;
    }
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }
    public void setColor(Color color){
        this.color = color;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public Color getColor(){
        return color;
    }
}
