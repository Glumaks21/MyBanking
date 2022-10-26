package com.mybanking.data.dao.client;

import com.mybanking.data.DataSourceHolder;

import com.mybanking.data.entity.client.Passport;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PassportSqlDaoTest {
    private static Passport passport;
    private static PassportSqlDao dao;

    public static Passport createPassport() {
        Passport passport = new Passport();
        passport.setNumber("0123456789");
        passport.setName("Test");
        passport.setSurname("Testov");
        passport.setPatronymic("Testovckiy");
        passport.setSex("male");
        passport.setBirthday(new Date(System.currentTimeMillis()));
        return passport;
    }

    @BeforeAll
    static void init() {
        dao = new PassportSqlDao(DataSourceHolder.getDataSource());
        passport = createPassport();
    }

    @Order(1)
    @Test
    public void save() {
        dao.save(passport);
    }

    @Order(2)
    @Test
    public void find() {
        Passport expected = dao.findByNumber(passport.getNumber()).get();
        assertEquals(expected, passport);
        passport = expected;
    }

    @Order(3)
    @Test
    public void update() {
        passport.setName("Dudya");
        passport.setSurname("BigTestov");
        passport.setPatronymic("Bigovoch");
        dao.update(passport);
        Passport expected = dao.findByNumber(passport.getNumber()).get();
        assertEquals(expected, passport);
    }
    @Order(4)
    @Test
    public void delete() {
        dao.delete(passport.getId());
        assertTrue(dao.findByNumber(passport.getNumber()).isEmpty());
    }

    @Order(5)
    @Test
    public void getAll() {
        dao.save(passport);

        List<Passport> passports = dao.getAll();

        assertTrue(passports.contains(passport));
        dao.findByNumber(passport.getNumber()).
                ifPresent(inDb -> dao.delete(inDb.getId()));
    }
}