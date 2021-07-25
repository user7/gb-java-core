package geekbrains.javacore1;

public class Main {

    public static void main(String[] args) {
        int a = 1;
        short b = 1;
        long c = 1;
        float d = 1;
        double e = 1;
        boolean f = false;
        String s = "";
    }

    public static float calc(float a, float b, float c, float d) {
        return a * (b + (c / d));
    }

    public static boolean between10and20(int a) {
        return 10 <= a && a <= 20;
    }

    public static void printPositiveOrNegative(int a) {
        System.out.println(a >= 0 ? "положительное" : "отрицательное");
    }

    public static void greet(String name) {
        System.out.println("Привет, " + name + "!");
    }

    public static void printIsLeap(int year) {
        System.out.println(
                year % 4 != 0   ? "обычный год" :
                year % 100 != 0 ? "високосный год" :
                year % 400 == 0 ? "високосный год" :
                                  "обычный год"
        );
    }
}
