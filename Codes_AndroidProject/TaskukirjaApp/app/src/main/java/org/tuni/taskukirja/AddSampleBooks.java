package org.tuni.taskukirja;

public class AddSampleBooks {

    public static String TAG = "";

    public static void addSamples(BookViewModel model) {
        Book book1 = new Book(1,"Mikki kiipelissä", "1970", "256", "12.2.2022");
        Book book2 = new Book(2,"Aku Ankka ja Karhukopla", "1970", "256", "12.2.2022");
        Book book3 = new Book(3,"Mikki ja viidakon vaarat", "1970", "256", "12.2.2022");
        Book book4 = new Book(4,"Älä hermoile, Roope-setä", "1970", "256", "12.2.2022");
        Book book5 = new Book(5,"Mikki ja Hessu timanttien jäljillä", "1971", "256", "12.2.2022");
        Book book6 = new Book(6,"Aku Baba ja viisi rosvoa", "1971", "256", "13.2.2022");
        Book book7 = new Book(7,"Mikki Hiiren kuuma kesä", "1971", "256", "13.2.2022");
        Book book8 = new Book(8, "Aku ja Amerikan keisari", "1971", "256", "13.2.2022");
        Book book9 = new Book(9,"Ota rennosti, Aku","1972","256", "13.2.2022");
        Book book10 = new Book(10, "Roope näkee punaista", "1972","256", "13.2.2022");
        Book book401 = new Book(401, "Tuplanolla ja uhka taivaalta", "2013", "512", "14.2.2022");

        model.insert(book1);
        model.insert(book2);
        model.insert(book3);
        model.insert(book4);
        model.insert(book5);
        model.insert(book6);
        model.insert(book7);
        model.insert(book8);
        model.insert(book9);
        model.insert(book10);
        model.insert(book401);
    }
}
