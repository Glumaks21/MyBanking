package data.dao;

import data.entity.Client;
import data.entity.Passport;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;


public class ClientDao implements Dao<Client> {
    private final DataSource dataSource;

    public ClientDao(DataSource dataSource) {
        Objects.requireNonNull(dataSource);
        this.dataSource = dataSource;
    }
    @Override
    public Optional<Client> find(long id) {
        try (Connection connection = dataSource.getConnection()) {
            Client client = null;

            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM clients c " +
                        "JOIN passports p ON(c.passport_id = p.id) +" +
                        "WHERE c.id = ?");
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                client = mapToClient(resultSet);
            }

            return Optional.ofNullable(client);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Client entity) {

    }

    @Override
    public void update(long id, String[] args) {

    }

    @Override
    public void delete(long id) {

    }

    @Override
    public List<Client> getAll() {
        try (Connection connection = dataSource.getConnection()) {
            List<Client> clients = new ArrayList<>();

            ResultSet resultSet = connection.createStatement().
                    executeQuery("SELECT * FROM clients c " +
                            "JOIN passports p ON(c.passport_id = p.id)");
            while (resultSet.next()) {
                clients.add(mapToClient(resultSet));
            }

            return clients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Client mapToClient(ResultSet resultSet) throws SQLException {
        Client client = new Client();
        client.setId(resultSet.getLong("id"));
        client.setPassport(mapClientsPassport(resultSet));
        client.setPhone(resultSet.getString("phone"));
        return client;
    }

    private Passport mapClientsPassport(ResultSet resultSet) throws SQLException {
        Passport passport = new Passport();
        passport.setId(resultSet.getLong("passport_id"));
        passport.setName(resultSet.getString("name"));
        passport.setSurname(resultSet.getString("surname"));
        passport.setPatronymic(resultSet.getString("patronymic"));
        passport.setSex(resultSet.getString("sex"));
        passport.setBirthday(resultSet.getDate("birthday"));
        return passport;
    }
}
