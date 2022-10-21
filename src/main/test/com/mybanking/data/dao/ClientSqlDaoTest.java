package com.mybanking.data.dao;

import com.mybanking.data.DataSourceHolder;
import com.mybanking.data.entity.Client;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class ClientSqlDaoTest {
    private static ClientSqlDao dao = new ClientSqlDao(DataSourceHolder.getDataSource());

    static Client createClient() {
        return new Client().
                setPhone("388800553535").
                setPassport(PassportSqlDaoTest.createPassport());
    }

    @DisplayName("All CRUD chain operations")
    @Test
    void chainOperations() {
        Client client = createClient();

        dao.save(client);
        Client expected = dao.findByPassport(client.getPassport()).get();
        Assertions.assertEquals(expected, client);

        client.
            setId(expected.getId()).
            setPhone("012345678910");
        client.getPassport().
            setName("Dudya").
            setSurname("BigTestov").
            setPatronymic("Bigovoch");

        dao.update(client);

        expected = dao.find(client.getId()).get();
        Assertions.assertEquals(expected, client);

        dao.delete(client.getId());
        Assertions.assertTrue(dao.find(expected.getId()).isEmpty());
    }

    @Test
    void getAll() {
        Client client = createClient();

        dao.save(client);

        List<Client> clients = dao.getAll();

        Assertions.assertTrue(clients.contains(client));
        dao.delete(dao.findByPassport(client.getPassport()).get().getId());
    }
}