package geekbrains;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        test(null);

        test(new String[][] {{"1", "2"}});

        test(new String[][] { null, null, null, null });

        test(new String[][] {
                {"0", "0", "0", "0"},
                {"0", "0", "0", "0"},
                {"1", "0", "0", "0", "0"},
                {"0", "0", "0", "0"}
        });

        test(new String[][] {
                {"1", "0", "0", "0"},
                {"0", "2", "0", "0"},
                {"0", "0", "0", "0"},
                {"0", "0", "0", "0"}
        });

        test(new String[][] {
                {"1", "0", "0", "0"},
                {"0", "Z", "0", "0"},
                {"0", "0", "0", "0"},
                {"0", "0", "0", "0"}
        });
    }

    public static void test(String[][] data) {
        System.out.print("Исходный массив:");
        if (data == null)
            System.out.println(" null");
        else {
            System.out.println();
            for (String[] row : data) {
                System.out.print("  ");
                if (row == null)
                    System.out.print("null row");
                else
                    for (String s : row) {
                        System.out.print(s);
                        System.out.print(" ");
                    }
                System.out.println();
            }
        }

        System.out.print("Результат: ");
        try {
            int s = sumMyArray(data);
            System.out.println("сумма равна " + s);
        } catch (MyArraySizeException e) {
            System.out.println("неверный размер массива");
        } catch (MyArrayDataException e) {
            System.out.println(e.getMessage());
        }
        System.out.println();
    }

    public static int sumMyArray(String[][] data) throws MyArraySizeException {
        if (data == null || data.length != 4)
            throw new MyArraySizeException("неправильное число строк");
        for (String[] row : data)
            if (row == null || row.length != 4)
                throw new MyArraySizeException("неправильное число колонок");
        int sum = 0;
        for (int r = 0; r < data.length; ++r) {
            for (int c = 0; c < data[r].length; ++c) {
                try {
                    int p = Integer.parseInt(data[r][c]);
                    sum += p;
                } catch(NumberFormatException e) {
                    throw new MyArrayDataException(r, c, e.getMessage());
                }
            }
        }
        return sum;
    }
}
