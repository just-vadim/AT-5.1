package ru.netology.appcarddelivery;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.util.SubmitByNamePhone;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.Keys.chord;
import static ru.netology.util.DataGenerator.*;

public class AppCardDeliveryV2Test {

    SubmitByNamePhone submitData = generateByNamePhone();

    private SelenideElement dateForm = $("[data-test-id='date'] .input__control");

    private SelenideElement inputDate(SelenideElement item) {
        item.sendKeys(chord(Keys.CONTROL, "a") + Keys.DELETE);
        item.setValue(generateDate());
        return item;
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:9999/");
    }

    @Test
    void shouldSubmitRequest() throws IOException, ParseException {
        $("[data-test-id='city'] .input__control").setValue(generateCity());
        inputDate(dateForm);
        String selectedDate = $("[data-test-id='date'] .input__control").getAttribute("value");
        $("[name='name']").setValue(submitData.getName());
        $("[name='phone']").setValue(submitData.getPhone());
        $(".checkbox__box").click();
        $(".button__content").click();
        String text = $(withText("Встреча успешно")).waitUntil(Condition.visible, 15000 ).getText();
        assertEquals("Встреча успешно запланирована на " + selectedDate, text.trim());
    }

    @Test
    void shouldNotSubmitIfEmptyCity() {
        inputDate(dateForm);
        $("[name='name']").setValue("Василий Петров");
        $("[name='phone']").setValue("+79067778899");
        $(".checkbox__box").click();
        $(".button__content").click();
        String text = $(".input_invalid[data-test-id='city']").$(".input__sub").getText();
        assertEquals("Поле обязательно для заполнения", text.trim());
    }
    @Test
    void shouldNotSubmitIfEmptyDate() {
        $("[data-test-id='city'] .input__control").setValue("Москва");
        dateForm.sendKeys(chord(Keys.CONTROL, "a") + Keys.DELETE);
        dateForm.setValue(generateDate());
        $("[name='name']").setValue("Василий Петров");
        $("[name='phone']").setValue("+79067778899");
        $(".checkbox__box").click();
        $(".button__content").click();
        String text = $("[data-test-id='date']").$(".input_invalid").$(".input__sub").getText();
        assertEquals("Неверно введена дата", text.trim());
    }

    @Test
    void shouldNotSubmitIfEmptyName() {
        $("[data-test-id='city'] .input__control").setValue("Москва");
        inputDate(dateForm);
        $("[name='phone']").setValue("+79067778899");
        $(".checkbox__box").click();
        $(".button__content").click();
        String text = $(".input_invalid[data-test-id='name']").$(".input__sub").getText();
        assertEquals("Поле обязательно для заполнения", text.trim());
    }

    @Test
    void shouldNotSubmitIfEmptyPhone() {
        $("[data-test-id='city'] .input__control").setValue("Москва");
        inputDate(dateForm);
        $("[name='name']").setValue("Василий Петров");
        $(".checkbox__box").click();
        $(".button__content").click();
        String text = $(".input_invalid[data-test-id='phone']").$(".input__sub").getText();
        assertEquals("Поле обязательно для заполнения", text.trim());
    }

    @Test
    void shouldNotSubmitIfAgreementNotChecked() {
        $("[data-test-id='city'] .input__control").setValue("Москва");
        inputDate(dateForm);
        $("[name='name']").setValue("Василий Петров");
        $("[name='phone']").setValue("+79067778899");
        $(".button__content").click();
        String text = $(".input_invalid[data-test-id='agreement']").getText();
        assertEquals("Я соглашаюсь с условиями обработки и использования моих персональных данных", text.trim());
    }

    @Test
    void shouldNotSubmitIfInaccessibleCity() {
        $("[data-test-id='city'] .input__control").setValue("Колыма");
        inputDate(dateForm);
        $("[name='name']").setValue("Василий Петров");
        $("[name='phone']").setValue("+79067778899");
        $(".checkbox__box").click();
        $(".button__content").click();
        String text = $(".input_invalid[data-test-id='city']").$(".input__sub").getText();
        assertEquals("Доставка в выбранный город недоступна", text.trim());
    }

    @Test
    void shouldNotSubmitIfInaccessibleDate() {
        LocalDate date = LocalDate.now();
        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id='city'] .input__control").setValue("Москва");
        dateForm.sendKeys(chord(Keys.CONTROL, "a") + Keys.DELETE);
        dateForm.setValue(formattedDate);
        $("[name='name']").setValue("Василий Петров");
        $("[name='phone']").setValue("+79067778899");
        $(".checkbox__box").click();
        $(".button__content").click();
        String text = $("[data-test-id='date']").$(".input_invalid").$(".input__sub").getText();
        assertEquals("Заказ на выбранную дату невозможен", text.trim());
    }

    @Test
    void shouldNotSubmitIfPastDate() {
        String pastDate = "01.01.2020";
        $("[data-test-id='city'] .input__control").setValue("Москва");
        dateForm.sendKeys(chord(Keys.CONTROL, "a") + Keys.DELETE);
        dateForm.setValue(pastDate);
        $("[name='name']").setValue("Василий Петров");
        $("[name='phone']").setValue("+79067778899");
        $(".checkbox__box").click();
        $(".button__content").click();
        String text = $("[data-test-id='date']").$(".input_invalid").$(".input__sub").getText();
        assertEquals("Заказ на выбранную дату невозможен", text.trim());
    }

    @Test
    void shouldNotSubmitIfLatinName() {
        $("[data-test-id='city'] .input__control").setValue("Москва");
        inputDate(dateForm);
        $("[name='name']").setValue("Vasiliy Petrov");
        $("[name='phone']").setValue("+79067778899");
        $(".checkbox__box").click();
        $(".button__content").click();
        String text = $(".input_invalid[data-test-id='name']").$(".input__sub").getText();
        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", text.trim());
    }

    @Test
    void shouldNotSubmitIfNumeralsInName() {
        $("[data-test-id='city'] .input__control").setValue("Москва");
        inputDate(dateForm);
        $("[name='name']").setValue("Василий Петр0в");
        $("[name='phone']").setValue("+79067778899");
        $(".checkbox__box").click();
        $(".button__content").click();
        String text = $(".input_invalid[data-test-id='name']").$(".input__sub").getText();
        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", text.trim());
    }

    @Test
    void shouldNotSubmitIfPhoneWithoutPlus() {
        $("[data-test-id='city'] .input__control").setValue("Москва");
        inputDate(dateForm);
        $("[name='name']").setValue("Василий Петров");
        $("[name='phone']").setValue("89067778899");
        $(".checkbox__box").click();
        $(".button__content").click();
        String text = $(".input_invalid[data-test-id='phone']").$(".input__sub").getText();
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", text.trim());
    }

    @Test
    void shouldNotSubmitIfLettersInPhone() {
        $("[data-test-id='city'] .input__control").setValue("Москва");
        inputDate(dateForm);
        $("[name='name']").setValue("Василий Петров");
        $("[name='phone']").setValue("+79О67778899");
        $(".checkbox__box").click();
        $(".button__content").click();
        String text = $(".input_invalid[data-test-id='phone']").$(".input__sub").getText();
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", text.trim());
    }

    /* ЗАДАНИЕ №2 */

    @Test
    void shouldChooseCityByDropDownList() {
        $("[data-test-id='city'] .input__control").setValue("Мо");
        $(".menu").shouldBe(Condition.visible).$(withText("Москва")).click();
        inputDate(dateForm);
        String selectedDate = $("[data-test-id='date'] .input__control").getAttribute("value");
        $("[name='name']").setValue("Василий Петров");
        $("[name='phone']").setValue("+79067778899");
        $(".checkbox__box").click();
        $(".button__content").click();
        String text = $(withText("Встреча успешно")).waitUntil(Condition.visible, 15000 ).getText();
        assertEquals("Встреча успешно запланирована на " + selectedDate, text.trim());
    }

    /* В тесте реализован универсальный алгоритм поиска даты доставки через форму с календарем
     *  Он умеет переключать месяц пока не найдёт нужную дату */
    @Test
    void shouldChooseDateByCalendar() {
        $("[data-test-id='city'] .input__control").setValue("Москва");
        $(".icon_name_calendar").click();
        String nextAvailableDayDataDayStringValue = $(".calendar__day[data-day]").getAttribute("data-day");
        long nextAvailableDayDataDayLongValue = Long.parseLong(nextAvailableDayDataDayStringValue);
        boolean isFound = false;
        do {
            ElementsCollection days = $$(".calendar__day[data-day]");
            for (SelenideElement day : days) {
                String dataDayStringValue = day.getAttribute("data-day");
                long dataDayLongValue = Long.parseLong(dataDayStringValue);
                /* Здесь указывается Unix-time значение, полученное вычитанием Unix-time
                 *  значения ближайшей доступной даты из Unix-time значения нужной даты */
                if (dataDayLongValue - nextAvailableDayDataDayLongValue == 6048000000L) {
                    day.click();
                    isFound = true;
                }
            }
            if (!isFound) {
                $(".calendar__arrow_direction_right[data-step='1']").click();
            }
        } while (!isFound);
        String selectedDate = $("[data-test-id='date'] .input__control").getAttribute("value");
        $("[name='name']").setValue("Василий Петров");
        $("[name='phone']").setValue("+79067778899");
        $(".checkbox__box").click();
        $(".button__content").click();
        String text = $(withText("Встреча успешно")).waitUntil(Condition.visible, 15000 ).getText();
        assertEquals("Встреча успешно запланирована на " + selectedDate, text.trim());
    }
}