package geekbrains;

import java.util.Arrays;
import java.util.function.Consumer;

public class Main {
    static final int size = 10000000;
    static final int h = size / 2;

    public static void main(String[] args) {
        testCalc("основной тред делает всю работу", Main::workerSingleThread);
        testCalc("основной + 2 рабочих треда, с копированием", Main::workerTwoThreadsCopy);
        testCalc("рабочий тред помогает основному, без копирования", Main::workerHelperThread);
    }

    static float[] savedResult;

    static void testCalc(String algo, Consumer<float[]> worker) {
        float[] arr = new float[size];
        Arrays.fill(arr, 1);
        long start = System.currentTimeMillis();
        worker.accept(arr);
        long stop = System.currentTimeMillis();
        System.out.format("%50s  %7.3fс\n", algo, (stop - start) / 1000.);
        if (savedResult == null)
            savedResult = arr;
        for (int i = 0; i < arr.length; ++i)
            if (Math.abs(savedResult[i] - arr[i]) > 0.0001) {
                System.out.println("Ошибка, результат не совпадает с эталоном в позиции " + i
                                    + ": " + arr[i] + " != " + savedResult[i]);
                break;
            }
    }

    static void workerSingleThread(float[] arr) {
        calcRange(arr, 0, arr.length, 0);
    }

    static void workerTwoThreadsCopy(float[] arr) {
        Thread[] threads = new Thread[2];
        float[][] a = new float[2][];
        for (int i = 0; i < 2; ++i) {
            int off = h * i;
            float[] ai = new float[h];
            a[i] = ai;
            System.arraycopy(arr, off, ai, 0, h);
            threads[i] = new Thread(() -> calcRange(ai, 0, h, off));
            threads[i].start();
        }
        for (int i = 0; i < 2; ++i) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.arraycopy(a[i], 0, arr, h * i, h);
        }
    }

    static void workerHelperThread(float[] arr) {
        Thread helper = new Thread(() -> calcRange(arr, 0, h, 0));
        helper.start();
        calcRange(arr, h, h, h);
        try {
            helper.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void calcRange(float[] arr, int start, int length, int startingEffectiveIndex) {
        // В вычислении нужно использовать не индекс в (возможно временном) массиве,
        // а "индекс элемента в изначальном массиве", т.е. когда обрабатываем вторую
        // половину индекс элемента j меняется от 0 до h, а эффективный индекс i
        // от h до size. Это нужно при расчёте верхней половины значений, в нижней
        // половине i и j совпадают.
        int i = startingEffectiveIndex;
        for (int j = start; j < start + length; ++j, ++i) {
            arr[j] = (float) (arr[j] * (Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2)));
        }
    }
}