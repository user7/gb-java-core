package geekbrains;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class PhoneBook {
    private HashMap<String, ArrayList<String>> data = new HashMap<>();

    public void add(String name, String phone) {
        ArrayList<String> entry = data.getOrDefault(name, new ArrayList<>());
        entry.add(phone);
        data.put(name, entry);
    }

    public void get(String name) {
        ArrayList<String> entry = data.getOrDefault(name, new ArrayList<>());
        System.out.println(name + ":");
        for (String phone : entry) {
            System.out.println(name + " " + phone);
        }
        System.out.println("--");
    }
}
