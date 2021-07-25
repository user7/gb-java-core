package geekbrains.javacore3;

import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        task1();
        task2();
    }

    public static void task1() {
        Random rand = new Random();
        int r = rand.nextInt(10);
        Scanner s = new Scanner(System.in);
        System.out.println("Угадай число от 0 до 9 с трёх попыток");
        for (int i = 0; i < 3; ++i) {
            int x = s.nextInt();
            if (x == r) {
                System.out.println("Правильно!");
                return;
            }
            System.out.println("Неправильно, " + (r < x ? "меньше" : "больше"));
        }
        System.out.println("Попытки кончились! Было загадано число " + r);
    }

    public static void task2() {
        String[] words = {"apple", "orange", "lemon", "banana", "apricot", "avocado",
                          "broccoli", "carrot", "cherry", "garlic", "grape", "melon",
                          "leak", "kiwi", "mango", "mushroom", "nut", "olive", "pea",
                          "peanut", "pear", "pepper", "pineapple", "pumpkin", "potato"};
        int r = new Random().nextInt(words.length);
        int take = 1;
        String w = words[r];
        Scanner s = new Scanner(System.in);
        System.out.println("Угадай английское слово, после каждой попытки будет показаны правильные буквы!");
        do {
            System.out.println("Попытка " + take + ": ");
            String guess = s.next();
            ++take;
            if (guess.equals(w)) {
                System.out.println("Правильно!");
                return;
            }
            StringBuilder match = new StringBuilder();
            for (int i = 0; i < 15; ++i) {
                if (i < w.length() && i < guess.length() && w.charAt(i) == guess.charAt(i))
                    match.append(w.charAt(i));
                else
                    match.append("#");
            }
            System.out.println(match.toString());
        } while(true);
    }
}
