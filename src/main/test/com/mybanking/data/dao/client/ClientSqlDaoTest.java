package com.mybanking.data.dao.client;

import com.mybanking.data.DataSourceHolder;
import com.mybanking.data.entity.client.Client;
import com.mybanking.data.entity.client.Passport;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientSqlDaoTest {
    private static PassportSqlDao passportDao;
    private static ClientSqlDao dao;
    private static Client client;

    public static Client createClient() {
        return new Client().
                setPhone("388800553535").
                setPassport(PassportSqlDaoTest.createPassport());
    }

    @BeforeAll
    static void init() {
        passportDao = new PassportSqlDao(DataSourceHolder.getDataSource());
        dao = new ClientSqlDao(DataSourceHolder.getDataSource());
        client = createClient();
    }

    @Order(1)
    @Test
    public void save() {
        passportDao.save(client.getPassport());
        Passport passportInDb = passportDao.findByNumber(client.getPassport().getNumber()).get();
        client.setPassport(passportInDb);
        dao.save(client);
    }

    @Order(2)
    @Test
    public void findByPassport() {
        Client expected = dao.findByPassport(client.getPassport()).get();
        assertEquals(expected, client);
    }

    @Order(3)
    @Test
    public void findByPhone() {
        Client expected = dao.findByPhone(client.getPhone()).get();
        assertEquals(expected, client);
        client.setId(expected.getId());
    }

    @Order(4)
    @Test
    public void find() {
        Client expected = dao.find(client.getId()).get();
        assertEquals(expected, client);
    }

    @Order(5)
    @Test
    public void update() {
        client.
                setPhone("012345678910");
        client.getPassport().
                setName("Dudya").
                setSurname("BigTestov").
                setPatronymic("Bigovoch");

        passportDao.update(client.getPassport());
        dao.update(client);
    }
    @Order(6)
    @Test
    public void delete() {
        dao.delete(client.getId());
        passportDao.delete(client.getPassport().getId());
        assertTrue(dao.find(client.getId()).isEmpty());
    }

//    @Order(7)
//    @Test
//    public void getAll() {
//        dao.save(client);
//
//        List<Client> clients = dao.getAll();
//
//        assertTrue(clients.contains(client));
//        dao.findByPhone(client.getPhone()).
//                ifPresent(inDb -> dao.delete(inDb.getId()));
//    }
}