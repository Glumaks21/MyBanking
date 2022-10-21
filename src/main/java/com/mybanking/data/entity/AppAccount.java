package com.mybanking.data.entity;

import java.util.Objects;

public class AppAccount {
    private long id;
    private Client client;
    private String email;

    public long getId() {
        return id;
    }

    public AppAccount setId(long id) {
        if (id > 0) {
            this.id = id;
        }
        return this;
    }

    public Client getClient() {
        return client;
    }

    public AppAccount setClient(Client client) {
        Objects.requireNonNull(client);
        this.client = client;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public AppAccount setEmail(String email) {
        //add pattern
        this.email = email;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppAccount account = (AppAccount) o;
        return Objects.equals(client, account.client) && Objects.equals(email, account.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, email);
    }

    @Override
    public String toString() {
        return "AppAccount{" +
                "id=" + id +
                ", client=" + client +
                ", email='" + email + '\'' +
                '}';
    }
}
