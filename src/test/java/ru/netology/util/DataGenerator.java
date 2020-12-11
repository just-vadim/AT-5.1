package ru.netology.util;

import com.github.javafaker.Faker;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class DataGenerator {
    private DataGenerator() {}

    /* Метод генерирует случайную дату в диапазоне от +3 до +30 дней от сегодняшней даты */
    public static String generateDate() {
        Random random = new Random();
        int randomInt = random.nextInt(31) + 3;
        LocalDate date = LocalDate.now().plusDays(randomInt);
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    /*Метод выбирает случайный город из файла cities.json*/
    public static String generateCity() {
        ArrayList<String> citiesList = new ArrayList<>();
        Random random = new Random();
        String filePath = "./src/test/resources/cities.json";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONArray cities = (JSONArray) jsonObject.get("city");

        for (Object city : cities) {
            citiesList.add(city.toString());
        }

        return citiesList.get(random.nextInt(citiesList.size()));
    }

    public static SubmitByCityNamePhone generateByCityNamePhone() {
        Faker faker = new Faker(new Locale("ru"));
        return new SubmitByCityNamePhone(
                generateCity(),
                faker.name().lastName().replace("ё", "е") + " " +
                        faker.name().firstName().replace("ё", "е"),
                faker.phoneNumber().phoneNumber());
    }

    public static String generateInvalidPhone() {
        Faker faker = new Faker(new Locale("ru"));
        return faker.phoneNumber().phoneNumber().substring(0, 6);
    }

    public static String generateNameWithLetterYo() {
        Faker faker = new Faker(new Locale("ru"));
        String firstName;
        String lastName;
        boolean containsYo = false;
        do {
            firstName = faker.name().firstName();
            lastName = faker.name().lastName();
            if (firstName.contains("ё") || lastName.contains("ё")) {
                containsYo = true;
            }
        } while (!containsYo);
        return firstName + " " + lastName;
    }
}