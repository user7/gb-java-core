package geekbrains;

public class Main {

    public static void main(String[] args) {
        Traveller[] travellers = new Traveller[] {
            new Robot("Фёдор", 5, 0),
            new Cat("Гарфилд", 1000, 2),
            new Human("Валера", 3000, 1),
            new Robot("Дарпи", 10000000, 10)
        };
        Obstacle[] obstacles = new Obstacle[] {
            new Track(10),
            new Wall(1),
            new Track(500),
            new Wall(2),
            new Track(10000),
            new Track(40000),
            new Track(90000),
        };
        for (Traveller t : travellers) {
            boolean finished = true;
            for (Obstacle o : obstacles) {
                if (!o.interact(t)) {
                    finished = false;
                    break;
                }
            }
            if (finished)
                System.out.println(t.fullName() + " закончил дистанцию");
        }
    }
}
