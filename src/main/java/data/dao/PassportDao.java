package data.dao;

import data.entity.Passport;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PassportDao implements Dao<Passport> {
    static final String SQL_SELECT_BY_NUMBER = "SELECT * FROM passports WHERE number=?;";
    static final String SQL_SELECT_BY_ID = "SELECT * FROM passports WHERE id=?;";
    static final String SQL_INSERT = "INSERT INTO passports(number, name, surname, patronymic, sex, birthday) " +
                                    "VALUES(?, ?, ?, ?, ?, ?);";
    static final String SQL_UPDATE_BY_ID = "UPDATE passports SET " +
                                            "number = ?, name = ?, surname = ?, patronymic = ?, sex = ?, birthday = ? " +
                                            "WHERE id = ?;";
    static final String SQL_DELETE_BY_ID = "DELETE FROM passports WHERE id=?;";
    static final String SQL_SELECT_ALL = "SELECT * FROM passports;";


    private final DataSource dataSource;

    public PassportDao(DataSource dataSource) {
        Objects.requireNonNull(dataSource);
        this.dataSource = dataSource;
    }

    public Optional<Passport> findByNumber(String number) {
        Passport passport = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement =
                    connection.prepareStatement(SQL_SELECT_BY_NUMBER);
            statement.setString(1, number);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                passport = mapToPassport(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(passport);
    }

    @Override
    public Optional<Passport> find(long id) {
        Passport passport = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement =
                    connection.prepareStatement(SQL_SELECT_BY_ID);
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                passport = mapToPassport(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(passport);
    }

    @Override
    public void save(Passport passport) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_INSERT);
            statement.setString(1, passport.getNumber());
            statement.setString(2, passport.getName());
            statement.setString(3, passport.getSurname());
            statement.setString(4, passport.getPatronymic());
            statement.setString(5, passport.getSex());
            statement.setDate(6, passport.getBirthday());

            statement.execute();

            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(long id, Passport passport) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_BY_ID);
            statement.setString(1, passport.getNumber());
            statement.setString(2, passport.getName());
            statement.setString(3, passport.getSurname());
            statement.setString(4, passport.getPatronymic());
            statement.setString(5, passport.getSex());
            statement.setDate(6, passport.getBirthday());
            statement.setLong(7, id);

            statement.execute();

            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(long id) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_DELETE_BY_ID);
            statement.setLong(1, id);

            statement.execute();
            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Passport> getAll() {
        try (Connection connection = dataSource.getConnection()) {
            List<Passport> passports = new ArrayList<>();
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL);
            while (resultSet.next()) {
                passports.add(mapToPassport(resultSet));
            }

            return passports;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    static Passport mapToPassport(ResultSet resultSet) throws SQLException {
        Passport passport = new Passport();
        passport.setId(resultSet.getLong("id"));
        passport.setNumber(resultSet.getString("number"));
        passport.setName(resultSet.getString("name"));
        passport.setSurname(resultSet.getString("surname"));
        passport.setPatronymic(resultSet.getString("patronymic"));
        passport.setSex(resultSet.getString("sex"));
        passport.setBirthday(resultSet.getDate("birthday"));
        return passport;
    }
}
