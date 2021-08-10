package geekbrains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.BiConsumer;

public class Main {
    static final int size = 100000000;
    static final int h = size / 2;

    // интерфейс расчета диапазона для сравнения различных реализаций формулы
    @FunctionalInterface
    public interface CalcInterface {
        void accept(float[] arr, int start, int length, int startingEffectiveIndex);
    }

    enum Strategy {
        ST_SIMPLE("1 поток", Main::strategySingleThread),
        ST_COPY_2T("+2 потока c копир.", Main::strategyTwoThreadsCopy),
        ST_INPLACE_2T("+2 потока", Main::strategyTwoThreadsInplace),
        ST_INPLACE_HT("+1 поток", Main::strategyHelperThread);

        final String name;
        final BiConsumer<CalcInterface, float[]> func;
        Strategy(String name, BiConsumer<CalcInterface, float[]> func) {
            this.name = name;
            this.func = func;
        }
    };

    enum Calculator {
        CALC_SIMPLE("простой расчёт", Main::calcSimple),
        CALC_CACHED("кэш + синус 2x", Main::calcOptimized),
        CALC_UNROLL("анроллинг по 10", Main::calcUnroll),
        CALC_CHEEKY("анроллинг с л.т.", Main::calcUnrollLocalTrig);

        final String name;
        final CalcInterface func;
        Calculator(String name, CalcInterface func) {
            this.name = name;
            this.func = func;
        }
    }

    // тест состоит из стратегии, калькулятора и хранилища статистики
    record TestEntry(
            Strategy strategy,
            Calculator calculator,
            ArrayList<Long> measures  // время работы на каждый прогон теста
    )
    {
        TestEntry(Strategy strategy, Calculator calculator) {
            this(strategy, calculator, new ArrayList<>());
        }

        // медианное время по всем прогонам
        Long medianTime() {
            Collections.sort(measures);
            int s = measures.size();
            Long a = measures.get((s - 1) / 2);
            Long b = measures.get(s / 2);
            return (a + b) / 2;
        }

        String fullName() {
            return calculator.name + (strategy.name.isEmpty() ? "" : ", " + strategy.name);
        }
    }

    public static void main(String[] args) {
        testThreading();
    }

    static void testThreading() {
        assert (size == h * 2);

        // создаём тесты
        ArrayList<TestEntry> tests = new ArrayList<>();
        tests.add(new TestEntry(Strategy.ST_SIMPLE, Calculator.CALC_SIMPLE));        // первый метод из ДЗ
        tests.add(new TestEntry(Strategy.ST_COPY_2T, Calculator.CALC_SIMPLE));       // второй метод из ДЗ, с тредами
        // tests.add(new TestEntry(Strategy.ST_INPLACE_HT, Calculator.CALC_SIMPLE));    // +1 тред, без копирования
        // tests.add(new TestEntry(Strategy.ST_INPLACE_HT, Calculator.CALC_CACHED));    // +1, ускоренная версия формулы
        tests.add(new TestEntry(Strategy.ST_INPLACE_HT, Calculator.CALC_UNROLL));
        tests.add(new TestEntry(Strategy.ST_INPLACE_HT, Calculator.CALC_CHEEKY));

        // понадобится для проверки, что результаты работы всех реализация одинаковы
        float[] savedResult = null;

        // измеряем несколько раз для надёжности
        for (int i = 1; i <= 3; ++i) {
            System.out.println("\nпрогон " + i + ":");
            for (var t : tests) {
                float[] arr = new float[size];
                Arrays.fill(arr, 1f);
                long start = System.currentTimeMillis();
                t.strategy.func.accept(t.calculator.func, arr);
                long stop = System.currentTimeMillis();
                t.measures.add(stop - start);
                System.out.format("%50s  %7.3fс\n", t.fullName(), (stop - start) / 1000.);

                // проверяем, что результат работы разных реализаций одинаковый
                if (savedResult == null)
                    savedResult = arr;
                for (int j = 0; j < arr.length; ++j)
                    if (Math.abs(arr[j] - savedResult[j]) > 0.0001) {
                        System.out.println("Ошибка, результаты в позиции " + j + " не совпадают: " + arr[j] + ", " +
                                savedResult[j]);
                        break;
                    }
            }
        }

        System.out.println("\nрезультаты:\n");
        PrettyTable pt = new PrettyTable();
        pt.addColumns("стратегия", "%s", "алгоритм", "%s", "время", "%7.3fс", "скорость", "x%.2f");
        tests.sort(Comparator.comparingLong(TestEntry::medianTime).reversed());
        Long tmax = tests.get(0).medianTime();
        for (var test : tests) {
            Long t = test.medianTime();
            pt.addRow(
                test.strategy.name,
                test.calculator.name,
                t / 1000.,
                (float) tmax / t
            );
        }
        pt.print();
    }

    static void strategySingleThread(CalcInterface calc, float[] arr) {
        calc.accept(arr, 0, arr.length, 0);
    }

    static void strategyTwoThreadsCopy(CalcInterface calc, float[] arr) {
        float[] a1 = new float[h];
        System.arraycopy(arr, 0, a1, 0, h);
        Thread t1 = new Thread(() -> calc.accept(a1, 0, h, 0));
        t1.start();
        float[] a2 = new float[h];
        System.arraycopy(arr, h, a2, 0, h);
        Thread t2 = new Thread(() -> calc.accept(a2, 0, h, h));
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.arraycopy(a1, 0, arr, 0, h);
        System.arraycopy(a2, 0, arr, h, h);
    }

    static void strategyTwoThreadsInplace(CalcInterface calc, float[] arr) {
        Thread t1 = new Thread(() -> calc.accept(arr, 0, h, 0));
        Thread t2 = new Thread(() -> calc.accept(arr, h, h, h));
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void strategyHelperThread(CalcInterface calc, float[] arr) {
        Thread t1 = new Thread(() -> calc.accept(arr, 0, h, 0));
        t1.start();
        calc.accept(arr, h, h, h);
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

     // простой последовательный подсчёт по формуле
    static void calcSimple(float[] arr, int start, int length, int startingEffectiveIndex) {
        // В вычислении нужно использовать не индекс в (возможно временном) массиве,
        // а "индекс элемента в изначальном массиве", т.е. когда обрабатываем вторую
        // половину индекс элемента j меняется от 0 до h, а эффективный индекс i
        // от h до size. Это нужно при расчёте верхней половины значений, в нижней
        // половине i и j совпадают.
        int i = startingEffectiveIndex;
        for (int j = start; j < start + length; ++j, ++i) {
            arr[j] = (float) (arr[j] * (Math.sin(0.2 + i / 5) * Math.cos(0.2 + i / 5) * Math.cos(0.4 + i / 2)));
        }
    }

    // Оптимизированная версия.
    //
    // 1. Упростим формулу, чтобы вычислять на один косинус меньше. Обозначим
    //      x = (int) (i / 5)
    //      y = (int) (i / 2)
    //      a = 0.2f
    //    тогда
    //      sin(a + x) * cos(a + x) * cos(2a + y) = 0.5 * sin(2a + 2x) * cos(2a + y)
    //
    // 2. Будем также кэшировать уже найденные sin и cos, это позволит:
    //    - считать sin(2a + 2x) в 5 раз реже
    //    - считать cos(2a + y) в 2 раза реже
    //
    static void calcOptimized(float[] arr, int start, int length, int startEffectiveIndex) {
        int i = startEffectiveIndex;
        int prevX = -1, prevY = -1;
        double prevXSinHalved = 0, prevYCos = 0;
        for (int j = start; j < start + length; ++j, ++i) {
            int x = i / 5;
            int y = i / 2;
            if (x != prevX) {
                prevXSinHalved = 0.5 * Math.sin(0.4 + 2 * x);
                prevX = x;
            }
            if (y != prevY) {
                prevYCos = Math.cos(0.4 + y);
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
        double a2 = 0.4;
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

    // Анроллинг с локальным расчетом тригонометрических функций. Чтобы считать меньше выражений вида sin(q - 2) и
    // cos(r + -2..2), используем формулы синуса и косинуса суммы. Наример, sin(x - 2) = sin(x) cos(2) - cos(x) sin(2).
    // Если синус и косинус x и двойки известны, то вычисление sin(x - 2) заменяется на 2 умножения и 1 вычитание.
    // Тригонометрические функции считать сложнее, поэтому если сократить их число, то программа будет работать быстрее.
    // Посчитаем сколько тригонометрических функций можно сократить. Пусть i = 5 (mod 10), тогда для расчёта результата
    // в десяти точках {i - 5, i - 4, ..., i + 4} понадобится вычислить четыре значения тригонометрических функций:
    //
    //   sin x, cos x, sin y, cos y
    //
    //   где x = 0.4 + floor(i / 5) * 2, y = 0.4 + floor(i / 2). Искомая функция будет выражена через эти четыре
    //   значения, а также через значения sin 1, sin 2, cos 1 и cos 2, которые вычисляются вне цикла:
    //
    //   f(i - 5) = .5 * sin (x - 2) * cos(y - 2)
    //   f(i - 4) = .5 * sin (x - 2) * cos(y - 2)
    //   f(i - 3) = .5 * sin (x - 2) * cos(y - 1)
    //   f(i - 2) = .5 * sin (x - 2) * cos(y - 1)
    //   f(i - 1) = .5 * sin (x - 2) * cos(y + 0)
    //   f(i + 0) = .5 * sin x * cos(y + 0)
    //   f(i + 1) = .5 * sin x * cos(y + 1)
    //   f(i + 2) = .5 * sin x * cos(y + 1)
    //   f(i + 3) = .5 * sin x * cos(y + 2)
    //   f(i + 4) = .5 * sin x * cos(y + 2)
    //
    // Если считать напрямую, то пришлось бы посчитать все неповторяющиеся тригонометрические функции из таблицы, т.е.
    // 2 синуса и 5 косинусов, всего 7 функций против 4х в нашем решении. Взамен добавится умножений и сложений.
    // Эксперементально метод даёт ускорение примерно в 1.7 раз, что близко к 7/4. Теоретически можно увеличить шаг
    // анролла например до 20 и считать только 4 триг. функции на 20 точек, вместо обычных 14. Число умножений и
    // сложений будет расти линейно, поэтому ускорение приблизится к 14/4 = 3.5. Увеличивая шаг анролла можно продолжать
    // исключать тригонометрию до тех пор, пока не начнут доминировать умножения, полагаю ещё в 10-50 таким образом
    // ускорить можно.
    //
    static void calcUnrollLocalTrig(float[] arr, int start, int length, int startEffectiveIndex) {
        assert (length % 10 == 0);
        assert (startEffectiveIndex % 10 == 0);
        int i = startEffectiveIndex;
        int x2 = i / 5 * 2;
        int y = i / 2;
        double a2 = 0.4;
        double sin1 = Math.sin(1), cos1 = Math.cos(1);
        double sin2 = Math.sin(2), cos2 = Math.cos(2);
        for (int j = start; j < start + length; j += 10, x2 += 4, y += 5) {
            double xbase = a2 + 2 + x2;
            double ybase = a2 + 2 + y;
            double xs5 = Math.sin(xbase), xc5 = Math.cos(xbase);
            double xs0 = xs5 * cos2 - xc5 * sin2;

            double yc4 = Math.cos(ybase), ys4 = Math.sin(ybase);
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
