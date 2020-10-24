package com.netease.nim.demo.rts;

/**
 * 形状枚举
 */
public enum ActionTypeEnum {
    UnKnow(0),
    Point(1),
    Path(2),
    Line(3),
    Rect(4),
    Circle(5),
    FilledRect(6),
    FilledCircle(7);

    private int value;

    ActionTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ActionTypeEnum typeOfValue(int value) {
        for (ActionTypeEnum e : values()) {
            if (e.getValue() == value) {
                return e;
            }
        }
        return UnKnow;
    }
}