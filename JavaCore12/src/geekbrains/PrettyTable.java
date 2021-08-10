package geekbrains;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class PrettyTable {
    private record Column(String title, String format) {}
    private ArrayList<Column> columns = new ArrayList<>();
    private ArrayList<ArrayList<String>> rows = new ArrayList<>();
    private HashMap<Integer, Integer> widthMap = new HashMap<>();

    void print() {
        int w = -1;
        for (int ci = 0; ci < columns.size(); ++ci) {
            System.out.format("| %" + getWidth(ci) + "s ", columns.get(ci).title);
            w += getWidth(ci) + 3;
        }
        System.out.println("|\n|" + "-".repeat(w) + "|");
        for (var r : rows) {
            for (int ci = 0; ci < columns.size(); ++ci) {
                System.out.format("| %" + getWidth(ci) + "s ", ci < r.size() && r.get(ci) != null ? r.get(ci) : "");
            }
            System.out.println("|");
        }
    }

    void addColumns(String... nameFormat) {
        for (int i = 0; i + 1 < nameFormat.length; i += 2) {
            String name = nameFormat[i], format = nameFormat[i + 1];
            columns.add(new Column(name, format));
            widenMaybe(columns.size() - 1, name.length());
        }
    }

    void addRow(Object... args) {
        ArrayList<String> r = new ArrayList<>();
        for (int ci = 0; ci < columns.size() && ci < args.length; ++ci) {
            var a = args[ci];
            var col = columns.get(ci);
            String cell = "";
            if (a != null) {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                PrintStream out = new PrintStream(buf, true, StandardCharsets.UTF_8);
                out.format(col.format, a);
                out.flush();
                cell = buf.toString();
                widenMaybe(ci, cell.length());
            }
            r.add(cell);
        }
        rows.add(r);
    }

    private int getWidth(int ci) {
        return widthMap.containsKey(ci) ? widthMap.get(ci) : 0;
    }

    private void widenMaybe(int ci, int width) {
        widthMap.put(ci, Math.max(getWidth(ci), width));
    }
}