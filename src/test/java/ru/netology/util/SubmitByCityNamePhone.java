package ru.netology.util;

public class SubmitByNamePhoneCity {

    private final String name;
    private final String phone;
    private final String city;

    public SubmitByNamePhoneCity(String name, String phone, String city) {
        this.name = name;
        this.phone = phone;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getCity() {
        return city;
    }
}