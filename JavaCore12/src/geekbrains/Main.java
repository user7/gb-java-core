package geekbrains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;

public class Main {
    static final int size = 20000000;
    static final int h = size / 2;

    record TestEntry(String name, Consumer<float[]> func, ArrayList<Long> measures) {
        Long medianTime() {
            Collections.sort(measures);
            return (measures.get(measures.size() / 2) + measures.get((measures.size() + 1) / 2)) / 2;
        }
    }

    public static void main(String[] args) {
        assert (size == h * 2);

        // список тестируемых алгоритмов
        int xt = 0;
        TestEntry[] tests = new TestEntry[]{
                mkt("простой", Main::calcSimple, 0),
                mkt("простой", Main::calcSimple, 1),
                mkt("простой", Main::calcSimple, 2),
                mkt("кэширование триг. ф-ций", Main::calcOptimized, 0),
                mkt("анроллинг", Main::calcUnroll, 0),
                mkt("анроллинг+чит", Main::calcUnrollCheat, 0),
                mkt("анроллинг+чит", Main::calcUnrollCheat, 2),
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
                    if (Math.abs(arr[j] - savedResult[j]) > 0.005) {
                        System.out.println("Ошибка, результаты в позиции " + j + " не совпадают: " + arr[j] + ", " +
                                savedResult[j]);
                        //break;
                    }
            }
        }

        System.out.println("\nрезультаты:");
        Arrays.sort(tests, Comparator.comparingLong(TestEntry::medianTime).reversed());
        Long tmax = tests[0].medianTime();
        for (var test : tests) {
            Long t = test.medianTime();
            System.out.format("%30s, медианное время %7.3fс, скорость x%.2f\n",
                    test.name,
                    t / 1000.,
                    (float) tmax / t
            );
        }
    }

    @FunctionalInterface
    public interface CalcInterface {
        void accept(float[] arr, int start, int length, int startingEffectiveIndex);
    }

    static TestEntry mkt(String name, CalcInterface calc, int extraThreads) {
        return new TestEntry(
                name + (extraThreads == 0 ? "" : extraThreads == 1 ? ", +1 тред" : ", +2 треда"),
                mktFunc(calc, extraThreads),
                new ArrayList<>()
        );
    }

    static Consumer<float[]> mktFunc(CalcInterface calc, int extraThreads) {
        switch (extraThreads) {
            case 0:
                return arr -> calc.accept(arr, 0, arr.length, 0);

            case 1: // "полтора треда", создаём только один дополнительный, вторую половину обрабатываем в основном
                return arr -> {
                    Thread t = new Thread(() -> calc.accept(arr, 0, h, 0));
                    t.start();
                    calc.accept(arr, h, h, h);
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };

            case 2:
                return arr -> {
                    Thread[] threads = new Thread[2];
                    for (int i = 0; i < threads.length; ++i) {
                        int effectiveIndex = i * h; // компилятор не любит не-final вычисления в лямбде
                        threads[i] = new Thread(() -> calc.accept(arr, effectiveIndex, h, effectiveIndex));
                        threads[i].start();
                    }
                    for (var t : threads) {
                        try {
                            t.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };

            default:
                throw new IllegalArgumentException("неподдерживаемое число дополнительных тредов " + extraThreads);
        }
    }

    // простой последовательны подсчёт по формуле
    static void calcSimple(float[] arr, int start, int length, int startingEffectiveIndex) {
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

    // Оптимизированная версия
    //
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
    static void calcOptimized(float[] arr, int start, int length, int startEffectiveIndex) {
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

    // Попробуем ручной анроллинг цикла
    static void calcUnroll(float[] arr, int start, int length, int startEffectiveIndex) {
        assert (length % 10 == 0);
        assert (startEffectiveIndex % 10 == 0);
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

    // Читерский анроллинг, чтобы считать меньше выражений вида sin(q - 2) и cos(r + -2..2), используя формулы
    // суммы и значения sin/cos для -2..2, которые можно вычислить однократно на весь цикл. Поскольку умножение быстрее
    // тригонометрии, то ускорение заметное. К сожалению, иногда этот метод даёт результат отличающийся от посчитанного
    // напрямую больше чем на 0.01, не вполне понятно почему так. Ошибки распределены неравномерно, особенно большая
    // ошибка возникает в элементе 16777216 при подсчёте от центра с поправкой -2..+2, или в элементе 16777218
    // при подсчёте от начала с поправкой +1..+4 (старый вариант). Примеры аномалий:
    //
    // Ошибка, результаты в позиции 1048578 не совпадают: -9.0533524E-4, 0.0064106667
    // Ошибка, результаты в позиции 1048579 не совпадают: -9.0533524E-4, 0.0064106667
    // Ошибка, результаты в позиции 16777216 не совпадают: 0.49849874, 0.45015603
    // Ошибка, результаты в позиции 16777217 не совпадают: 0.49849874, 0.45015603
    // Ошибка, результаты в позиции 16777218 не совпадают: 0.24708061, 0.42479157
    // Ошибка, результаты в позиции 16777219 не совпадают: 0.24708061, 0.42479157
    //
    static void calcUnrollCheat(float[] arr, int start, int length, int startEffectiveIndex) {
        assert (length % 10 == 0);
        assert (startEffectiveIndex % 10 == 0);
        int i = startEffectiveIndex;
        int x2 = i / 5 * 2;
        int y = i / 2;
        float a2 = 0.4f;
        double sin1 = Math.sin(1), cos1 = Math.cos(1);
        double sin2 = Math.sin(2), cos2 = Math.cos(2);
        for (int j = start; j < start + length; j += 10, x2 += 4, y += 5) {
            double xs5 = Math.sin(a2 + (x2 + 2)), xc5 = Math.cos(a2 + (x2 + 2));
            double xs0 = xs5 * cos2 - xc5 * sin2;

            double yc4 = Math.cos(a2 + (y + 2)), ys4 = Math.sin(a2 + (y + 2));
            double yc0 = yc4 * cos2 + ys4 * sin2;
            double yc2 = yc4 * cos1 + ys4 * sin1;
            double yc6 = yc4 * cos1 - ys4 * sin1;
            double yc8 = yc4 * cos2 - ys4 * sin2;

            double mul0 = xs0 * yc0 / 2;
            double mul2 = xs0 * yc2 / 2;
            double mul4 = xs0 * yc4 / 2;
            double mul5 = xs5 * yc4 / 2;
            double mul6 = xs5 * yc6 / 2;
            double mul8 = xs5 * yc8 / 2;

            arr[j + 0] = (float) (arr[j + 0] * mul0);
            arr[j + 1] = (float) (arr[j + 1] * mul0);
            arr[j + 2] = (float) (arr[j + 2] * mul2);
            arr[j + 3] = (float) (arr[j + 3] * mul2);
            arr[j + 4] = (float) (arr[j + 4] * mul4);
            arr[j + 5] = (float) (arr[j + 5] * mul5);
            arr[j + 6] = (float) (arr[j + 6] * mul6);
            arr[j + 7] = (float) (arr[j + 7] * mul6);
            arr[j + 8] = (float) (arr[j + 8] * mul8);
            arr[j + 9] = (float) (arr[j + 9] * mul8);
        }
    }

}
