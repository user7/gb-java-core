package geekbrains;

public class Dog extends Animal {
    private static int count = 0;

    public static int getCount() {
        return count;
    }

    Dog(String name, String color, int age) {
        super(name, color, age);
        ++count;
    }

    @Override
    public void run(int meters) {
        System.out.println(meters > 500 ?
                (name + " не может пробежать " + meters + " метров") :
                (name + " пробежал " + meters + " метров"));
    }

    @Override
    public void swim(int meters) {
        System.out.println(meters > 10 ?
                (name + " не может проплыть " + meters + " метров") :
                (name + " проплыл " + meters + " метров"));
    }
}
