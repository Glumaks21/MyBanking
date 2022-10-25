package com.mybanking.data.dao;

import com.mybanking.data.DataSourceHolder;
import com.mybanking.data.dao.app.AppAccountSqlDao;
import com.mybanking.data.dao.client.ClientSqlDao;
import com.mybanking.data.dao.client.PassportSqlDao;
import com.mybanking.data.entity.Client;
import com.mybanking.data.entity.Passport;
import com.mybanking.data.entity.app.Account;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AppAccountSqlDaoTest {
    private static PassportSqlDao passportDao;
    private static ClientSqlDao clientDao;
    private static AppAccountSqlDao dao;
    private static Account account;

    @BeforeAll
    static void init() {
        passportDao = new PassportSqlDao(DataSourceHolder.getDataSource());
        clientDao = new ClientSqlDao(DataSourceHolder.getDataSource());
        dao = new AppAccountSqlDao(DataSourceHolder.getDataSource());
        account = createAccount();
    }

    public static Account createAccount() {
        return new Account().
                setClient(ClientSqlDaoTest.createClient()).
                setEmail("biba@gmail.com");
    }

    @Order(1)
    @Test
    public void save() {
        passportDao.save(account.getClient().getPassport());
        Passport passportInDb = passportDao.findByNumber(account.getClient().getPassport().getNumber()).get();
        account.getClient().setPassport(passportInDb);
        clientDao.save(account.getClient());
        Client clientInDb = clientDao.findByPassport(account.getClient().getPassport()).get();
        account.setClient(clientInDb);
        dao.save(account);
    }

    @Order(2)
    @Test
    public void findByPhone() {
        Account expected = dao.findByPhone(account.getClient().getPhone()).get();
        assertEquals(expected, account);
        account.setId(expected.getId());
    }


    @Order(3)
    @Test
    public void find() {
        Account expected = dao.find(account.getId()).get();
        assertEquals(expected, account);
    }

    @Order(4)
    @Test
    public void update() {
        account.setEmail("ne_biba@gmail.com");
        account.getClient().
                setPhone("012345678910");
        account.getClient().getPassport().
                setName("Dudya").
                setSurname("BigTestov").
                setPatronymic("Bigovoch");


        passportDao.update(account.getClient().getPassport());
        clientDao.update(account.getClient());
        dao.update(account);
    }
    @Order(5)
    @Test
    public void delete() {
        dao.delete(account.getId());
        clientDao.delete(account.getClient().getId());
        passportDao.delete(account.getClient().getPassport().getId());
        //assertTrue(dao.find(account.getId()).isEmpty());
    }
}