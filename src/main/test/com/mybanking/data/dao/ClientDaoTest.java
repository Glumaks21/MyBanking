package com.mybanking.data.dao;

import com.mybanking.data.DataSourceHolder;
import com.mybanking.data.entity.Client;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ClientDaoTest {
    static Client createClient() {
        Client client = new Client();
        client.setPhone("8800553535");
        client.setPassport(PassportDaoTest.createPassport());
        return client;
    }

    @Test
    void find() {
        ClientDao dao = new ClientDao(DataSourceHolder.getDataSource());
        Client client = createClient();

        dao.save(client);
        Client expected = dao.findByPassport(client.getPassport()).get();
        Assertions.assertEquals(expected, client);

        dao.delete(expected.getId());
        Assertions.assertTrue(dao.find(expected.getId()).isEmpty());
    }


    @Test
    void chainOperations() {
        ClientDao dao = new ClientDao(DataSourceHolder.getDataSource());
        Client client = createClient();

        dao.save(client);
        Client expected = dao.findByPassport(client.getPassport()).get();
        Assertions.assertEquals(expected, client);

        client.setPhone("0123456789");
        client.getPassport().setName("Dudya");
        client.getPassport().setSurname("BigTestov");
        client.getPassport().setPatronymic("Bigovoch");

        dao.update(expected.getId(), client);

        expected = dao.find(expected.getId()).get();
        Assertions.assertEquals(expected, client);

        dao.delete(expected.getId());
        Assertions.assertTrue(dao.find(expected.getId()).isEmpty());
    }

    @Test
    void getAll() {
        ClientDao dao = new ClientDao(DataSourceHolder.getDataSource());
        Client client = createClient();

        dao.save(client);

        List<Client> clients = dao.getAll();

        Assertions.assertTrue(clients.contains(client));
        dao.delete(dao.findByPassport(client.getPassport()).get().getId());
    }
}