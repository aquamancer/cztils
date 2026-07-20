package com.aquamancer.cztils.hud;

public class Rectangle {
    int x, y, w, h;
    int x2, y2;
    public Rectangle(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.x2 = x+w;
        this.y2 = y+h;
    }
}
