package geekbrains.javacore4;

import java.util.Arrays;

class XInARowGame {
    private byte[][] field;
    private int maxX;
    private int maxY;
    private int winLength;
    private int cellsUsed = 0;
    private boolean gameOver = false;
    private int currentPlayer = 1;
    private int winnerPlayer = 0;
    private int lastMoveX = -1;
    private int lastMoveY = -1;

    private final static char[] mark = { '_', 'X', 'O' };
    private final static int allDirections[][] = new int[][] {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};

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

    public int getWinLength() {
        return winLength;
    }

    XInARowGame(int fieldSize, int winLength) {
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

    // вывод поля на консоль
    public void print() {
        System.out.print(" ");
        for (int i = 0; i < maxX; ++i) {
            System.out.print(" " + (i % 10 + 1));
        }
        System.out.println("");
        for (int i = 0; i < maxY; ++i) {
            System.out.print(i % 10 + 1);
            for (int j = 0; j < maxX; ++j)
                System.out.print(" " + mark[field[i][j]]);
            System.out.println("");
        }
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
        for (int[] dir : allDirections)
            if (checkPlayerWonRow(x, y, currentPlayer, dir)) {
                winnerPlayer = currentPlayer;
                return true; // корректный ход и победа currentPlayer
            }
        if (cellsUsed == maxX * maxY) {
            gameOver = true;
            winnerPlayer = 0; // ничья
        }
        currentPlayer = 3 - currentPlayer; // 2 -> 1, 1 -> 2
        return true;
    }

    // проверяет выиграл ли игрок player
    public boolean checkPlayerWon(int player) {
        for (int x = 1; x <= maxX; ++x) {
            for (int y = 1; y <= maxY; ++y) {
                // достаточно просмотреть половину направлений из каждой точки, если где-то
                // есть winLength камней в ряд, то этот ряд будет найден с одного из двух концов
                for (int di = 0; di < allDirections.length / 2; ++di) {
                    if (checkPlayerWonRow(x, y, player, allDirections[di]))
                        return true;
                }
            }
        }
        return false;
    }

    public int at(int x, int y) {
        return field[y - 1][x - 1];
    }

    private void setAt(int x, int y, int player) {
        field[y - 1][x - 1] = (byte) player;
        lastMoveX = x;
        lastMoveY = y;
        cellsUsed++;
    }

    private boolean checkPlayerWonRow(int x, int y, int player, int[] dir) {
        boolean win = true;
        int step = 0;
        if (x < 1 || x > maxX || y < 1 || y > maxY)
            return false;
        int endX = x + dir[0] * (winLength - 1);
        int endY = y + dir[1] * (winLength - 1);
        if (endX < 1 || endX > maxX || endY < 1 || endY > maxY)
            return false;
        do {
            if (at(x, y) != player)
                return false;
            x += dir[0];
            y += dir[1];
            ++step;
        } while (step < winLength);
        return true;
    }

}
