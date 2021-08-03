package geekbrains;

import java.util.ArrayList;

public class Box<E extends Fruit> {
    private ArrayList<E> data = new ArrayList<>();

    public Box(E e, int count) {
        while(count-- > 0)
            data.add(e);
    }

    void add(E e) {
        data.add(e);
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
}
