package geekbrains;

public class Human implements Traveller {
    private final String name;
    private final int runDistance;
    private final int jumpHeight;

    Human(String name, int runDistance, int jumpHeight) {
        this.name = name;
        this.runDistance = runDistance;
        this.jumpHeight = jumpHeight;
    }

    @Override
    public String fullName() {
        return "человек " + name;
    }

    @Override
    public boolean run(int distance) {
        if (runDistance >= distance) {
            System.out.println("человек " + name + " пробежал " + distance + " метров");
            return true;
        } else {
            System.out.println("человек " + name + " не смог пробежать " + distance + " метров");
            return false;
        }
    }

    @Override
    public boolean jump(int height) {
        if (jumpHeight >= height) {
            System.out.println("человек " + name + " прыгнул на " + height + " метров");
            return true;
        } else {
            System.out.println("человек " + name + " не смог прыгнуть на " + height + " метров");
            return false;
        }
    }
}
