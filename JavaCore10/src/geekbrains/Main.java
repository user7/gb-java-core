package geekbrains;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        test1();
        test2();
    }

    public static void test2() {
        System.out.println("*** test2");
        compareBoxes(3, 2);
        compareBoxes(30, 20);
        compareBoxes(30, 19);
        compareBoxes(30, 22);
    }

    static void compareBoxes(int apples, int oranges) {
        Box<Apple> ba = new Box<>(new Apple(), apples);
        Box<Orange> bo = new Box<>(new Orange(), oranges);
        System.out.printf("в ящике яблок: %2d шт, в ящике апельсинов: %2d шт, вес равный: %s\n",
                          apples, oranges, (ba.compare(bo) ? "ДА" : "НЕТ"));
    }

    public static void test1() {
        System.out.println("*** test1");
        testSwapIJ(new Integer[] {0, 1, 2, 3, 4}, 0, 3);
        testSwapIJ(new String[] {"aa", "bb", "cc", "dd", "ee"}, 4, 3);
        System.out.println();
    }

    public static <E> void testSwapIJ(E[] array, int i, int j) {
        System.out.print("swapIJ: " + Arrays.toString(array));
        swapIJ(array, i, j);
        System.out.println(" + swap(" + i + "," + j + ") -> " + Arrays.toString(array));
    }

    public static <E> void swapIJ(E[] array, int i, int j) {
        if (i >= array.length || j >= array.length || i == j)
            return;
        E tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }
}
