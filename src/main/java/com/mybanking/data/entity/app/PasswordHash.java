package com.mybanking.data.entity.app;

import java.util.Objects;

public class PasswordHash {
    private long accountId;
    private String hash;

    public long getAccountId() {
        return accountId;
    }

    public PasswordHash setAccountId(long accountId) {
        this.accountId = accountId;
        return this;
    }

    public String getHash() {
        return hash;
    }

    public PasswordHash setHash(String hash) {
        this.hash = hash;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordHash that = (PasswordHash) o;
        return Objects.equals(hash, that.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }

    @Override
    public String toString() {
        return "PasswordHashes{" +
                "id=" + accountId +
                ", hash='" + hash + '\'' +
                '}';
    }
}
