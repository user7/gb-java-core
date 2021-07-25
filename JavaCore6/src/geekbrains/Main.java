package geekbrains;

public class Main {

    public static void main(String[] args) {
        Dog d1 = new Dog("Рекс", "Серый", 3);
        Dog d2 = new Dog("Тирекс", "Чёрный", 4);
        Cat c1 = new Cat("Барсик", "Белый", 5);
        d1.run(300);
        d1.run(700);
        d2.swim(5);
        d2.swim(50);
        c1.run(50);
        c1.run(300);
        c1.swim(5);
        System.out.println("всего создано животных: " + Animal.getCount());
        System.out.println("всего создано собак: " + Dog.getCount());
        System.out.println("всего создано котов: " + Cat.getCount());
    }
}
