package com.chess.message;

import java.io.Serializable;

public class ChessMessage implements Serializable {
    private int x;
    private int y;
    private boolean isBlack;


    public ChessMessage() {
        super();
    }

    public ChessMessage(int x,int y ,boolean isBlack)
    {
        this.x = x;
        this.y = y;
        this.isBlack = isBlack;
    }


    public int get_x() {
        return x;
    }

    public int get_y() {
        return y;
    }

    public void set_x(int x) {
        this.x = x;
    }

    public void set_y(int y) {
        this.y = y;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public void setBlack(boolean black) {
        isBlack = black;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }


    @Override
    public String toString() {
        return super.toString();
    }
}
