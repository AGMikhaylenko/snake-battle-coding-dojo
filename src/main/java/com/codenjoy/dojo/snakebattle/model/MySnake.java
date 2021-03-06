package com.codenjoy.dojo.snakebattle.model;

import com.codenjoy.dojo.services.Direction;

import java.awt.*;
import java.util.ArrayList;

/**
 * Класс описывающий змею игрока (бота)
 */
public class MySnake extends Snake {
    private int actOfPillFly; //Остаток ходов действия пилюли полета
    private int actOfPillFury; //Остаток ходов действия пилюли ярости
    private Direction headDirection; //Направление движения
    private int countOfStones; //Количество камней, которые можно сбросить

    /**
     * Конструктор класса
     *
     * @param body Список точек, составляющих тело змеи
     */
    public MySnake(ArrayList<Point> body) {
        this.body = body;
        isFly = isFury = false;
        actOfPillFly = actOfPillFury = countOfStones = 0;
        headDirection = Direction.RIGHT;
    }

    /**
     * Обновление значений полей змеи
     *
     * @param nextElement Следующий элемент по ходу движения
     * @param action      Сбрасывается ли камень на следующем ходу
     */
    public void update(Elements nextElement, boolean action) {
        if (isFly && --actOfPillFly == 0)
            isFly = false;

        if (isFury && --actOfPillFury == 0)
            isFury = false;

        if (action)
            countOfStones--;

        switch (nextElement) {
            case FLYING_PILL:
                isFly = true;
                actOfPillFly = 10;
                break;
            case FURY_PILL:
                isFury = true;
                actOfPillFury = 10;
                break;
            case STONE:
                if (!isFly)
                    countOfStones++;
                break;
        }
    }

    /**
     * Сравнение сил с другой змеей
     *
     * @param enemy Вторая змея (противник)
     * @return Возможные значения: объект сильнее другой змеи - >0, другая змея сильнее - <0, силы равны = 0
     */
    public int compareTo(Snake enemy) {
        if (this.isFury && actOfPillFury > 1) {
            if (enemy.isFury) {
                return this.getSize() - enemy.getSize() - 2;
            } else {
                return 1;
            }
        } else {
            if (enemy.isFury) {
                return -1;
            } else {
                return this.getSize() - enemy.getSize() - 2;
            }
        }
    }

    public int getActOfPillFly() {
        return actOfPillFly;
    }

    public int getActOfPillFury() {
        return actOfPillFury;
    }

    public Direction getHeadDirection() {
        return headDirection;
    }

    public void setHeadDirection(Direction headDirection) {
        this.headDirection = headDirection;
    }

    public void setBody(ArrayList<Point> body) {
        this.body = body;
    }

    public int getCountOfStones() {
        return countOfStones;
    }
}
