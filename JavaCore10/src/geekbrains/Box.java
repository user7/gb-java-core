package geekbrains;

import java.util.ArrayList;
import java.util.function.Supplier;

public class Box<E extends Fruit> {
    private ArrayList<E> data = new ArrayList<>();

    public Box(Supplier<E> s, int count) {
        while(count-- > 0)
            data.add(s.get());
    }

    String getContentsName() {
        return data.isEmpty() ? "<пусто>" : data.get(0).getName();
    }

    void add(E e) {
        data.add(e);
    }

    public int getSize() {
        return data.size();
    }

    double getWeight() {
        if (data.isEmpty())
            return 0;
        double weight = 0;
        for (E e : data) {
            weight += e.getWeight();
        }
        return weight;
    }

    boolean compare(Box<?> other) {
        return Math.abs(getWeight() - other.getWeight()) < 0.01;
    }

    public void putToAnotherBox(Box<E> other) {
        for (E e: data) {
            other.add(e);
        }
        data.clear();
    }
}