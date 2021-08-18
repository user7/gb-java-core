package geekbrains.javacore2;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class MainTest {
    static boolean testOk = true;

    @BeforeEach
    void startUp() {
        testOk = true;
    }

    @AfterEach
    void shutDown() {
        if (!testOk)
            System.out.println("Тест провалился");
    }

    @DisplayName("Минимум и максимум в массиве")
    @Test
    public void minMax() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> Main.minMax(new int[]{}));
        Assertions.assertEquals(Main.minMax(new int[]{5}), new Main.MinMax(5, 5));
        Assertions.assertEquals(Main.minMax(new int[]{1, 5, 0, -2}), new Main.MinMax(-2, 5));
    }

    @DisplayName("Сбалансированный массив")
    @ParameterizedTest
    @MethodSource("checkBalanceData")
    public void checkBalance(int[] a, boolean result) {
        Assertions.assertEquals(Main.checkBalance(a), result);
    }

    static Stream<Arguments> checkBalanceData() {
        return Stream.of(
                Arguments.arguments(new int[]{}, true),
                Arguments.arguments(new int[]{}, true),
                Arguments.arguments(new int[]{0}, true),
                Arguments.arguments(new int[]{1}, false),
                Arguments.arguments(new int[]{0, 1}, false),
                Arguments.arguments(new int[]{1, 1}, true),
                Arguments.arguments(new int[]{3, 5, 2, 2, 2, 2}, true),
                Arguments.arguments(new int[]{-2, -3, -5}, true),
                Arguments.arguments(new int[]{3, -1, 2}, true)
        );
    }

    @DisplayName("Наибольший общий делитель")
    @ParameterizedTest
    @MethodSource("gcdData")
    public void gcd(int a, int b, int result) {
        Assertions.assertEquals(Main.gcd(a, b), result);
    }

    static @NotNull
    Stream<Arguments> gcdData() {
        return Stream.of(
                Arguments.arguments(3, 10, 1),
                Arguments.arguments(-3, 10, 1),
                Arguments.arguments(3, -10, 1),
                Arguments.arguments(10, 100, 10),
                Arguments.arguments(10, 102, 2),
                Arguments.arguments(10, -102, 2),
                Arguments.arguments(10, 105, 5),
                Arguments.arguments(10, -105, 5)
        );
    }

    @DisplayName("Сдвиг массива")
    @ParameterizedTest
    @MethodSource("rotateData")
    void rotate(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; ++i)
            a[i] = i;
        int[] b = new int[n];
        // проверяем для всех сдвигов в диапазоне -n..n
        for (int j = -n; j <= n; ++j) {
            // заполнили массив b со сдвигом -j
            for (int i = 0; i < n; ++i)
                b[(i - j + n) % n] = i;
            // сдвинули массив b на j, должно получиться то же, что и было
            Main.rotate(b, j);
            Assertions.assertArrayEquals(a, b);
        }
    }

    static @NotNull
    Stream<Arguments> rotateData() {
        return Stream.of(
                Arguments.arguments(1),
                Arguments.arguments(10),
                Arguments.arguments(7),
                Arguments.arguments(8)
        );
    }
}