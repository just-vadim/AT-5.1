package ru.netology.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class DateGenerator {
    private DateGenerator() {};

    /* Метод генерирует случайную дату в диапазоне от +3 до +30 дней от сегодняшней даты */
    public static String generateDate() {
        Random random = new Random();
        int randomInt = random.nextInt(31) + 3;
        LocalDate date = LocalDate.now().plusDays(randomInt);
        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        return formattedDate;
    }
}
