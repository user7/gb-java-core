package geekbrains;

public class Cat extends Animal {
    private static int count = 0;

    public static int getCount() {
        return count;
    }

    Cat(String name, String color, int age) {
        super(name, color, age);
        ++count;
    }

    @Override
    public void run(int meters) {
        System.out.println(meters > 200 ?
                           (name + " не может пробежать " + meters + " метров") :
                           (name + " пробежал " + meters + " метров"));
    }

    @Override
    public void swim(int meters) {
        System.out.println(name + " не умеет плавать");
    }
}
