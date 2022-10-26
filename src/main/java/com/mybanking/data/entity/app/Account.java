package com.mybanking.data.entity.app;

import com.mybanking.data.entity.client.Client;

import java.util.Objects;

public class Account {
    private long id;
    private Client client;
    private String email;

    public long getId() {
        return id;
    }

    public Account setId(long id) {
        if (id > 0) {
            this.id = id;
        }
        return this;
    }

    public Client getClient() {
        return client;
    }

    public Account setClient(Client client) {
        Objects.requireNonNull(client);
        this.client = client;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Account setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
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
