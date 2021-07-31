package geekbrains.javacore2;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        task1();
        task2();
        task3();
        task4();
        task5();
        task6();
        task7();
    }

    public static void task1() {
        System.out.println("\n= task1");
        int[] a = new int[] {1, 1, 0, 0, 1, 0, 1, 1, 0, 0};
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
        int[] a = new int[] {1, 5, 3, 2, 11, 4, 5, 2, 4, 8, 9, 1};
        for (int i = 0; i < a.length; ++i) {
            if (a[i] < 6)
                a[i] *= 2;
        }
        System.out.println(Arrays.toString(a));
    }


    public static void task4() {
        System.out.println("\n= task4");
        int[][] a = new int[12][12];
        for(int i = 0; i < a.length; ++i) {
            a[i][i] = 1;
            a[a.length - 1 - i][i] = 1;
        }
        for(int i = 0; i < a.length; ++i)
            System.out.println(Arrays.toString(a[i]));
    }

    public static void task5() {
        System.out.println("\n= task5");
        int[] a = new int[] {1, 5, 3, 2, 11, 4, 5, 2, 4, 8, 9, 1};
        int min = a[0];
        int max = a[0];
        for (int i = 1; i < a.length; ++i) {
            if (a[i] > max)
                max = a[i];
            if (a[i] < min)
                min = a[i];
        }
        System.out.println("min=" + min + " max=" + max);
    }

    public static boolean checkBalanced(int[] a) {
        int[] sumBelowInc = new int[a.length];
        int[] sumAbove = new int[a.length];
        int sum;
        sum = 0;
        for (int i = 0; i < a.length; ++i) {
            sum += a[i];
            sumBelowInc[i] = sum;
        }
        sum = 0;
        for (int i = a.length - 1; i >= 0; --i) {
            sumAbove[i] = sum;
            sum += a[i];
        }
        for (int i = 0; i < a.length; ++i)
            if (sumBelowInc[i] == sumAbove[i])
                return true;
        return false;
    }

    public static boolean checkBalancedSinglePass(int[] a) {
        int i = 0;
        int j = a.length - 1;
        int sumLow = 0;
        int sumHigh = 0;
        while (i <= j) {
            if (sumLow < sumHigh)
                sumLow += a[i++];
            else
                sumHigh += a[j--];
        }
        return sumLow == sumHigh;
    }

    static void checkBisect(int[] data) {
        System.out.println("Checking array balanced: " + Arrays.toString(data));
        System.out.println("V1: " + checkBalanced(data));
        System.out.println("V2: " + checkBalancedSinglePass(data));
    }

    public static void task6() {
        System.out.println("\n= task6");
        checkBisect(new int[] {2, 4, 2, 1, 2, 10, 1});
        checkBisect(new int[] {2, 2, 2, 1, 2, 10, 1});
        checkBisect(new int[] {2, 2, 2, 1, 2, 10, 3});
    }

    public static int euclidNod(int a, int b) {
        return a < 0 ? euclidNod(b, -a) : b == 0 ? a : euclidNod(b, a % b);
    }

    public static void rotate(int[] a, int n) {
        if (a.length == 0 || n == 0)
            return;
        if (n < -a.length)
            n %= a.length;
        int nod = euclidNod(n, a.length);
        for (int startj = 0; startj < nod; ++startj) {
            int j = startj;
            int carry = a[j];
            do {
                j = (j + n + a.length) % a.length;
                int carry2 = a[j];
                a[j] = carry;
                carry = carry2;
            } while(j != startj);
        }
    }

    public static void checkRotate(int[] a, int n) {
        int a2[] = new int[a.length];
        for (int i = 0; i < a.length; ++i)
            a2[i] = a[i];
        rotate(a2, n);
        System.out.println(Arrays.toString(a) + " >> " + n + " = " + Arrays.toString(a2));
    }

    public static void task7() {
        System.out.println("\n= task7");
        checkRotate(new int[] {1, 2, 3, 4, 5, 6}, 0);
        checkRotate(new int[] {1, 2, 3, 4, 5, 6}, -1);
        checkRotate(new int[] {1, 2, 3, 4, 5, 6}, 1);
        checkRotate(new int[] {1, 2, 3, 4, 5, 6}, 2);
        checkRotate(new int[] {1, 2, 3, 4, 5, 6}, 3);
    }
}
