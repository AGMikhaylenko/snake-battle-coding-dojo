Реализация бота для игры SnakeBattle https://dojorena.io/events/187

Стратегия бота: съесть самое большео количество яблок и быть самым большим на конец раунда, набрав 50 очков.
На каждом шаге бот вычисляет расстояние до ближайших полезных объектов на карте. Использован Волновой алгоритм.
Далее делается выбор направления на основании приоритетов:
- Съесть соперника
- Съесть таблетку ярости
- Съесть таблетку полета
- Съесть камень, если это возможно и позволяет игровая ситуация
- Съесть золото
- Съесть яблоко
- Уйти от столкновения с препятствием

Реализованный код находится в пакете model и разбит на пять классов:
- Класс Snake описывает змею
- Класс MySnake наследуется от Snake и описывает свойства змеи игрока
- Класс Solution содержит основной алгоритм выбора хода
- Класс Helper содержит алгоритмы, необходимые для класса Solution
- Класс GoalPoint описывает целевую точку, необходимую для алгоритмов класса Helper



For NonJava languages:
- please go to .\src\main\<language>
- chose your language
- and follow README.md instructions

For Java:
- setup Java (JDK 11)
    + setup JAVA_HOME variable
- setup Maven3
    + setup M2_HOME variable
    + setup Path variable
    + open cmd and run command 'mvn -version' it should print valid java and maven location
- import this project as Maven project into Intellij Idea (Eclipse/ is not recommended)
- please install Engine dependency
    + on page http://server/codenjoy-contest/help
        * you can download zip with dependency
            - server = server_host_ip:8080 server ip inside your LAN
            - server = codenjoy.com if you play on http://codenjoy.com/codenjoy-contest
        * on this page you can also read game instructions
- register your hero on server http://server/codenjoy-contest/register
- in class .\src\main\java\com\codenjoy\dojo\<gamename>\client\YourSolver.java
    + copy board page browser url from address bar and paste into main method
    + implement logic inside method
        * public String get(Board board) {
    + run main method of YourSolver class
    + on page http://server/codenjoy-contest/board/game/<gamename> you can check the leaderboard - your bot should move
    + if something changed - restart the process
        * warning! only one instance of YourSolver class you can run per player - please check this
- in class .\src\main\java\com\codenjoy\dojo\<gamename>\client\Board.java
    + you can add you own methods for work with board
- in test package .\src\test\java\com\codenjoy\dojo\<gamename>\client
    + you can write yor own test
- Codenjoy!