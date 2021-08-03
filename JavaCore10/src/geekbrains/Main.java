package geekbrains;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        test1();
        test2();
    }

    public static void test2() {
        System.out.println("*** test2");

        compareBoxes(new Box(new Apple(), 3), new Box(new Apple(), 3));
        compareBoxes(new Box(new Apple(), 30), new Box(new Orange(), 30));
        compareBoxes(new Box(new Apple(), 300), new Box(new Orange(), 200));

        Box<Apple> ba30 = new Box<>(new Apple(), 30);
        Box<Orange> bo1 = new Box<>(new Orange(), 8);
        Box<Orange> bo2 = new Box<>(new Orange(), 12);

        System.out.println("два маленьких ящика с апельсинами");
        compareBoxes(ba30, bo1);
        compareBoxes(ba30, bo2);

        System.out.println("пересыпали всё во 2й ящик");
        bo1.putToAnotherBox(bo2);
        compareBoxes(ba30, bo1);
        compareBoxes(ba30, bo2);
    }

    static void compareBoxes(Box a, Box b) {
        System.out.format("[%2$10s %1$3dшт] и [%4$10s %3$3dшт] равны по весу: %5$s\n",
                          a.getSize(), a.getContentsName(),
                          b.getSize(), b.getContentsName(),
                          (a.compare(b) ? "ДА" : "НЕТ"));
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
