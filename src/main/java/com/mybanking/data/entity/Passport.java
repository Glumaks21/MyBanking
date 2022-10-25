package com.mybanking.data.entity;

import java.sql.Date;
import java.util.Objects;

public class Passport {
    private long id;
    private String number;
    private String name;
    private String surname;
    private String patronymic;
    private String sex;
    private Date birthday;

    public long getId() {
        return id;
    }

    public Passport setId(long id) {
        this.id = id;
        return this;
    }

    public String getNumber() {
        return number;
    }

    public Passport setNumber(String number) {
        this.number = number;
        return this;
    }

    public String getName() {
        return name;
    }

    public Passport setName(String name) {
        this.name = name;
        return this;
    }

    public String getSurname() {
        return surname;
    }

    public Passport setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public Passport setPatronymic(String patronymic) {
        this.patronymic = surname;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public Passport setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public Date getBirthday() {
        return birthday;
    }

    public Passport setBirthday(Date birthday) {
        this.birthday = birthday;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passport passport = (Passport) o;
        return Objects.equals(number, passport.number) &&
            Objects.equals(name, passport.name) &&
            Objects.equals(surname, passport.surname) &&
            Objects.equals(patronymic, passport.patronymic) &&
            Objects.equals(sex, passport.sex) &&
            Objects.equals(birthday.toString(), passport.birthday.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, name, surname, patronymic, sex, birthday);
    }

    @Override
    public String toString() {
        return "Passport{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
