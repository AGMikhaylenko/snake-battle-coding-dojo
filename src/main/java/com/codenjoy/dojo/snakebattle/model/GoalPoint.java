package com.codenjoy.dojo.snakebattle.model;

import com.codenjoy.dojo.services.Direction;

import java.awt.*;

/**
 * Класс, описывающий структуру целевой точки
 */
public class GoalPoint {

    private Point goal; //Конечная точка
    private Direction firstStep; //Направление движения первого шага к точке
    private int distance; //Расстояние до точки

    /**
     * Конструктор класса
     * @param goal Конечная точка
     * @param firstStep Направление движения первого шага к точке
     * @param value Расстояние до точки
     */
    public GoalPoint(Point goal, Direction firstStep, int value) {
        this.goal = goal;
        this.firstStep = firstStep;
        this.distance = value;
    }

    public Direction getFirstStep() {
        return firstStep;
    }

    public int getDistance() {
        return distance;
    }

    public Point getGoal() {
        return goal;
    }
}
