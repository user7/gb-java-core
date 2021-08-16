package geekbrains.javacore5;

class Employee {
    private final String name;
    private final String position;
    private final String email;
    private final String phone;
    private final long salary;
    private final int age;

    public int getAge() {
        return age;
    }

    public Employee(String name, String position, String email, String phone, long salary, int age) {
        this.name = name;
        this.position = position;
        this.email = email;
        this.phone = phone;
        this.salary = salary;
        this.age = age;
    }

    public void print() {
        System.out.println("Сотрудник: " + name);
        System.out.println("Должность: " + position);
        System.out.println("    Email: " + email);
        System.out.println("  Телефон: " + phone);
        System.out.println(" Зарплата: " + salary);
        System.out.println("  Возраст: " + age);
    }
}


public class Main {

    public static void main(String[] args) {
        Employee[] employees = new Employee[5];
        employees[0] = new Employee("Маша", "Директор", "masha@mail.ru", "+79990000000", 99000, 38);
        employees[1] = new Employee("Миша", "Водитель", "misha@mail.ru", "+79990000001", 33000, 41);
        employees[2] = new Employee("Макс", "Охранник", "maxik@mail.ru", "+79990000002", 34000, 21);
        employees[3] = new Employee("Каха", "Рекрутер", "kaha1@mail.ru", "+79990000003", 35000, 24);
        employees[4] = new Employee("Марк", "Юрист", "mark@mail.ru", "+79990000004", 199000, 55);
        for (Employee employee : employees) {
            if (employee.getAge() > 40) {
                employee.print();
                System.out.println("--");
            }
        }
    }
}
