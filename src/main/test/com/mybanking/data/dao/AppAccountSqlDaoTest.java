package com.mybanking.data.dao;

import com.mybanking.data.DataSourceHolder;
import com.mybanking.data.entity.AppAccount;
import com.mybanking.data.entity.Client;
import com.mybanking.data.entity.Passport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppAccountSqlDaoTest {
    private static AppAccountSqlDao dao = new AppAccountSqlDao(DataSourceHolder.getDataSource());

    static AppAccount createAppAccount() {
        return new AppAccount().
                setClient(ClientSqlDaoTest.createClient()).
                setEmail("biba@gmail.com");
    }

    @DisplayName("All CRUD chain operations")
    @Test
    void operationChain() {
        ClientSqlDao clientDao = new ClientSqlDao(DataSourceHolder.getDataSource());
        AppAccount account = createAppAccount();

        clientDao.save(account.getClient());
        dao.save(account);

        AppAccount expected = dao.findByClient(account.getClient()).get();
        Assertions.assertEquals(expected, account);
        account.setId(expected.getId());
        account.getClient().setId(expected.getClient().getId());

        account.setEmail("nebiba@gmail.com");

        dao.update(account);
        expected = dao.findByClient(account.getClient()).get();
        Assertions.assertEquals(expected, account);

        dao.delete(account.getId());
        clientDao.delete(account.getClient().getId());
        Assertions.assertTrue(dao.find(account.getId()).isEmpty());
    }

    @Test
    void getAll() {
    }
}