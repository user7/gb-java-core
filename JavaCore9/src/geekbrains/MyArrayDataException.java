package geekbrains;

public class MyArrayDataException extends RuntimeException {
    private final int row;
    private final int column;
    private final String error;

    public MyArrayDataException(int row, int column, String error) {
        super("некорректные данные в строке " + (row + 1) + ", столбце " + (column + 1) + ": " + error);
        this.row = row;
        this.column = column;
        this.error = error;
    }
}
