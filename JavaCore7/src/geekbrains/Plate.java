package geekbrains;

public class Plate {
    private int food;

    public int getFood() {
        return food;
    }

    public Plate(int food) {
        this.food = food;
    }

    public Plate() {
        this(0);
    }

    public void putFood(int food) {
        this.food += food;
    }

    public boolean takeFood(int food) {
        if (this.food >= food) {
            this.food -= food;
            return true;
        } else {
            return false;
        }
    }
}
