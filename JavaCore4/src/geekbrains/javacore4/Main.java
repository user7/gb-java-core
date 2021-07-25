package geekbrains.javacore4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

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

interface XInARowGameAi {
    void makeMove(byte player, XInARowGame game);
}

class AiRandom implements XInARowGameAi {
    @Override
    public void makeMove(byte player, XInARowGame game) {
        ArrayList<int[]> possibleMoves = new ArrayList<>();
        for (int x = 1; x <= game.getMaxX(); ++x)
            for (int y = 1; y <= game.getMaxY(); ++y)
                if (game.at(x, y) == XInARowGame.PNONE)
                    possibleMoves.add(new int[]{x, y});
        if (possibleMoves.size() == 0)
            throw new IllegalArgumentException("не осталось возможных ходов");
        Random r = new Random();
        int[] at = possibleMoves.get(r.nextInt(possibleMoves.size()));
        game.move(player, at[0], at[1]);
    }
}

class AiRandomMaxDensity implements XInARowGameAi {
    @Override
    public void makeMove(byte player, XInARowGame game) {
        throw new UnsupportedOperationException("этот AI еще не реализован");
    }
}

class AiMinMax implements XInARowGameAi {
    @Override
    public void makeMove(byte player, XInARowGame game) {
        throw new UnsupportedOperationException("этот AI еще не реализован");
    }
}

public class Main {
    private static XInARowGame game;
    private static XInARowGameAi ai;
    private static byte currentPlayer;

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        while (true) {
            if (game != null && game.checkDraw()) {
                game.print();
                System.out.println("ничья!");
                game = null;
                continue;
            }

            if (game != null && currentPlayer == XInARowGame.P2) {
                handleAIMove();
                continue;
            }

            System.out.println("введите команду (q, n, m, ?):");
            if (!s.hasNext())
                break;
            String cmdLine = s.nextLine();
            String[] cmd = cmdLine.split(" +");

            if (cmd[0].matches("[?]|h(e(lp?)?)?")) {
                printHelp();
                continue;
            }

            if (cmd[0].matches("q(u(it?)?)?"))
                break;

            if (cmd[0].matches("n(ew?)?")) {
                handleNewGame(cmd);
                continue;
            }

            if (cmd[0].matches("m(o(ve?)?)?")) {
                handleMove(cmd);
                continue;
            }

            System.out.println("неизвестная команда: " + cmdLine);
        }
    }

    static void handleAIMove() {
        try {
            ai.makeMove(XInARowGame.P2, game);
            if (game.checkPlayerWon(XInARowGame.P2)) {
                game.print();
                System.out.println("вы проиграли!");
                game = null;
                return;
            }
            game.print();
            currentPlayer = XInARowGame.P1;
        } catch(IllegalArgumentException e) {
            game.print();
            System.out.println("AI не смог сделать ход: " + e.toString());
            game = null;
        }
    }

    static void handleMove(String[] cmd) {
        if (game == null) {
            System.out.println("нет активной игры, команда move не работает");
            return;
        }

        if (cmd.length < 3) {
            System.out.println("недостаточно параметров для move, должно быть move X Y");
            return;
        }

        int x = Integer.parseInt(cmd[1]);
        int y = Integer.parseInt(cmd[2]);
        try {
            if (!game.move(currentPlayer, x, y)) {
                System.out.println("некорректный ход!");
                return;
            }
            game.print();
            if (game.checkPlayerWon(XInARowGame.P1)) {
                System.out.println("вы выиграли!");
                game = null;
            } else {
                currentPlayer = XInARowGame.P2;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("некооректный ход: " + e);
        }
    }

    static void handleNewGame(String cmd[]) {
        int fieldSize = 3;
        if (cmd.length > 1)
            fieldSize = Integer.parseInt(cmd[1]);

        int winLength = 3;
        if (cmd.length > 2)
            winLength = Integer.parseInt(cmd[2]);

        ai = new AiRandom();
        if (cmd.length > 3) {
            switch (cmd[3]) {
                case "r":
                    ai = new AiRandom();
                    break;
                case "rd":
                    ai = new AiRandomMaxDensity();
                    break;
                case "mm":
                    ai = new AiMinMax();
                    break;
                default:
                    System.out.println("неизвестный тип AI: '" + cmd[3] + "'");
                    ai = null;
            }
        }

        if (ai == null)
            return;
        try {
            game = new XInARowGame(fieldSize, winLength);
            currentPlayer = XInARowGame.P1;
            game.print();
        } catch (IllegalArgumentException e) {
            System.out.println("не удалось создать игру: " + e);
            return;
        }
    }

    static void printHelp() {
        System.out.println("Команды:");
        System.out.println(" ?|h[elp]     - вывести это сообщение");
        System.out.println(" q[uit]       - выход");
        System.out.println(" n[ew] N W AI - новая игра NxN клеток, требуется W в ряд для победы, использовать один из AI:");
        System.out.println("                r - random AI, случайные ходы");
        System.out.println("                rd - random+density, случайный ход в зоне наибольшей плотности");
        System.out.println("                mm - алгоритм minimax");
        System.out.println(" m[ove] X Y   - сделать ход по координатам X, Y");
    }
}
