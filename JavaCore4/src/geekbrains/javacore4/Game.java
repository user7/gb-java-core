package geekbrains.javacore4;

import java.util.Arrays;

class Game {
    private final byte[][] field;
    private final int maxX;
    private final int maxY;
    private final int winLength;
    private int cellsUsed = 0;
    private boolean gameOver = false;
    private int currentPlayer = 1;
    private int winnerPlayer = 0;
    private int lastMoveX = -1;
    private int lastMoveY = -1;

    private final static char[] mark = {'_', 'X', 'O'};
    private final static int[][] directions = new int[][]{{1, 0}, {1, 1}, {0, 1}, {-1, 1}};

    public void concede() {
        gameOver = true;
        winnerPlayer = 3 - currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getWinnerPlayer() {
        return winnerPlayer;
    }

    public boolean isDraw() {
        return isGameOver() && getWinnerPlayer() == 0;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    Game(int fieldSize, int winLength) {
        if (fieldSize < 1)
            throw new IllegalArgumentException("размер поля должен быть положительным");
        if (winLength < 1)
            throw new IllegalArgumentException("размер выигрышной серии должен быть положительным");
        if (winLength > fieldSize)
            throw new IllegalArgumentException("длина выигрышной серии не должна превышать размера поля");
        field = new byte[fieldSize][fieldSize];
        for (byte[] row : field)
            Arrays.fill(row, (byte) 0);
        maxY = fieldSize;
        maxX = fieldSize;
        this.winLength = winLength;
    }

    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RESET = "\u001B[0m";

    // вывод поля на консоль
    public void print() {
        System.out.print(" ");
        for (int x = 1; x <= maxX; ++x)
            System.out.print(" " + ((x - 1) % 10 + 1));
        System.out.println();
        for (int y = 1; y <= maxY; ++y) {
            System.out.print((y - 1) % 10 + 1);
            for (int x = 1; x <= maxX; ++x) {
                String pre = "";
                String post = "";
                if (x == lastMoveX && y == lastMoveY) {
                    pre = ANSI_YELLOW;
                    post = ANSI_RESET;
                } else if (isGameOver() && !isDraw() && checkWinAt(x, y)) {
                    pre = ANSI_GREEN;
                    post = ANSI_RESET;
                }
                System.out.print(" " + pre + mark[at(x, y)] + post);
            }
            System.out.println();
        }

        if (!isGameOver())
            System.out.println("ход игрока " + currentPlayer + "(" + mark[currentPlayer] + ")");
        else {
            if (isDraw())
                System.out.println("ничья!");
            else
                System.out.println("игрок " + currentPlayer + "(" + mark[currentPlayer] + ") выиграл!");
        }
        System.out.println("--");
    }

    // если ход корректный, то делает ход от имени currentPlayer,
    // иначе возвращает false
    public boolean move(int x, int y) {
        if (gameOver)
            return false; // нельзя сделать ход, игра уже закончена
        if (x < 1 || x > maxX || y < 1 || y > maxY)
            return false; // некоррентный ход за пределы поля
        if (at(x, y) != 0)
            return false; // некоррентный ход, клетка занята

        setAt(x, y, currentPlayer);
        if (checkWinAt(x, y)) {
            gameOver = true;
            winnerPlayer = currentPlayer;
        }
        else
            currentPlayer = 3 - currentPlayer; // 1 <-> 2

        if (!gameOver && cellsUsed == maxX * maxY) { // никто не выиграл, но клетки пончились - ничья
            gameOver = true;
            winnerPlayer = 0;
        }
        return true; // корректный ход
    }

    private boolean checkWinAt(int x, int y) {
        for (int[] d : directions) {
            int lengthForward = runLength(x, y, currentPlayer, d[0], d[1]);
            int lengthBackward = runLength(x, y, currentPlayer, -d[0], -d[1]);
            if (lengthForward + lengthBackward >= winLength + 1)
                return true;
        }
        return false;
    }

    public void undoMove(int x, int y) {
        currentPlayer = at(x, y);
        assert (currentPlayer != 0);
        gameOver = false;
        winnerPlayer = 0;
        setAt(x, y, 0);
    }

    private int runLength(int x, int y, int player, int dx, int dy) {
        int length = 0;
        while (1 <= x && x <= maxX && 1 <= y && y <= maxY && at(x, y) == player) {
            length++;
            x += dx;
            y += dy;
        }
        return length;
    }

    public int at(int x, int y) {
        return field[y - 1][x - 1];
    }

    private void setAt(int x, int y, int player) {
        field[y - 1][x - 1] = (byte) player;
        lastMoveX = x;
        lastMoveY = y;
        if (player == 0)
            cellsUsed--;
        else
            cellsUsed++;
    }
}
