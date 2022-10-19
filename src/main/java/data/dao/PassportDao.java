package data.dao;


import data.DataSourceHolder;
import data.entity.Passport;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PassportDao implements Dao<Passport> {
    private final DataSource dataSource;

    public PassportDao(DataSource dataSource) {
        Objects.requireNonNull(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Passport> find(long id) {
        Passport passport = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM passports WHERE id=?");
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
    public void save(Passport entity) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO passports(number, name, surname, patronymic, sex, birthday) " +
                        "VALUES(?, ?, ?, ?, ?, ?)");
            statement.setString(1, entity.getNumber());
            statement.setString(2, entity.getName());
            statement.setString(3, entity.getSurname());
            statement.setString(4, entity.getPatronymic());
            statement.setString(5, entity.getSex());
            statement.setDate(6, entity.getBirthday());

            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(long id, String[] args) {
        
    }

    @Override
    public void delete(long id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM passports WHERE id=?");
            statement.setLong(1, id);

            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Passport> getAll() {
        try (Connection connection = dataSource.getConnection()) {
            List<Passport> passports = new ArrayList<>();

            ResultSet resultSet = connection.createStatement().
                    executeQuery("SELECT * FROM passports");
            while (resultSet.next()) {
                passports.add(mapToPassport(resultSet));
            }

            return passports;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Passport mapToPassport(ResultSet resultSet) throws SQLException {
        Passport passport = new Passport();
        passport.setId(resultSet.getLong("id"));
        passport.setNumber(resultSet.getString("number"));
        passport.setName(resultSet.getString("name"));
        passport.setSurname(resultSet.getString("surname"));
        passport.setSurname(resultSet.getString("patronymic"));
        passport.setSex(resultSet.getString("sex"));
        passport.setBirthday(resultSet.getDate("birthday"));
        return passport;
    }
}
