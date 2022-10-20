package com.mybanking.data.dao;

import com.mybanking.data.DataSourceHolder;
import com.mybanking.data.dao.PassportDao;
import com.mybanking.data.entity.Passport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.List;

class PassportDaoTest {
    static Passport createPassport() {
        Passport passport = new Passport();
        passport.setNumber("0123456789");
        passport.setName("Test");
        passport.setSurname("Testov");
        passport.setPatronymic("Testovckiy");
        passport.setSex("male");
        passport.setBirthday(new Date(System.currentTimeMillis()));
        return passport;
    }

    @Test
    void find() {
        PassportDao dao = new PassportDao(DataSourceHolder.getDataSource());
        Passport passport = createPassport();

        dao.save(passport);

        Passport expectedByNumber = dao.findByNumber(passport.getNumber()).get();
        Passport expectedById = dao.find(expectedByNumber.getId()).get();

        Assertions.assertEquals(passport, expectedByNumber);
        Assertions.assertEquals(passport, expectedById);
        Assertions.assertEquals(expectedByNumber, expectedById);

        dao.delete(expectedById.getId());
        Assertions.assertTrue(dao.find(expectedById.getId()).isEmpty());
    }

    @DisplayName("All CRUD chain operations")
    @Test
    void operationChain() {
        PassportDao dao = new PassportDao(DataSourceHolder.getDataSource());
        Passport passport = createPassport();

        dao.save(passport);

        Passport expected = dao.findByNumber(passport.getNumber()).get();
        Assertions.assertEquals(expected, passport);

        passport.setName("Dudya");
        passport.setSurname("BigTestov");
        passport.setPatronymic("Bigovoch");
        dao.update(expected.getId(), passport);

        expected = dao.findByNumber(passport.getNumber()).get();
        Assertions.assertEquals(expected, passport);

        dao.delete(expected.getId());
        Assertions.assertTrue(dao.findByNumber(passport.getNumber()).isEmpty());
    }

    @Test
    void getAll() {
        PassportDao dao = new PassportDao(DataSourceHolder.getDataSource());
        Passport passport = createPassport();

        dao.save(passport);

        List<Passport> passports = dao.getAll();

        Assertions.assertTrue(passports.contains(passport));
        dao.findByNumber(passport.getNumber()).
                ifPresent(inDb -> dao.delete(inDb.getId()));
    }
}