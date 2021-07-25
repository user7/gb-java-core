package geekbrains;

public abstract class Animal {
    protected final String name;
    protected final String color;
    protected final int age;

    private static int count = 0;

    public static int getCount() {
        return count;
    }

    Animal(String name, String color, int age) {
        this.name = name;
        this.color = color;
        this.age = age;
        count++;
    }

    Animal(String name) {
        this(name, "серобуромалиновый", 0);
    }

    abstract public void run(int meters);
    abstract public void swim(int meters);
}
