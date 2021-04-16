package com.codenjoy.dojo.snakebattle.model;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.snakebattle.client.Board;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Класс, содержащий инструменты для расчета игровых ситуаций
 */
public class Helper {

    public final static Elements[] MY_BODY = {Elements.BODY_HORIZONTAL, Elements.BODY_VERTICAL, Elements.BODY_LEFT_DOWN,
            Elements.BODY_LEFT_UP, Elements.BODY_RIGHT_DOWN, Elements.BODY_RIGHT_UP, Elements.TAIL_END_DOWN,
            Elements.TAIL_END_LEFT, Elements.TAIL_END_RIGHT, Elements.TAIL_END_UP, Elements.TAIL_INACTIVE};

    public final static Elements[] ENEMY_BODY = {Elements.ENEMY_BODY_HORIZONTAL, Elements.ENEMY_BODY_VERTICAL,
            Elements.ENEMY_BODY_LEFT_DOWN, Elements.ENEMY_BODY_LEFT_UP, Elements.ENEMY_BODY_RIGHT_DOWN,
            Elements.ENEMY_BODY_RIGHT_UP, Elements.ENEMY_TAIL_END_DOWN, Elements.ENEMY_TAIL_END_LEFT,
            Elements.ENEMY_TAIL_END_UP, Elements.ENEMY_TAIL_END_RIGHT, Elements.ENEMY_TAIL_INACTIVE};

    public final static Elements[] ENEMY_HEAD = {Elements.ENEMY_HEAD_DOWN, Elements.ENEMY_HEAD_LEFT,
            Elements.ENEMY_HEAD_RIGHT, Elements.ENEMY_HEAD_UP, Elements.ENEMY_HEAD_DEAD, Elements.ENEMY_HEAD_EVIL,
            Elements.ENEMY_HEAD_FLY};

    public final static Elements[] ENEMY_FULL = {Elements.ENEMY_BODY_HORIZONTAL, Elements.ENEMY_BODY_VERTICAL,
            Elements.ENEMY_BODY_LEFT_DOWN, Elements.ENEMY_BODY_LEFT_UP, Elements.ENEMY_BODY_RIGHT_DOWN,
            Elements.ENEMY_BODY_RIGHT_UP, Elements.ENEMY_TAIL_END_DOWN, Elements.ENEMY_TAIL_END_LEFT,
            Elements.ENEMY_TAIL_END_UP, Elements.ENEMY_TAIL_END_RIGHT, Elements.ENEMY_HEAD_DOWN, Elements.ENEMY_HEAD_LEFT,
            Elements.ENEMY_HEAD_RIGHT, Elements.ENEMY_HEAD_UP, Elements.ENEMY_HEAD_DEAD, Elements.ENEMY_HEAD_EVIL,
            Elements.ENEMY_HEAD_FLY, Elements.ENEMY_TAIL_INACTIVE};

    private Board board;
    private final int sizeOfBoardX; //Ширина игрового поля
    private final int sizeOfBoardY; //Высота игрового поля

    final int[] dx = {1, -1, 0, 0};//Вспомогательный массив для расчета соседних точек
    final int[] dy = {0, 0, 1, -1};//Вспомогательный массив для расчета соседних точек

    /**
     * Конструктор класса
     *
     * @param board Текущее игровое поле
     */
    public Helper(Board board) {
        this.board = board;
        sizeOfBoardX = board.getField().length;
        sizeOfBoardY = board.getField()[0].length;
    }

    /**
     * Расчет максимального размера змеи среди противников
     *
     * @param enemies Список змей противников
     * @return Максимальный размер змеи противников.
     * В случае, если змеи противников отсутствуют, максимальный размер равен 0
     */
    public int getMaxSizeOfEnemy(ArrayList<Snake> enemies) {
        int maxSize = 0;
        for (Snake s : enemies) {
            if (maxSize < s.getSize())
                maxSize = s.getSize();
        }
        return maxSize;
    }

    /**
     * Получение списка змей противников
     *
     * @return Список змей противников
     */
    public ArrayList<Snake> getEnemiesSnakes() {
        ArrayList<Snake> enemies = new ArrayList<>();
        for (int i = 0; i < sizeOfBoardX; i++)
            for (int j = 0; j < sizeOfBoardY; j++)
                if (board.isAt(i, j, ENEMY_HEAD))
                    enemies.add(new Snake(getBodySnakeEnemy(i, j), board.getAt(i, j)));
        return enemies;
    }

    /**
     * Получение списка точек, из которых состоит своя змея
     *
     * @param xHead Х координата головы змеи
     * @param yHead Y координата головы змеи
     * @return Список точек, относящихся к телу змеи
     */
    public ArrayList<Point> getMyBody(int xHead, int yHead) {
        ArrayList<Point> body = new ArrayList<>();
        body.add(new Point(xHead, yHead));
        for (int i = 0; i < sizeOfBoardX; i++)
            for (int j = 0; j < sizeOfBoardY; j++)
                if (board.isAt(i, j, MY_BODY))
                    body.add(new Point(i, j));
        return body;
    }

    /**
     * Расчет точек, относящихся к телу змеи противника
     *
     * @param xHead Х координата головы змеи противника
     * @param yHead Y координата головы змеи противника
     * @return Список точек, относящихся к телу змеи
     */
    private ArrayList<Point> getBodySnakeEnemy(int xHead, int yHead) {
        int[][] temp = new int[sizeOfBoardX][sizeOfBoardY];
        for (int[] row : temp)
            Arrays.fill(row, 0);
        temp[xHead][yHead] = 1;

        ArrayList<Point> body = new ArrayList<>();
        ArrayList<Point> queue = new ArrayList<>();
        body.add(new Point(xHead, yHead));
        queue.add(new Point(xHead, yHead));
//        int[] dx = {1, -1, 0, 0};
//        int[] dy = {0, 0, 1, -1};

        while (!queue.isEmpty()) {
            Point tempPoint = queue.remove(0);
            for (int i = 0; i < dx.length; i++) {
                if (board.isAt(tempPoint.x + dx[i], tempPoint.y + dy[i], ENEMY_FULL) &&
                        temp[tempPoint.x + dx[i]][tempPoint.y + dy[i]] == 0) {
                    temp[tempPoint.x + dx[i]][tempPoint.y + dy[i]] = 1;
                    body.add(new Point(tempPoint.x + dx[i], tempPoint.y + dy[i]));
                    queue.add(new Point(tempPoint.x + dx[i], tempPoint.y + dy[i]));
                }
            }
        }
        return body;
    }

    /**
     * Определение змеи по точке на игровом поле
     *
     * @param snakes Список змей
     * @param point  Точка на игровом поле
     * @return Змея, которой принадлежит точка. Если точка не принадлежит ни одной из змей,
     * то возвращается змея с пустым телом
     */
    public Snake getSnakeByPoint(ArrayList<Snake> snakes, Point point) {
        for (Snake s : snakes) {
            for (Point p : s.getBody())
                if (p.equals(point))
                    return s;
        }
        return new Snake();
    }

    /**
     * Расчет расстояния от начальной точки до клетки с необходимым элементом.
     * Проверка наличия пути до необходимого элемента.
     * Поиск основан на волновом алгоритме
     *
     * @param snake    Змея, для которой необходимо найти расстояние
     * @param elements Список элементов
     * @return Объект класса GoalPoint, содержащий расстояние до найденного элемента, его координаты и
     * направление первого шага.
     * В случае, если элемент не найден, расстояние до объекта = 0
     */
    public GoalPoint searchNearestElement(MySnake snake, Elements... elements) {
        ArrayList<Point> queue = new ArrayList<>();
        int xStart = (int) snake.getHeadPoint().getX();
        int yStart = (int) snake.getHeadPoint().getY();

        int distance = 0;
        Direction direction = Direction.ACT;
        Point endPoint = new Point(0, 0);

        //Заполнение дополнительного массива значениями по умолчанию
        int cells[][] = new int[board.getField().length][board.getField().length];
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++)
                cells[x][y] = -1;

        }
        //
        cells[xStart][yStart] = 0;
        queue.add(new Point(xStart, yStart));
        while (queue.size() != 0 && distance == 0) {
            Point temp = queue.remove(0);
            //Перебор соседних ячеек
            for (int i = 0; i < dx.length; i++) {
                //Если ячейку не проходили и это не барьер или змея может его пролететь
                if (cells[temp.x + dx[i]][temp.y + dy[i]] == -1 &&
                        (board.isFreeAt(temp.x + dx[i], temp.y + dy[i]) ||
                                (snake.isFly() && snake.getActOfPillFly() > cells[temp.x][temp.y] + 1 &&
                                        (board.isAt(temp.x + dx[i], temp.y + dy[i], ENEMY_FULL) ||
                                                board.isAt(temp.x + dx[i], temp.y + dy[i], Elements.STONE))))) {
                    cells[temp.x + dx[i]][temp.y + dy[i]] =
                            cells[temp.x][temp.y] + 1;
                    queue.add(new Point(temp.x + dx[i], temp.y + dy[i]));
                }
                //Если необходимый элемент найден - запоминаем его местонахождение и сохраняем расстояние
                if (board.isAt(temp.x + dx[i], temp.y + dy[i], elements)) {
                    distance = cells[temp.x][temp.y] + 1;
                    cells[temp.x + dx[i]][temp.y + dy[i]] = distance;
                    endPoint = new Point(temp.x + dx[i], temp.y + dy[i]);
                }
            }
        }
        //Если элемент найден, находим соседнюю с начальной точку, через которую идет ближайший путь к элементу
        if (distance != 0) {
            queue = new ArrayList<>();
            queue.add(endPoint);
            Point temp = new Point(0, 0);
            while (queue.size() != 0) {
                temp = queue.remove(0);
                for (int i = 0; i < dx.length; i++) {
                    if (cells[temp.x][temp.y] == 1) {
                        break;
                    }
                    if (cells[temp.x + dx[i]][temp.y + dy[i]] == cells[temp.x][temp.y] - 1) {
                        queue.add(new Point(temp.x + dx[i], temp.y + dy[i]));
                        break;
                    }
                }
            }
            if (xStart < temp.x)
                direction = Direction.RIGHT;
            else {
                if (xStart > temp.x)
                    direction = Direction.LEFT;
                else {
                    if (yStart < temp.y)
                        direction = Direction.UP;
                    else
                        direction = Direction.DOWN;
                }
            }
        }

        return new GoalPoint(endPoint, direction, distance);
    }

    /**
     * Проверка попадания в тупик на следующем ходе.
     * Тупик - ситуация, при которой дальнейшее движение неминуемо приведет к столкновению с барьером
     *
     * @param goalPoint Точка, к которой будет осуществляться движение
     * @param snake     Змея, которая движется в заданную точку
     * @return Результат проверки ситуации на потенциальный тупик. True - тупик, False - не тупик
     */
    public boolean isDeadEnd(GoalPoint goalPoint, MySnake snake, ArrayList<Snake> enemies) {
        int[][] cells = new int[sizeOfBoardX][sizeOfBoardY];
        for (int[] row : cells)
            Arrays.fill(row, 0);

        int xNext = board.getMe().getX();
        int yNext = board.getMe().getY();
        cells[xNext][yNext] = 1;

        //Рассчет координат головы на следующем ходе
        switch (goalPoint.getFirstStep()) {
            case LEFT:
                xNext--;
                break;
            case RIGHT:
                xNext++;
                break;
            case UP:
                yNext++;
                break;
            case DOWN:
                yNext--;
                break;
        }
        cells[xNext][yNext] = 1;

        ArrayList<Point> exits = new ArrayList<>();
        exits.add(new Point(xNext, yNext));
        while (exits.size() == 1) {
            Point temp = exits.remove(0);
            for (int i = 0; i < dx.length; i++) {
                if (cells[temp.x + dx[i]][temp.y + dy[i]] == 0 //Если в точку не заходили
                        && (board.isFreeAt(temp.x + dx[i], temp.y + dy[i]) || //Точка свободна
                        (temp.x + dx[i] == goalPoint.getGoal().x && temp.y + dy[i] == goalPoint.getGoal().y) || //Или это - целевая точка
                        (board.isAt(temp.x + dx[i], temp.y + dy[i], Elements.STONE) &&//Или камень
                                (snake.isFly() && snake.getActOfPillFly() > cells[temp.x][temp.y] || //И его можно облететь
                                        snake.isFury() && snake.getActOfPillFury() > cells[temp.x][temp.y] || //Или съесть
                                        snake.getSize() > 5)) ||
                        (board.isAt(temp.x + dx[i], temp.y + dy[i], Helper.ENEMY_FULL) && //Или это - противник
                                (snake.isFly() && snake.getActOfPillFly() > cells[temp.x][temp.y]) || //И его можно облететь
                                (snake.isFury() && snake.getActOfPillFury() > cells[temp.x][temp.y] && //Или съесть
                                        !getSnakeByPoint(enemies, new Point(temp.x + dx[i], temp.y + dy[i])).isFury())))) {
                    cells[temp.x + dx[i]][temp.y + dy[i]] = cells[temp.x][temp.y] + 1;
                    exits.add(new Point(temp.x + dx[i], temp.y + dy[i]));
                }
            }
            if (exits.isEmpty())
                return true;
            if (exits.size() > 1)
                return false;
        }
        return false;
    }

    /**
     * Получение элемента, находящегося перед начальной точкой по ходу движения
     *
     * @param xHead         Х координата начальной точки (голова змеи)
     * @param yHead         Y координата начальной точки (голова змеи)
     * @param headDirection Направление движения
     * @return Элемент, находящийся перед начальной точкой по ходу движения
     */
    public Elements getNextElement(int xHead, int yHead, Direction headDirection) {
        switch (headDirection) {
            case RIGHT:
                return board.getAt(xHead + 1, yHead);
            case LEFT:
                return board.getAt(xHead - 1, yHead);
            case UP:
                return board.getAt(xHead, yHead + 1);
            case DOWN:
                return board.getAt(xHead, yHead - 1);
            default:
                return Elements.OTHER;
        }
    }

    /**
     * Проверка безопасности следующего хода с точки зрения встречи голов змей
     * @param goalPoint Целевая точка, в которую движется змея
     * @param snake Змея, для которой происходит проверка безопасности
     * @param enemies Список змей соперников
     * @return Возможные значения: True - ход безопасен, False - ход опасен
     */
    public boolean checkHeadEnemy(GoalPoint goalPoint, MySnake snake, ArrayList<Snake> enemies) {
        int xNext = (int) snake.getHeadPoint().getX();
        int yNext = (int) snake.getHeadPoint().getY();
        //Рассчет координат головы на следующем ходе
        switch (goalPoint.getFirstStep()) {
            case LEFT:
                xNext--;
                break;
            case RIGHT:
                xNext++;
                break;
            case UP:
                yNext++;
                break;
            case DOWN:
                yNext--;
                break;
        }
        //Проверка наличия голов змей и возможности их съесть/облететь
        for (int i = 0; i < dx.length; i++) {
            if (board.isAt(xNext + dx[i], yNext + dy[i], ENEMY_HEAD) &&
                    snake.compareTo(getSnakeByPoint(enemies, new Point(xNext + dx[i], yNext + dy[i]))) < 0 &&
                    (!snake.isFly() || !(snake.getActOfPillFly() > 2)))
                return false;
        }
        return true;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
