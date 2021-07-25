package geekbrains;

public class Main {

    public static void main(String[] args) {
        Cat[] cats = new Cat[]{
                new Cat("Пират", 3),
                new Cat("Барсик", 2),
                new Cat("Марсик", 10),
                new Cat("Рыжий", 1)
        };
        Plate p = new Plate(7);
        for(Cat c : cats) {
            c.eat(p);
        }

        p.putFood(9);
        cats[2].eat(p);
    }
}
