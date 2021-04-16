package com.codenjoy.dojo.snakebattle.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * Класс, описывающий змею в игре SnakeBattle
 * https://dojorena.io/events/187
 */
public class Snake {
    protected ArrayList<Point> body;//Список точек, принадлежащих змее, где 0 - голова
    protected boolean isFly;
    protected boolean isFury;

    /**
     * Конструктор класса
     */
    public Snake() {
        this.body = new ArrayList<>();
    }

    /**
     * Конструктор класса
     *
     * @param body Список точек, составляющих тело змеи
     * @param head Элемент головы змеи
     */
    public Snake(ArrayList<Point> body, Elements head) {
        this.body = body;
        switch (head) {
            case HEAD_FLY:
            case ENEMY_HEAD_FLY:
                isFly = true;
                break;
            case HEAD_EVIL:
            case ENEMY_HEAD_EVIL:
                isFury = true;
                break;
            default:
                break;
        }
    }

    /**
     * Получение координат головы змеи
     *
     * @return Точка, содержащая координаты головы змеи
     * Если у змеи отсутствуют точки тела, координаты возвращаемой точки равны -1
     */
    public Point getHeadPoint() {
        if (body.size() > 0)
            return body.get(0);
        else
            return new Point(-1, -1);
    }

    public ArrayList<Point> getBody() {
        return body;
    }

    public int getSize() {
        return body.size();
    }

    public boolean isFly() {
        return isFly;
    }

    public boolean isFury() {
        return isFury;
    }

}
