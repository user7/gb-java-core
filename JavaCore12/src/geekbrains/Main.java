package geekbrains;

import java.util.Arrays;
import java.util.function.Consumer;

public class Main {
    static final int size = 10000000;
    static final int h = size / 2;

    public static void main(String[] args) {
        for (int i = 0; i < 3; ++i) {
            test("один поток", Main::calcSingleThread);
            test("два потока с копированием", Main::calcTwoThreadsCopy);
            test("два потока без копирования", Main::calcTwoThreadsInplace);
            System.out.println();
        }
    }

    static float[] compare;

    static void test(String name, Consumer<float[]> func) {
        long start = System.currentTimeMillis();
        float[] arr = new float[size];
        Arrays.fill(arr, 1f);
        func.accept(arr);
        long stop = System.currentTimeMillis();
        System.out.format("%30s : %7.2f", name, (stop - start) / 1000.);
        if (compare == null)
            compare = arr;
        boolean bad = false;
        for (int i = 0; i < arr.length; ++i)
            if (Math.abs(arr[i] - compare[i]) > 0.0001) {
                bad = true;
                break;
            }
        System.out.println(bad ? " ERROR" : " OK");
        //System.out.println(Arrays.toString(arr));
    }

    static void calcAux(float[] arr, int from, int length, int adjustI) {
        for (int i = from; i < from + length; ++i) {
            int ia = i + adjustI;
            arr[i] = (float) (arr[i] * Math.sin(0.2f + ia / 5) * Math.cos(0.2f + ia / 5) * Math.cos(0.4f + ia / 2));
        }
    }

    static void calcSingleThread(float[] arr) {
        calcAux(arr, 0, 2 * h, 0);
    }

    static void calcTwoThreadsCopy(float[] arr) {
        float[][] tmps = new float[][]{new float[h], new float[h]};
        Thread[] threads = new Thread[2];
        for (int i = 0; i < threads.length; ++i) {
            float[] tmp = tmps[i];
            int start = i * h;
            System.arraycopy(arr, start, tmp, 0, h);
            threads[i] = new Thread(() -> Main.calcAux(tmp, 0, h, start));
            threads[i].start();
        }
        joinAll(threads);
        for (int i = 0; i < 2; ++i)
            System.arraycopy(tmps[i], 0, arr, i * h, h);
    }

    static void calcTwoThreadsInplace(float[] arr) {
        Thread[] threads = new Thread[2];
        for (int i = 0; i < 2; ++i) {
            int start = i * h;
            threads[i] = new Thread(() -> Main.calcAux(arr, start, h, 0));
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
