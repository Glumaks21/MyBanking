package data.dao;

import data.DataSourceHolder;
import data.entity.Client;
import data.entity.Passport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientDaoTest {
    static Client createClient() {
        Client client = new Client();
        client.setPhone("8800553535");
        client.setPassport(PassportDaoTest.createEntity());
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

    }
}