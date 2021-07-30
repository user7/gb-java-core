package geekbrains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Main {

    public static void main(String[] args) {
        test1();
        test2();
    }

    static void test1() {
        System.out.println("*** test 1");
        printUnique(new String[] {
                "Alex",
                "Brooks",
                "Brooks",
                "Sam",
                "Mila",
                "Mila",
                "Brooks",
                "Aisha",
                "Alex",
        });
    }

    static void test2() {
        System.out.println("\n*** test 2");
        PhoneBook phoneBook = new PhoneBook();
        phoneBook.add("Аня", "+7000");
        phoneBook.add("Джон", "099");
        phoneBook.add("Аня", "+7002");
        phoneBook.add("Ингрид", "+999");
        phoneBook.get("Аня");
        phoneBook.get("Джон");
        phoneBook.get("Карл");
    }

    static void printUnique(String[] words) {
        LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();
        for (String w : words)
            counts.put(w, counts.getOrDefault(w, 0) + 1);
        counts.forEach((key, value) -> {
            System.out.println(key + " " + value);
        });
    }

}
