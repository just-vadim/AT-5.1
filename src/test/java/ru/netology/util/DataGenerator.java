package ru.netology.util;

import com.github.javafaker.Faker;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class DataGenerator {
    private DataGenerator() {};

    public static String generateCity() throws IOException, ParseException {
        ArrayList<String> citiesList = new ArrayList<>();
        Random random = new Random();
        String filePath = "./src/test/resources/cities.json";
        FileReader reader = new FileReader(filePath);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
        JSONArray cities = (JSONArray) jsonObject.get("city");

        for (int i = 0; i < cities.size(); i++) {
            citiesList.add(cities.get(i).toString());
        }

        String randomCity = citiesList.get(random.nextInt(citiesList.size()));
        return randomCity;
    }

    /* Метод генерирует случайную дату в диапазоне от +3 до +30 дней от сегодняшней даты */
    public static String generateDate() {
        Random random = new Random();
        int randomInt = random.nextInt(31) + 3;
        LocalDate date = LocalDate.now().plusDays(randomInt);
        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        return formattedDate;
    }

    public static SubmitByNamePhone generateByNamePhone() {
        Faker faker = new Faker(new Locale("ru"));
        return new SubmitByNamePhone(
                faker.name().lastName() + " " + faker.name().firstName(),
                faker.phoneNumber().phoneNumber());
    }
}