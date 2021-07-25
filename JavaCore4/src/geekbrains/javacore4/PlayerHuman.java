package geekbrains.javacore4;

import javax.swing.*;
import java.util.Scanner;

// игрок-человек
public class PlayerHuman implements StonesGamePlayer {
    Scanner s = new Scanner(System.in);

    @Override
    public void makeMove(StonesGame game) {
        while (true) {
            String line = s.nextLine();
            System.out.println("q - сдаться, x y - ход");
            if (line.matches("q(u(it?)?)?"))
                game.concede();
            String[] xy = line.split(" +");
            if (xy.length != 2) {
                System.out.println("требуется ровно два целочисленных аргумента, x и y");
                continue;
            }
            int x = Integer.parseInt(xy[0]);
            int y = Integer.parseInt(xy[1]);
            if (!game.move(x, y)) {
                System.out.println("некорректный ход!");
                continue;
            }
            return;
        }
    }
}
