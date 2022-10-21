package com.mybanking.data.dao;

import com.mybanking.data.DataSourceHolder;
import com.mybanking.data.entity.Client;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ClientSqlDaoTest {
    static Client createClient() {
        Client client = new Client();
        client.setPhone("388800553535");
        client.setPassport(PassportSqlDaoTest.createPassport());
        return client;
    }

    @Test
    void find() {
        ClientSqlDao dao = new ClientSqlDao(DataSourceHolder.getDataSource());
        Client client = createClient();

        dao.save(client);
        Client expected = dao.findByPassport(client.getPassport()).get();
        Assertions.assertEquals(expected, client);

        dao.delete(expected.getId());
        Assertions.assertTrue(dao.find(expected.getId()).isEmpty());
    }


    @Test
    void chainOperations() {
        ClientSqlDao dao = new ClientSqlDao(DataSourceHolder.getDataSource());
        Client client = createClient();

        dao.save(client);
        Client expected = dao.findByPassport(client.getPassport()).get();
        Assertions.assertEquals(expected, client);

        client.setId(expected.getId());
        client.setPhone("012345678910");
        client.getPassport().setName("Dudya");
        client.getPassport().setSurname("BigTestov");
        client.getPassport().setPatronymic("Bigovoch");

        dao.update(client);

        expected = dao.find(client.getId()).get();
        Assertions.assertEquals(expected, client);

        dao.delete(client.getId());
        Assertions.assertTrue(dao.find(expected.getId()).isEmpty());
    }

    @Test
    void getAll() {
        ClientSqlDao dao = new ClientSqlDao(DataSourceHolder.getDataSource());
        Client client = createClient();

        dao.save(client);

        List<Client> clients = dao.getAll();

        Assertions.assertTrue(clients.contains(client));
        dao.delete(dao.findByPassport(client.getPassport()).get().getId());
    }
}