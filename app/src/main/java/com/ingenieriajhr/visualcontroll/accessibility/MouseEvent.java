package com.ingenieriajhr.visualcontroll.accessibility;


/**
 * Created by chetan on 30/04/15.
 */
public class MouseEvent {

    public static final int
            MOVE_UP = 0;
    public static final int MOVE_DOWN = 1;
    public static final int MOVE_LEFT = 2;
    public static final int MOVE_RIGHT = 3;
    public static final int LEFT_CLICK = 4;

    public final int direction;

    public MouseEvent(int direction) {
        this.direction = direction;
    }
}