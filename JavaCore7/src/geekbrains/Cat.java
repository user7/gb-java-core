package geekbrains;

public class Cat {
    private final String name;
    private final int appetite;
    private boolean fed = false;

    public Cat(String name, int appetite) {
        this.name = name;
        this.appetite = appetite;
    }

    public void eat(Plate plate) {
        fed = plate.takeFood(appetite);
        if (fed)
            System.out.print(name + " поел");
        else
            System.out.print(name + " остался голодным");
        System.out.println(", в тарелке осталось " + plate.getFood());
    }
}
