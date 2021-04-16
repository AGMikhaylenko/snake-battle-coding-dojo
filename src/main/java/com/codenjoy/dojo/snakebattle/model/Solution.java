package com.codenjoy.dojo.snakebattle.model;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.snakebattle.client.Board;

import java.util.ArrayList;

/**
 * Класс, содержащий методы выбора движения бота для игры SnakeBattle
 * https://dojorena.io/events/187
 */
public class Solution {
    private Board board;
    private MySnake mySnake;
    private int xHead; //Координата Х головы своей змеи
    private int yHead; //Координата Y головы своей змеи
    private Helper helper;
    private ArrayList<Snake> enemies; //Список змей противников

    /**
     * Конструктор класса
     *
     * @param board Текущее игровое поле
     */
    public Solution(Board board) {
        this.board = board;
        helper = new Helper(board);
        xHead = board.getMe().getX();
        yHead = board.getMe().getY();
        mySnake = new MySnake(helper.getMyBody(xHead, yHead));
        enemies = helper.getEnemiesSnakes();
    }

    /**
     * Расчет направления следующего шага
     *
     * @param board Текущее игровое поле
     * @return Направление следующего шага
     */
    public Direction getNextStep(Board board) {
        this.board = board;
        helper.setBoard(board);
        //Обновление значений ячеек своей змеи и змей противников
        xHead = board.getMe().getX();
        yHead = board.getMe().getY();
        mySnake.setBody(helper.getMyBody(xHead, yHead));
        enemies = helper.getEnemiesSnakes();

        doChoice();

        mySnake.update(helper.getNextElement(xHead, yHead, mySnake.getHeadDirection()));
        printInfo();
        return mySnake.getHeadDirection();
    }

    /**
     * Печать информации о текущих свойствах объектов
     */
    private void printInfo() {
        System.out.println("Size = " + mySnake.getSize());
        System.out.println("Steps with fury = " + mySnake.getActOfPillFury());
        System.out.println("Steps with fly = " + mySnake.getActOfPillFly());
        System.out.println("Direction = " + mySnake.getHeadDirection());
        System.out.println("Count of enemies = " + enemies.size());
    }

    /**
     * Выбор направления движения в следующем шаге
     * Значение направления передается в объект mySnake
     */
    private void doChoice() {
        //Запрос размера змеи и максимального размера среди противников
        int maxSizeOfEnemy = helper.getMaxSizeOfEnemy(enemies);
        int mySize = mySnake.getSize();

        GoalPoint dFury = helper.searchNearestElement(xHead, yHead, Elements.FURY_PILL);
        GoalPoint dFly = helper.searchNearestElement(xHead, yHead, Elements.FLYING_PILL);
        GoalPoint dApple = helper.searchNearestElement(xHead, yHead, Elements.APPLE);
        GoalPoint dGold = helper.searchNearestElement(xHead, yHead, Elements.GOLD);
        GoalPoint dStone = helper.searchNearestElement(xHead, yHead, Elements.STONE);
        GoalPoint dEnemy = helper.searchNearestElement(xHead, yHead, Helper.ENEMY_FULL);

        if (dApple.getDistance() == 0 && dGold.getDistance() == 0 && dFury.getDistance() == 0 &&
                dFly.getDistance() == 0) {//Тупик
            if (dStone.getDistance() != 0 && (mySnake.isFury() && dStone.getDistance() < mySnake.getActOfPillFury() ||
                    mySnake.isFly() && dStone.getDistance() < mySnake.getActOfPillFly() ||
                    mySize >= 5))//Если есть камень и есть возможность к нему идти
                mySnake.setHeadDirection(dStone.getFirstStep());
            else
                checkNextCell();//Обходим препятствие
        } else {
            if (dFury.getDistance() != 0 && (dFury.getDistance() <= 5 || (dFury.getDistance() <= dApple.getDistance() &&
                    dFury.getDistance() <= dGold.getDistance())))//Если пилюля ярости ближе чем 5 клеток или ближе чем яблоко и золото
                mySnake.setHeadDirection(dFury.getFirstStep()); //Идем к пилюле
            else {
                if (mySnake.isFury() && dEnemy.getDistance() < mySnake.getActOfPillFury())//Если мы под яростью и есть тело змеи рядом
                    mySnake.setHeadDirection(dEnemy.getFirstStep());//Двигаемся в сторону тела противника
                else {
                    if (dApple.getDistance() * 2 < dGold.getDistance()
                            || dGold.getDistance() == 0)//Если поблизости нет золота
                        if ((mySnake.isFury() && mySnake.getActOfPillFury() > dStone.getDistance()) ||
                                mySize > maxSizeOfEnemy + 5)
                            mySnake.setHeadDirection(dStone.getFirstStep());
                        else
                            mySnake.setHeadDirection(dApple.getFirstStep());//Идем к яблоку
                    else
                        mySnake.setHeadDirection(dGold.getFirstStep());//Идем к золоту
                }
            }
        }
    }

    /**
     * Проверка значения следующей ячейки
     * Если ячейка является препятствием, вызов методов для ее обхода
     */
    private void checkNextCell() {
        switch (helper.getNextElement(xHead, yHead, mySnake.getHeadDirection())) {
            case NONE:
                break;
            default:
                switch (mySnake.getHeadDirection()) {
                    case RIGHT:
                    case LEFT:
                        rotateVertical();
                        break;
                    case UP:
                    case DOWN:
                        rotateHorizontal();
                        break;
                    default:
                        break;
                }
        }
    }

    /**
     * Уход от препятствия по горизонтали.
     * Значение направления передается в объект mySnake
     */
    private void rotateHorizontal() {
        if (board.isFreeAt(xHead + 1, yHead) &&
                board.isFreeAt(xHead - 1, yHead))
            //Если можно идти в обе стороны, то идем в центр
            mySnake.setHeadDirection(xHead - board.getField().length / 2 > 0 ?
                    Direction.LEFT : Direction.RIGHT);
        else {
            //Если свободно справа - идем вправо
            if (board.isFreeAt(xHead + 1, yHead))
                mySnake.setHeadDirection(Direction.RIGHT);
            //Если свободно слева - идем влево
            if (board.isFreeAt(xHead - 1, yHead))
                mySnake.setHeadDirection(Direction.LEFT);
            //Если обе стороны закрыты, выбираем меньшее из зол
            if (!board.isFreeAt(xHead + 1, yHead) &&
                    !board.isFreeAt(xHead - 1, yHead)) {
                if (board.isAt(xHead + 1, yHead, Helper.MY_BODY) ||
                        (board.isAt(xHead + 1, yHead, Helper.ENEMY_FULL)
                                && (mySnake.isFury() || mySnake.isFly())) ||
                        (board.isAt(xHead + 1, yHead, Elements.STONE)
                                && (mySnake.isFury() || mySnake.isFly() || mySnake.getSize() >= 5)))
                    mySnake.setHeadDirection(Direction.RIGHT);
                if (board.isAt(xHead - 1, yHead, Helper.MY_BODY) ||
                        (board.isAt(xHead - 1, yHead, Helper.ENEMY_FULL)
                                && (mySnake.isFury() || mySnake.isFly())) ||
                        (board.isAt(xHead - 1, yHead, Elements.STONE)
                                && (mySnake.isFury() || mySnake.isFly() || mySnake.getSize() >= 5)))
                    mySnake.setHeadDirection(Direction.LEFT);
            }
        }
    }

    /**
     * Уход от препятствия по вертикали.
     * Значение направления передается в объект mySnake
     */
    private void rotateVertical() {
        if (board.isFreeAt(xHead, yHead + 1) &&
                board.isFreeAt(xHead, yHead - 1))
            //Если можно идти в обе стороны, то идем в центр
            mySnake.setHeadDirection(yHead - board.getField().length / 2 > 0 ?
                    Direction.DOWN : Direction.UP);
        else {
            //Если свободно сверху - идем наверх
            if (board.isFreeAt(xHead, yHead + 1))
                mySnake.setHeadDirection(Direction.UP);
            //Если свободно снизу - идем вниз
            if (board.isFreeAt(xHead, yHead - 1))
                mySnake.setHeadDirection(Direction.DOWN);
            //Если обе стороны закрыты, выбираем меньшее из зол
            if (!board.isFreeAt(xHead, yHead + 1) &&
                    !board.isFreeAt(xHead, yHead - 1)) {
                if (board.isAt(xHead, yHead + 1, Helper.MY_BODY) ||
                        (board.isAt(xHead, yHead + 1, Helper.ENEMY_FULL)
                                && (mySnake.isFury() || mySnake.isFly())) ||
                        (board.isAt(xHead, yHead + 1, Elements.STONE)
                                && (mySnake.isFury() || mySnake.isFly() || mySnake.getSize() >= 5)))
                    mySnake.setHeadDirection(Direction.UP);
                if (board.isAt(xHead, yHead - 1, Helper.MY_BODY) ||
                        (board.isAt(xHead, yHead - 1, Helper.ENEMY_FULL)
                                && (mySnake.isFury() || mySnake.isFly())) ||
                        (board.isAt(xHead, yHead - 1, Elements.STONE)
                                && (mySnake.isFury() || mySnake.isFly() || mySnake.getSize() >= 5)))
                    mySnake.setHeadDirection(Direction.DOWN);
            }
        }
    }


}
