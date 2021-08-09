package geekbrains;

import java.util.Arrays;
import java.util.function.Consumer;

public class Main {
    static final int size = 10000000;
    static final int h = size / 2;

    public static void main(String[] args) {
        assert (size == h * 2);
        // измеряем три раза для надёжности
        for (int i = 0; i < 3; ++i) {
            test("один поток", arr -> calcAux(arr, 0, arr.length, 0));
            test("два потока с копированием", Main::calcTwoThreadsCopy);
            test("два потока без копирования", Main::calcTwoThreadsInplace);
            System.out.println();
        }
    }

    static float[] savedResult;

    static void test(String name, Consumer<float[]> func) {
        long start = System.currentTimeMillis();
        float[] arr = new float[size];
        Arrays.fill(arr, 1f);
        func.accept(arr);
        long stop = System.currentTimeMillis();
        System.out.format("%30s : %7.3f", name, (stop - start) / 1000.);
        if (savedResult == null)
            savedResult = arr;
        for (int i = 0; i < arr.length; ++i)
            if (Math.abs(arr[i] - savedResult[i]) > 0.0001) {
                System.out.println(" результаты в позиции " + i + " не совпадают: " + arr[i] + " != " + savedResult[i]);
                return;
            }
        System.out.println(" ОК");
        //System.out.println(Arrays.toString(arr));
    }

    static void calcAux(float[] arr, int start, int length, int startEffectiveIndex) {
        // В вычислении нужно использовать не индекс в (возможно временном) массиве,
        // а "индекс элемента в изначальном массиве", т.е. когда обрабатываем вторую
        // половину индекс элемента j меняется от 0 до h, а эффективный индекс i
        // от h до size. Это нужно при расчёте верхней половины значений, в нижней
        // половине i и j совпадают.
        int i = startEffectiveIndex;
        for (int j = start; j < start + length; ++j, ++i) {
            arr[j] = (float) (arr[j] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
    }

    static void calcTwoThreadsCopy(float[] arr) {
        float[][] tmpArrs = new float[][]{new float[h], new float[h]};
        Thread[] threads = new Thread[2];
        for (int i = 0; i < threads.length; ++i) {
            float[] tmpArr = tmpArrs[i];
            System.arraycopy(arr, i * h, tmpArr, 0, h);
            int effectiveIndex = i * h; // компилятор не любит не-final вычисления в лямбде
            threads[i] = new Thread(() -> Main.calcAux(tmpArr, 0, h, effectiveIndex));
            threads[i].start();
        }
        joinAll(threads);
        for (int i = 0; i < 2; ++i)
            System.arraycopy(tmpArrs[i], 0, arr, i * h, h);
    }

    static void calcTwoThreadsInplace(float[] arr) {
        Thread[] threads = new Thread[2];
        for (int i = 0; i < 2; ++i) {
            int effectiveIndex = i * h; // компилятор не любит не-final вычисления в лямбде
            threads[i] = new Thread(() -> Main.calcAux(arr, effectiveIndex, h, effectiveIndex));
            threads[i].start();
        }
        joinAll(threads);
    }

    static void joinAll(Thread[] threads) {
        for (var t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
