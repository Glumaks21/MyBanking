package com.mybanking.data.dao.client;

import com.mybanking.data.dao.AbstractSqlDao;
import com.mybanking.data.dao.app.AccountSqlDao;
import com.mybanking.data.entity.client.Passport;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PassportSqlDao extends AbstractSqlDao<Passport> {
    static final String SQL_SELECT_BY_ID = "SELECT * FROM passports WHERE id = ?;";

    static final String SQL_SELECT_BY_NUMBER = "SELECT * FROM passports WHERE number = ?;";

    static final String SQL_INSERT = "INSERT INTO passports(number, name, surname, patronymic, sex, birthday) " +
            "VALUES(?, ?, ?, ?, ?, ?);";

    static final String SQL_UPDATE_BY_ID = "UPDATE passports SET " +
            "number = ?, name = ?, surname = ?, patronymic = ?, sex = ?, birthday = ? " +
            "WHERE id = ?;";

    static final String SQL_DELETE_BY_ID = "DELETE FROM passports WHERE id = ?;";

    static final String SQL_SELECT_ALL = "SELECT * FROM passports;";

    public PassportSqlDao(DataSource dataSource) {
        super(dataSource);
    }

    public Optional<Passport> findByNumber(String number) {
        Passport passport = null;
        getLogger().info("Try to find by number");
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_NUMBER)) {
            statement.setString(1, number);

            getLogger().info("Execute query: " + statement.toString().substring(statement.toString().indexOf(" ") + 1));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                passport = mapToPassport(resultSet);
            }
        } catch (SQLException e) {
            getLogger().error(e.getMessage());
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(passport);
    }

    @Override
    public Optional<Passport> find(long id) {
        Passport passport = null;
        getLogger().info("Try to find by id");
        try (Connection connection = getDataSource().getConnection()) {
            PreparedStatement statement =
                    connection.prepareStatement(SQL_SELECT_BY_ID);
            fillStatement(statement, id);

            getLogger().info("Execute query: " + statement.toString().substring(statement.toString().indexOf(" ") + 1));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                passport = mapToPassport(resultSet);
            }
        } catch (SQLException e) {
            getLogger().error(e.getMessage());
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(passport);
    }

    @Override
    public void save(Passport passport) {
        Connection connection = null;
        getLogger().info("Try to save passport");
        try {
            connection = getDataSource().getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_INSERT);
            fillStatement(statement,
                    passport.getNumber(),
                    passport.getName(),
                    passport.getSurname(),
                    passport.getPatronymic(),
                    passport.getSex(),
                    passport.getBirthday());

            getLogger().info("Execute query: " + statement.toString().substring(statement.toString().indexOf(" ") + 1));
            statement.execute();

            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            getLogger().error(e.getMessage());
            tryToRollBack(connection);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Passport passport) {
        Connection connection = null;
        getLogger().info("Try to update passport");
        try {
            connection = getDataSource().getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_BY_ID);
            fillStatement(statement,
                    passport.getNumber(),
                    passport.getName(),
                    passport.getSurname(),
                    passport.getPatronymic(),
                    passport.getSex(),
                    passport.getBirthday(),
                    passport.getId());
            getLogger().info("Execute query: " + statement.toString().substring(statement.toString().indexOf(" ") + 1));
            statement.execute();

            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            getLogger().error(e.getMessage());
            tryToRollBack(connection);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(long id) {
        Connection connection = null;
        getLogger().info("Try to delete passport");
        try {
            connection = getDataSource().getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_DELETE_BY_ID);
            fillStatement(statement, id);
            getLogger().info("Execute query: " + statement.toString().substring(statement.toString().indexOf(" ") + 1));
            statement.execute();

            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            getLogger().error(e.getMessage());
            tryToRollBack(connection);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Passport> getAll() {
        getLogger().info("Try get all passports");
        try (Connection connection = getDataSource().getConnection()) {
            List<Passport> passports = new ArrayList<>();
            Statement statement = connection.createStatement();

            getLogger().info("Execute query: " + SQL_SELECT_ALL);
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL);
            while (resultSet.next()) {
                passports.add(mapToPassport(resultSet));
            }

            return passports;
        } catch (SQLException e) {
            getLogger().error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Passport mapToPassport(ResultSet resultSet) throws SQLException {
        return new Passport().
                setId(resultSet.getLong("id")).
                setNumber(resultSet.getString("number")).
                setName(resultSet.getString("name")).
                setSurname(resultSet.getString("surname")).
                setPatronymic(resultSet.getString("patronymic")).
                setSex(resultSet.getString("sex")).
                setBirthday(resultSet.getDate("birthday"));
    }
}
