package geekbrains;

import java.util.ArrayList;
import java.util.HashMap;

public class PhoneBook {
    private HashMap<String, ArrayList<String>> data = new HashMap<>();

    public void add(String name, String phone) {
        data.compute(name, (n, phones) -> {
            if (phones == null)
                phones = new ArrayList<>();
            phones.add(phone);
            return phones;
        });
    }

    public void get(String name) {
        ArrayList<String> phones = data.get(name);
        if (phones == null) {
            System.out.println(name + " отсутствует в книге");
            return;
        }
        System.out.print(name + ":");
        for (String phone : phones)
            System.out.print(" " + phone);
        System.out.println();
    }
}
