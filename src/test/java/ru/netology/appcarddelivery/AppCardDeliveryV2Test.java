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

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static org.openqa.selenium.Keys.chord;
import static ru.netology.util.DataGenerator.*;

public class AppCardDeliveryV2Test {

    SubmitByNamePhone submitData = generateByNamePhone();

    private final SelenideElement dateForm = $("[data-test-id='date'] .input__control");

    private void inputDate(SelenideElement item) {
        item.sendKeys(chord(Keys.CONTROL, "a") + Keys.DELETE);
        item.setValue(generateDate());
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
        SelenideElement successMsg = $(withText("Встреча успешно")).waitUntil(Condition.visible, 15000 );
        successMsg.shouldHave(exactText("Встреча успешно запланирована на " + selectedDate));
    }

    @Test
    void shouldNotSubmitIfEmptyCity() {
        inputDate(dateForm);
        $("[name='name']").setValue(submitData.getName());
        $("[name='phone']").setValue(submitData.getPhone());
        $(".checkbox__box").click();
        $(".button__content").click();
        SelenideElement errorMsg = $(".input_invalid[data-test-id='city']").$(".input__sub");
        errorMsg.shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldNotSubmitIfEmptyDate() throws IOException, ParseException {
        $("[data-test-id='city'] .input__control").setValue(generateCity());
        dateForm.sendKeys(chord(Keys.CONTROL, "a") + Keys.DELETE);
        $("[name='name']").setValue(submitData.getName());
        $("[name='phone']").setValue(submitData.getPhone());
        $(".checkbox__box").click();
        $(".button__content").click();
        SelenideElement errorMsg = $("[data-test-id='date']").$(".input_invalid").$(".input__sub");
        errorMsg.shouldHave(exactText("Неверно введена дата"));
    }

    @Test
    void shouldNotSubmitIfEmptyName() throws IOException, ParseException {
        $("[data-test-id='city'] .input__control").setValue(generateCity());
        inputDate(dateForm);
        $("[name='phone']").setValue(submitData.getPhone());
        $(".checkbox__box").click();
        $(".button__content").click();
        SelenideElement errorMsg = $(".input_invalid[data-test-id='name']").$(".input__sub");
        errorMsg.shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldNotSubmitIfEmptyPhone() throws IOException, ParseException {
        $("[data-test-id='city'] .input__control").setValue(generateCity());
        inputDate(dateForm);
        $("[name='name']").setValue(submitData.getName());
        $(".checkbox__box").click();
        $(".button__content").click();
        SelenideElement errorMsg = $(".input_invalid[data-test-id='phone']").$(".input__sub");
        errorMsg.shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldNotSubmitIfAgreementNotChecked() throws IOException, ParseException {
        $("[data-test-id='city'] .input__control").setValue(generateCity());
        inputDate(dateForm);
        $("[name='name']").setValue(submitData.getName());
        $("[name='phone']").setValue(submitData.getPhone());
        $(".button__content").click();
        SelenideElement errorMsg = $(".input_invalid[data-test-id='agreement']");
        errorMsg.shouldHave(exactText("Я соглашаюсь с условиями обработки и использования моих персональных данных"));
    }

    @Test
    void shouldNotSubmitIfInaccessibleCity() {
        $("[data-test-id='city'] .input__control").setValue("Колыма");
        inputDate(dateForm);
        $("[name='name']").setValue(submitData.getName());
        $("[name='phone']").setValue(submitData.getPhone());
        $(".checkbox__box").click();
        $(".button__content").click();
        SelenideElement errorMsg = $(".input_invalid[data-test-id='city']").$(".input__sub");
        errorMsg.shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldNotSubmitIfInaccessibleDate() throws IOException, ParseException {
        LocalDate date = LocalDate.now();
        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id='city'] .input__control").setValue(generateCity());
        dateForm.sendKeys(chord(Keys.CONTROL, "a") + Keys.DELETE);
        dateForm.setValue(formattedDate);
        $("[name='name']").setValue(submitData.getName());
        $("[name='phone']").setValue(submitData.getPhone());
        $(".checkbox__box").click();
        $(".button__content").click();
        SelenideElement errorMsg = $("[data-test-id='date']").$(".input_invalid").$(".input__sub");
        errorMsg.shouldHave(exactText("Заказ на выбранную дату невозможен"));
    }

    @Test
    void shouldNotSubmitIfPastDate() throws IOException, ParseException {
        String pastDate = "01.01.2020";
        $("[data-test-id='city'] .input__control").setValue(generateCity());
        dateForm.sendKeys(chord(Keys.CONTROL, "a") + Keys.DELETE);
        dateForm.setValue(pastDate);
        $("[name='name']").setValue(submitData.getName());
        $("[name='phone']").setValue(submitData.getPhone());
        $(".checkbox__box").click();
        $(".button__content").click();
        SelenideElement errorMsg = $("[data-test-id='date']").$(".input_invalid").$(".input__sub");
        errorMsg.shouldHave(exactText("Заказ на выбранную дату невозможен"));
    }

    @Test
    void shouldNotSubmitIfLatinName() throws IOException, ParseException {
        $("[data-test-id='city'] .input__control").setValue(generateCity());
        inputDate(dateForm);
        $("[name='name']").setValue("Vasiliy Petrov");
        $("[name='phone']").setValue(submitData.getPhone());
        $(".checkbox__box").click();
        $(".button__content").click();
        SelenideElement errorMsg = $(".input_invalid[data-test-id='name']").$(".input__sub");
        errorMsg.shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldNotSubmitIfNumeralsInName() throws IOException, ParseException {
        $("[data-test-id='city'] .input__control").setValue(generateCity());
        inputDate(dateForm);
        $("[name='name']").setValue("Василий Петр0в");
        $("[name='phone']").setValue(submitData.getPhone());
        $(".checkbox__box").click();
        $(".button__content").click();
        SelenideElement errorMsg = $(".input_invalid[data-test-id='name']").$(".input__sub");
        errorMsg.shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldNotSubmitIfIncompletePhone() throws IOException, ParseException {
        $("[data-test-id='city'] .input__control").setValue(generateCity());
        inputDate(dateForm);
        $("[name='name']").setValue(submitData.getName());
        $("[name='phone']").setValue("+");
        $(".checkbox__box").click();
        $(".button__content").click();
        SelenideElement errorMsg = $(".input_invalid[data-test-id='phone']").$(".input__sub");
        errorMsg.shouldHave(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldChooseCityByDropDownList() throws IOException, ParseException {
        String city = generateCity();
        String cityFirstLetters = city.substring(0,2);
        $("[data-test-id='city'] .input__control").setValue(cityFirstLetters);
        $(".menu").shouldBe(Condition.visible).$(withText(city)).click();
        inputDate(dateForm);
        String selectedDate = $("[data-test-id='date'] .input__control").getAttribute("value");
        $("[name='name']").setValue(submitData.getName());
        $("[name='phone']").setValue(submitData.getPhone());
        $(".checkbox__box").click();
        $(".button__content").click();
        SelenideElement successMsg = $(withText("Встреча успешно")).waitUntil(Condition.visible, 15000 );
        successMsg.shouldHave(exactText("Встреча успешно запланирована на " + selectedDate));
    }

    /* В тесте реализован универсальный алгоритм поиска даты доставки через форму с календарем
     *  Он умеет переключать месяц пока не найдёт нужную дату */
    @Test
    void shouldChooseDateByCalendar() throws IOException, ParseException {
        $("[data-test-id='city'] .input__control").setValue(generateCity());
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
        $("[name='name']").setValue(submitData.getName());
        $("[name='phone']").setValue(submitData.getPhone());
        $(".checkbox__box").click();
        $(".button__content").click();
        SelenideElement successMsg = $(withText("Встреча успешно")).waitUntil(Condition.visible, 15000 );
        successMsg.shouldHave(exactText("Встреча успешно запланирована на " + selectedDate));
    }

    @Test
    void shouldReschedule() throws IOException, ParseException {
        $("[data-test-id='city'] .input__control").setValue(generateCity());
        inputDate(dateForm);
        $("[name='name']").setValue(submitData.getName());
        $("[name='phone']").setValue(submitData.getPhone());
        $(".checkbox__box").click();
        $(".button__content").click();
        inputDate((dateForm));
        String selectedDate = $("[data-test-id='date'] .input__control").getAttribute("value");
        $(".button__content").click();
        $$(".button__content").last().click();
        SelenideElement successMsg = $(withText("Встреча успешно")).waitUntil(Condition.visible, 15000 );
        successMsg.shouldHave(exactText("Встреча успешно запланирована на " + selectedDate));
    }
}