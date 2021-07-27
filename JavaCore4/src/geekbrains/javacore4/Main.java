package geekbrains.javacore4;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.println("введите команду (?, q, n):");
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

            System.out.println("неизвестная команда: " + cmdLine);
        }
    }

    static int parseInt(int defaultValue, String[] cmd, int i) {
        return cmd.length > i ? Integer.parseInt(cmd[i]) : defaultValue;
    }

    static void handleNewGame(String[] cmd) {
        int fieldSize = parseInt(3, cmd, 1);
        int winLength = parseInt(3, cmd, 2);

        StonesGamePlayer[] players = new StonesGamePlayer[]{parsePlayer(cmd, 3), parsePlayer(cmd, 4)};
        if (players[0] == null || players[1] == null)
            return;

        int matches = parseInt(1, cmd, 5);
        int[] winner = new int[3];
        for (int i = 0; i < matches; ++i) {
            StonesGame game;
            try {
                game = new StonesGame(fieldSize, winLength);
            } catch (IllegalArgumentException e) {
                System.out.println("не удалось создать игру: " + e);
                return;
            }
            playGame(game, players);
            winner[game.getWinnerPlayer()]++;
        }
        if (matches > 1)
            System.out.println("Счёт " + winner[1] + ":" + winner[2] + ", всего матчей " + matches);
    }

    static void playGame(StonesGame game, StonesGamePlayer[] players) {
        while (true) {
            game.print();
            if (game.isGameOver())
                return;
            players[game.getCurrentPlayer() - 1].makeMove(game);
        }
    }

    static StonesGamePlayer parsePlayer(String[] cmd, int pos) {
        String t = cmd.length > pos ? cmd[pos] : "r"; // по умолчанию играет random AI
        switch (t) {
            case "r": return new PlayerRandom();
            case "m": return new PlayerMinMax();
            case "h": return new PlayerHuman();
            default:
                System.out.println("неизвестный тип AI: '" + cmd[pos] + "'");
                return null;
        }
    }

    static void printHelp() {
        System.out.println("Команды:");
        System.out.println(" ?|h[elp]          - вывести это сообщение");
        System.out.println(" q[uit]            - выход");
        System.out.println(" n[ew] N W P1 P2 X - новая игра NxN клеток,");
        System.out.println("                     требуется выстроить W камней в ряд для победы,");
        System.out.println("                     P1 и P2 задают тип игроков 1 и 2 соответсвенно:");
        System.out.println("                        h  - human, человек, управление с консоли");
        System.out.println("                        r  - random, AI делающий случайные ходы");
        System.out.println("                     X если больше 1, то провести X матчей и показать сумму");
        System.out.println();
        System.out.println("    пример: n 3 3 h r - крестики-нолики, первый игрок человек, второй - рандомный AI");
        System.out.println("    пример: n 4 3 h h - крестики-нолики на доске 4x4, два человека друг против друга");
    }
}
