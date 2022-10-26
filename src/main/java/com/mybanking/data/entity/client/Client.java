package com.mybanking.data.entity.client;

import java.util.Objects;

public class Client {
    private long id;
    private Passport passport;
    private String phone;

    public long getId() {
        return id;
    }

    public Client setId(long id) {
        this.id = id;
        return this;
    }

    public Passport getPassport() {
        return passport;
    }

    public Client setPassport(Passport passport) {
        this.passport = passport;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Client setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(passport, client.passport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passport);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", passport=" + passport +
                ", phone='" + phone + '\'' +
                '}';
    }
}
