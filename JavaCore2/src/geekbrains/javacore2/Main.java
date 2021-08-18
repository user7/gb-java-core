package geekbrains.javacore2;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
    }

    public static void task1() {
        System.out.println("\n= task1");
        int[] a = new int[]{1, 1, 0, 0, 1, 0, 1, 1, 0, 0};
        for (int i = 0; i < a.length; ++i) {
            a[i] = 1 - a[i];
        }
        System.out.println(Arrays.toString(a));
    }

    public static void task2() {
        System.out.println("\n= task2");
        int[] a = new int[8];
        for (int i = 0; i < a.length; ++i) {
            a[i] = i * 3;
        }
        System.out.println(Arrays.toString(a));
    }

    public static void task3() {
        System.out.println("\n= task3");
        int[] a = new int[]{1, 5, 3, 2, 11, 4, 5, 2, 4, 8, 9, 1};
        for (int i = 0; i < a.length; ++i) {
            if (a[i] < 6)
                a[i] *= 2;
        }
        System.out.println(Arrays.toString(a));
    }

    public static void task4() {
        System.out.println("\n= task4");
        int[][] a = new int[12][12];
        for (int i = 0; i < a.length; ++i) {
            a[i][i] = 1;
            a[a.length - 1 - i][i] = 1;
        }
        for (int i = 0; i < a.length; ++i)
            System.out.println(Arrays.toString(a[i]));
    }

    //task5, минимум и максимум
    public record MinMax(int min, int max) {
    }

    public static MinMax minMax(int[] a) {
        int min = a[0];
        int max = a[0];
        for (int i = 1; i < a.length; ++i) {
            min = Math.min(min, a[i]);
            max = Math.max(max, a[i]);
        }
        return new MinMax(min, max);
    }

    // task6, проверка, что массив можно разрезать на два массива с равными суммами
    public static boolean checkBalance(int[] a) {
        int sum = 0;
        for (int v : a) sum += v;
        int rsum = 0;
        for (int v : a) {
            rsum += v;
            if (rsum == sum - rsum)
                return true;
        }
        return a.length == 0;
    }

    // task7, сдвиг массива
    public static int gcd(int a, int b) {
        return a < 0 ? gcd(b, -a) : b == 0 ? a : gcd(b, a % b);
    }

    public static void rotate(int[] a, int n) {
        if (a.length == 0 || n == 0)
            return;
        n %= a.length;
        int nod = gcd(n, a.length);
        for (int startj = 0; startj < nod; ++startj) {
            int j = startj;
            int carry = a[j];
            do {
                j = (j + n + a.length) % a.length;
                int carry2 = a[j];
                a[j] = carry;
                carry = carry2;
            } while (j != startj);
        }
    }
}
