package com.codenjoy.dojo.snakebattle.model;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.snakebattle.client.Board;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;

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
    private int distanceToEnemy; //Дистанция до ближайшего соперника
    private int numberOfStep; //Номер хода в раунде
    private boolean nextStepIsAct; //Метка ставить ли камень на следующем ходе

    private final String ACT = ",ACT";

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
        numberOfStep = 0;
        nextStepIsAct = false;
    }

    /**
     * Расчет направления следующего шага
     *
     * @param board Текущее игровое поле
     * @return Направление следующего шага
     */
    public String getNextStep(Board board) {
        long millis = System.currentTimeMillis();
        this.board = board;
        helper.setBoard(board);
        //Обновление значений ячеек своей змеи и змей противников
        xHead = board.getMe().getX();
        yHead = board.getMe().getY();
        mySnake.setBody(helper.getMyBody(xHead, yHead));
        enemies = helper.getEnemiesSnakes();

        doChoice();

        boolean isAction = false;//Нужно ли скидывать камень на этом ходу

        //Проверка возможности поставить первый камень
        if (helper.tailIsSurrounded() && mySnake.getCountOfStones() > 1 && !nextStepIsAct && mySnake.getSize() >= 8) {
            nextStepIsAct = true;
            isAction = true;
        }else {
            //Проверка  нужно ли ставить второй камень
            if (nextStepIsAct) {
                isAction = true;
                nextStepIsAct = false;
            }
        }

        mySnake.update(helper.getNextElement(xHead, yHead, mySnake.getHeadDirection()), isAction);
        numberOfStep++;

        String answer = mySnake.getHeadDirection().toString();
        if (isAction)
            answer += ACT;

        printInfo(System.currentTimeMillis() - millis, answer);

        return answer;
    }

    /**
     * Печать информации о текущих характеристик объектов
     */
    private void printInfo(long time, String answer) {
        System.out.println(new Date(System.currentTimeMillis()));
        System.out.println("Time = " + time);
        System.out.println("Step № " + numberOfStep);
        System.out.println("Size = " + mySnake.getSize());
        System.out.println("Max size of enemy = " + helper.getMaxSizeOfEnemy(enemies));
        System.out.println("Steps with fury = " + mySnake.getActOfPillFury());
        System.out.println("Steps with fly = " + mySnake.getActOfPillFly());
        System.out.println("Count of stones = " + mySnake.getCountOfStones());
        System.out.println("Count of enemies = " + enemies.size());
        System.out.println("Direction = " + answer);
    }

    /**
     * Выбор направления движения в следующем шаге
     * Значение направления передается в объект mySnake
     * Выбор осуществляется на основании приоритетов: 1. Съесть соперника 2. Пилюля ярости 3. Пилюля полета 4. Золото
     * 5. Камень 6. Яблоко 7. Уход от столкновения
     */
    private void doChoice() {
        GoalPoint dFury = helper.searchNearestElement(mySnake, enemies, Elements.FURY_PILL);
        GoalPoint dFly = helper.searchNearestElement(mySnake, enemies, Elements.FLYING_PILL);
        GoalPoint dApple = helper.searchNearestElement(mySnake, enemies, Elements.APPLE);
        GoalPoint dGold = helper.searchNearestElement(mySnake, enemies, Elements.GOLD);
        GoalPoint dStone = helper.searchNearestElement(mySnake, enemies, Elements.STONE);
        GoalPoint dEnemy = helper.searchNearestElement(mySnake, enemies, Helper.ENEMY_FULL);

        if (checkOpportunityEat(dEnemy)) { //Если есть возможность съесть змею противника
            mySnake.setHeadDirection(dEnemy.getFirstStep()); //Идем к змее противника

        } else {
            if (dStone.getDistance() != 0 && !helper.isDeadEnd(dStone, mySnake, enemies) && helper.checkHeadEnemy(dStone, mySnake, enemies)
                    && checkOpportunityStone(dStone) && //Если есть возможность съесть камень
                    (dStone.getDistance() * 1.5 < dGold.getDistance() || dGold.getDistance() == 0) && //Он ближе чем золото
                    (dStone.getDistance() <= dFly.getDistance() || dFly.getDistance() == 0) && //Он ближе чем полет
                    (dStone.getDistance() * 2 < dFury.getDistance() || dFury.getDistance() == 0 && //Он ближе чем ярость
                            (dStone.getDistance() < dApple.getDistance() * 3 || dApple.getDistance() == 0))) { //Не дальше чем яблоко в 3 раз
                mySnake.setHeadDirection(dStone.getFirstStep()); //Идем к камню

            } else {
                if (dFury.getDistance() != 0 && !helper.isDeadEnd(dFury, mySnake, enemies) && helper.checkHeadEnemy(dFury, mySnake, enemies) &&
                        (dFury.getDistance() <= 5 || ( //Если ярость в радиусе 5 клеток
                                (dFury.getDistance() < dFly.getDistance() * 1.5 || dFly.getDistance() == 0) && //Если не дальше полета в 1,5 раза
                                        (dFury.getDistance() * 1.5 < dGold.getDistance() || dGold.getDistance() == 0) && //Ближе золота
                                        (dFury.getDistance() < dApple.getDistance() * 2 || dApple.getDistance() == 0)))) { //Ближе яблока
                    mySnake.setHeadDirection(dFury.getFirstStep());//Идем к пилюле ярости

                } else {
                    if (dFly.getDistance() != 0 && !helper.isDeadEnd(dFly, mySnake, enemies) &&
                            helper.checkHeadEnemy(dFly, mySnake, enemies) && //Проверка пилюли полета
                            (dFly.getDistance() * 1.2 < dGold.getDistance() || dGold.getDistance() == 0) && //Если ближе золота
                            (dFly.getDistance() < dApple.getDistance() || dApple.getDistance() == 0)) { //Не дальше чем яблоко
                        mySnake.setHeadDirection(dFly.getFirstStep());//Идем к пилюле полета

                    } else {
                        if (dGold.getDistance() != 0 && !helper.isDeadEnd(dGold, mySnake, enemies) &&
                                helper.checkHeadEnemy(dGold, mySnake, enemies) && //Проверка золота
                                (dGold.getDistance() < dApple.getDistance() * 2)) { //Если золото ближе яблока
                            mySnake.setHeadDirection(dGold.getFirstStep());//Идем к золоту

                        } else {
                            if (dApple.getDistance() != 0 && !helper.isDeadEnd(dApple, mySnake, enemies) &&
                                    helper.checkHeadEnemy(dApple, mySnake, enemies)) { //Если есть яблоко
                                mySnake.setHeadDirection(dApple.getFirstStep());//Идем к яблоку

                            } else { //Если безопасных путей к полезным клеткам нет
                                checkNextCell(); //Проверяем следующую клетку и, при необходимости, обходим препятствие
                            }
                        }
                    }
                }
            }
        }

        distanceToEnemy = dEnemy.getDistance();
    }

    /**
     * Проверка возможности съесть противника
     *
     * @param dEnemy Точка с противником
     * @return Результат проверки: True - возможность есть, False - возможности нет
     */
    private boolean checkOpportunityEat(GoalPoint dEnemy) {
        return mySnake.isFury &&
                !helper.getSnakeByPoint(enemies, dEnemy.getGoal()).isFury() &&
                !helper.getSnakeByPoint(enemies, dEnemy.getGoal()).isFly() &&
                dEnemy.getDistance() < mySnake.getActOfPillFury() &&
                dEnemy.getDistance() != distanceToEnemy;
    }

    /**
     * Проверка возможности съесть камень
     *
     * @param dStone Точка с камнем
     * @return Результат проверки: True - возможность есть, False - возможности нет
     */
    private boolean checkOpportunityStone(GoalPoint dStone) {
        int maxSizeOfEnemy = helper.getMaxSizeOfEnemy(enemies);
        int mySize = mySnake.getSize();
        return (mySnake.isFury() && mySnake.getActOfPillFury() > dStone.getDistance() ||//Если мы змея под яростью
                mySize > maxSizeOfEnemy + 7 && numberOfStep < 250 ) //Или больше самого крупного соперника и до конца больше 50 ходов
                //mySnake.getSize() > 5 && numberOfStep < 50) //Или с начала раунда прошло не более 50 ходов
                && mySnake.getActOfPillFly() < dStone.getDistance(); //И не находится под действием пилюли полета
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
                //Если слева и справа змея, проверяем на наличие голов
                //Ситуация возникает, если "разрезать змею"
                if (board.isAt(xHead + 1, yHead, Helper.ENEMY_FULL) && board.isAt(xHead - 1, yHead, Helper.ENEMY_FULL)) {
                    //Идем вправо, если у тела змеи справа нет головы
                    if (helper.getSnakeByPoint(enemies, new Point(xHead + 1, yHead)).getBody().isEmpty())
                        mySnake.setHeadDirection(Direction.RIGHT);
                    //Идем влево, если у тела змеи слева нет головы
                    if (helper.getSnakeByPoint(enemies, new Point(xHead - 1, yHead)).getBody().isEmpty())
                        mySnake.setHeadDirection(Direction.LEFT);
                } else {
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
                //Если сверху и снизу змея, проверяем на наличие голов
                //Ситуация возникает, если "разрезать змею"
                if (board.isAt(xHead, yHead + 1, Helper.ENEMY_FULL) && board.isAt(xHead, yHead - 1, Helper.ENEMY_FULL)) {
                    //Идем вверх, если у тела змеи сверху нет головы
                    if (helper.getSnakeByPoint(enemies, new Point(xHead, yHead + 1)).getBody().isEmpty())
                        mySnake.setHeadDirection(Direction.UP);
                    //Идем вниз, если у тела змеи снизу нет головы
                    if (helper.getSnakeByPoint(enemies, new Point(xHead, yHead - 1)).getBody().isEmpty())
                        mySnake.setHeadDirection(Direction.DOWN);
                } else {
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


}
