package geekbrains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;

public class Main {
    static final int size = 10000000;
    static final int h = size / 2;

    public static void main(String[] args) {
        assert (size == h * 2);
        record TestEntry(String name, Consumer<float[]> func, ArrayList<Long> measures) {
            Long medianTime() {
                Collections.sort(measures);
                return (measures.get(measures.size() / 2) + measures.get((measures.size() + 1) / 2)) / 2;
            }
        }
        // список тестируемых алгоритмов
        TestEntry[] tests = new TestEntry[]{
                new TestEntry("один поток", Main::calcSingleThread, new ArrayList<>()),
                new TestEntry("два потока с копировнием", Main::calcTwoThreadsCopy, new ArrayList<>()),
                new TestEntry("два потока без копирования", Main::calcTwoThreadsInplace, new ArrayList<>()),
                new TestEntry("один поток с оптимизацией", Main::calcSingleThreadOptimized, new ArrayList<>()),
                new TestEntry("один поток и анролл", Main::calcSingleThreadOptimizedUnroll, new ArrayList<>()),
                new TestEntry("два потока с оптимизацией", Main::calcTwoThreadsOptimized, new ArrayList<>()),
                new TestEntry("два потока и анролл", Main::calcTwoThreadsOptimizedUnroll, new ArrayList<>()),
        };

        // понадобится для проверки, что результаты работы всех реализация одинаковы
        float[] savedResult = null;

        // измеряем три раза для надёжности
        for (int i = 1; i <= 5; ++i) {
            System.out.println("\nпрогон " + i + ":");
            for (var t : tests) {
                float[] arr = new float[size];
                Arrays.fill(arr, 1f);
                long start = System.currentTimeMillis();
                t.func.accept(arr);
                long stop = System.currentTimeMillis();
                t.measures.add(stop - start);
                System.out.format("%30s  %7.3fс\n", t.name, (stop - start) / 1000.);

                // проверяем, что результат работы разных реализаций одинаковый
                if (savedResult == null)
                    savedResult = arr;
                for (int j = 0; j < arr.length; ++j)
                    if (Math.abs(arr[j] - savedResult[j]) > 0.0001) {
                        System.out.println("Ошибка, результаты в позиции " + j + " не совпадают: " + arr[j] + ", " +
                                savedResult[j]);
                        return;
                    }
            }
        }

        System.out.println("\nрезультаты:");
        Arrays.sort(tests, Comparator.comparingLong(TestEntry::medianTime).reversed());
        Long tmax = tests[0].medianTime();
        for (var test : tests) {
            Long t = test.medianTime();
            System.out.format("%30s, медианное время %1.3fс, скорость x%.2f\n",
                    test.name,
                    t / 1000.,
                    (float) tmax / t
            );
        }
    }

    static void calcAux(float[] arr, int start, int length, int startingEffectiveIndex) {
        // В вычислении нужно использовать не индекс в (возможно временном) массиве,
        // а "индекс элемента в изначальном массиве", т.е. когда обрабатываем вторую
        // половину индекс элемента j меняется от 0 до h, а эффективный индекс i
        // от h до size. Это нужно при расчёте верхней половины значений, в нижней
        // половине i и j совпадают.
        int i = startingEffectiveIndex;
        for (int j = start; j < start + length; ++j, ++i) {
            arr[j] = (float) (arr[j] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
    }

    static void calcSingleThread(float[] arr) {
        calcAux(arr, 0, arr.length, 0);
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

    // 1. упростим формулу, так нам вычислять на один синус меньше:
    //    x = trunc {i / 5}
    //    y = trunc {i / 2}
    //    a = 0.2
    //
    //    sin(a + x) * cos(a + x) * cos(2a + y) = 0.5 * sin(2a + 2x) * cos(2a + y)
    //
    // 2. будем также кэшировать уже найденные sin и cos, это позволит
    //    считать sin(2a + 2x) в 5 раз реже,
    //    считать cos(2a + y) в 2 раза реже
    //
    static void calcAuxOptimized(float[] arr, int start, int length, int startEffectiveIndex) {
        int i = startEffectiveIndex;
        int prevX = -1, prevY = -1;
        double prevXSinHalved = 0, prevYCos = 0;
        for (int j = start; j < start + length; ++j, ++i) {
            int x = i / 5;
            int y = i / 2;
            if (x != prevX) {
                prevXSinHalved = 0.5 * Math.sin(0.4f + 2 * x);
                prevX = x;
            }
            if (y != prevY) {
                prevYCos = Math.cos(0.4f + y);
                prevY = y;
            }
            arr[j] = (float) (arr[j] * prevXSinHalved * prevYCos);
        }
    }

    static void calcSingleThreadOptimized(float[] arr) {
        calcAuxOptimized(arr, 0, arr.length, 0);
    }

    static void calcTwoThreadsOptimized(float[] arr) {
        Thread[] threads = new Thread[2];
        for (int i = 0; i < 2; ++i) {
            int effectiveIndex = i * h; // компилятор не любит не-final вычисления в лямбде
            threads[i] = new Thread(() -> Main.calcAuxOptimized(arr, effectiveIndex, h, effectiveIndex));
            threads[i].start();
        }
        joinAll(threads);
    }

    // попробуем ручной анроллинг цикла
    static void calcAuxOptimizedUnroll(float[] arr, int start, int length, int startEffectiveIndex) {
        assert(length % 10 == 0);
        assert(startEffectiveIndex % 10 == 0);
        int i = startEffectiveIndex;
        int x2 = i / 5 * 2;
        int y = i / 2;
        final float a2 = 0.4f;
        for (int j = start; j < start + length; j += 10, x2 += 4, y += 5) {
            double exprX2s0 = Math.sin(a2 + (x2 + 0)) / 2;
            double exprYs0 = Math.cos(a2 + (y + 0));
            double mul0 = exprX2s0 * exprYs0;
            arr[j + 0] = (float) (arr[j + 0] * mul0);
            arr[j + 1] = (float) (arr[j + 1] * mul0);
            double exprYs2 = Math.cos(a2 + (y + 1));
            double mul2 = exprX2s0 * exprYs2;
            arr[j + 2] = (float) (arr[j + 2] * mul2);
            arr[j + 3] = (float) (arr[j + 3] * mul2);
            double exprYs4 = Math.cos(a2 + (y + 2));
            double mul4 = exprX2s0 * exprYs4;
            arr[j + 4] = (float) (arr[j + 4] * mul4);
            double exprX2s5 = Math.sin(a2 + (x2 + 2)) / 2;
            double mul5 = exprX2s5 * exprYs4;
            arr[j + 5] = (float) (arr[j + 5] * mul5);
            double exprYs6 = Math.cos(a2 + (y + 3));
            double mul6 = exprX2s5 * exprYs6;
            arr[j + 6] = (float) (arr[j + 6] * mul6);
            arr[j + 7] = (float) (arr[j + 7] * mul6);
            double exprYs8 = Math.cos(a2 + (y + 4));
            double mul8 = exprX2s5 * exprYs8;
            arr[j + 8] = (float) (arr[j + 8] * mul8);
            arr[j + 9] = (float) (arr[j + 9] * mul8);
        }
    }

    static void calcSingleThreadOptimizedUnroll(float[] arr) {
        calcAuxOptimizedUnroll(arr, 0, arr.length, 0);
    }

    static void calcTwoThreadsOptimizedUnroll(float[] arr) {
        Thread[] threads = new Thread[2];
        for (int i = 0; i < threads.length; ++i) {
            int effectiveIndex = i * h; // компилятор не любит не-final вычисления в лямбде
            threads[i] = new Thread(() -> Main.calcAuxOptimizedUnroll(arr, effectiveIndex, h, effectiveIndex));
            threads[i].start();
        }
        joinAll(threads);
    }

}
