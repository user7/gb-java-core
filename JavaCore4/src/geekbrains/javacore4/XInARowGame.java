package geekbrains.javacore4;

import java.util.Arrays;

class XInARowGame {
    private byte[][] field;
    private int maxX;
    private int maxY;
    private int winLength;
    private int cellsUsed = 0;

    public final static byte PNONE = 0;
    public final static byte P1 = 1;
    public final static byte P2 = 2;

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
            Arrays.fill(row, PNONE);
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
            for (int j = 0; j < maxX; ++j) {
                String o;
                switch (field[i][j]) {
                    case P1:
                        o = " X";
                        break;
                    case P2:
                        o = " O";
                        break;
                    default:
                        o = " _";
                        break;
                }
                System.out.print(o);
            }
            System.out.println("");
        }
    }

    // если ход корректный, то делает ход и возвращает true, иначе false,
    // player должен быть P1 или P2
    public boolean move(byte player, int x, int y) {
        if (player < 1 || player > 2)
            return false;
        if (x < 1 || x > maxX || y < 1 || y > maxY)
            return false;
        switch (at(x, y)) {
            case PNONE:
                setAt(x, y, player);
                return true;
            default:
                return false;
        }
    }

    public boolean checkDraw() {
        return cellsUsed == maxX * maxY && !checkPlayerWon(P1) && !checkPlayerWon(P2);
    }

    // проверяет выиграл ли игрок player
    public boolean checkPlayerWon(byte player) {
        int[][] directions = new int[][]{{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        for (int x = 1; x <= maxX; ++x) {
            for (int y = 1; y <= maxY; ++y) {
                for (int[] dir : directions) {
                    if (checkPlayerWonRow(x, y, player, dir))
                        return true;
                }
            }
        }
        return false;
    }

    public byte at(int x, int y) {
        return field[y - 1][x - 1];
    }

    private void setAt(int x, int y, byte player) {
        field[y - 1][x - 1] = player;
        cellsUsed++;
    }

    private boolean checkPlayerWonRow(int x, int y, byte player, int[] dir) {
        boolean win = true;
        int step = 0;
        do {
            if (x < 1 || x > maxX || y < 1 || y > maxY)
                return false;
            if (at(x, y) != player)
                return false;
            x += dir[0];
            y += dir[1];
            ++step;
        } while (step < winLength);
        return true;
    }

}
