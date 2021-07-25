package geekbrains.javacore4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static XInARowGame game;
    private static XInARowGameAi ai;

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        while (true) {
            if (game != null)
                System.out.println("game.currentPlayer=" + game.getCurrentPlayer());

            if (game != null && game.isDraw()) {
                game.print();
                System.out.println("ничья!");
                game = null;
                continue;
            }

            if (game != null && game.getCurrentPlayer() == 2) {
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
            ai.makeMove(game);
            if (game.checkPlayerWon(2)) {
                game.print();
                System.out.println("вы проиграли!");
                game = null;
                return;
            }
            game.print();
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
            if (!game.move(x, y)) {
                System.out.println("некорректный ход!");
                return;
            }
            game.print();
            if (game.checkPlayerWon(1)) {
                System.out.println("вы выиграли!");
                game = null;
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
