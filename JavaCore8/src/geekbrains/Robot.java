package geekbrains;

public class Robot implements Traveller {
    private final String name;
    private final int runDistance;
    private final int jumpHeight;

    @Override
    public String fullName() {
        return "робот " + name;
    }

    Robot(String name, int runDistance, int jumpHeight) {
        this.name = name;
        this.runDistance = runDistance;
        this.jumpHeight = jumpHeight;
    }

    @Override
    public boolean run(int distance) {
        if (runDistance >= distance) {
            System.out.println(fullName() + " пробежал " + distance + " метров");
            return true;
        } else {
            System.out.println(fullName() + " не смог пробежать " + distance + " метров");
            return false;
        }
    }

    @Override
    public boolean jump(int height) {
        if (jumpHeight >= height) {
            System.out.println(fullName() + " прыгнул на " + height + " метров");
            return true;
        } else {
            System.out.println(fullName() + " не смог прыгнуть на " + height + " метров");
            return false;
        }
    }
}
