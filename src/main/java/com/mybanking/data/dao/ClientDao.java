package com.mybanking.data.dao;

import com.mybanking.data.entity.Client;
import com.mybanking.data.entity.Passport;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;


public class ClientDao implements Dao<Client> {
    static String SQL_SELECT_BY_ID = "SELECT * FROM clients c " +
            "JOIN passports p ON (c.passport_id = p.id) " +
            "WHERE p.number = ?;";

    static String SQL_SELECT_BY_PASSPORT_NUMBER = "SELECT * FROM clients c " +
            "JOIN passports p ON(c.passport_id = p.id) " +
            "WHERE c.id = ?;";

    static String SQL_INSERT = "INSERT INTO clients(passport_id, phone) " +
            "VALUES ((SELECT id FROM passports WHERE number = ?), ?);";

    static String SQL_UPDATE_BY_ID = "UPDATE clients SET phone = ? WHERE id=?;";

    static String SQL_DELETE = "DELETE FROM clients WHERE id = ?;";
    
    static String SQL_SELECT_ALL = "SELECT * FROM clients c " +
            "JOIN passports p ON(c.passport_id = p.id);";

    private final DataSource dataSource;

    public ClientDao(DataSource dataSource) {
        Objects.requireNonNull(dataSource);
        this.dataSource = dataSource;
    }

    public Optional<Client> findByPassport(Passport passport) {
        try (Connection connection = dataSource.getConnection()) {
            Client client = null;

            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID);
            statement.setString(1, passport.getNumber());

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
    public Optional<Client> find(long id) {
        try (Connection connection = dataSource.getConnection()) {
            Client client = null;

            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_PASSPORT_NUMBER);
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
    public void save(Client client) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(PassportDao.SQL_INSERT);
            Passport clientPassport = client.getPassport();
            statement.setString(1, clientPassport.getNumber());
            statement.setString(2, clientPassport.getName());
            statement.setString(3, clientPassport.getSurname());
            statement.setString(4, clientPassport.getPatronymic());
            statement.setString(5, clientPassport.getSex());
            statement.setDate(6, clientPassport.getBirthday());

            statement.execute();

            statement = connection.prepareStatement(SQL_INSERT);
            statement.setString(1, clientPassport.getNumber());
            statement.setString(2, client.getPhone());

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
    public void update(long id, Client client) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_BY_ID);
            statement.setString(1, client.getPhone());
            statement.setLong(2, id);

            statement.execute();

            PassportDao dao = new PassportDao(dataSource);
            Passport clientPassport = client.getPassport();
            Passport dbPassport = dao.findByNumber(clientPassport.getNumber()).get();

            statement = connection.prepareStatement(PassportDao.SQL_UPDATE_BY_ID);
            statement.setString(1, clientPassport.getNumber());
            statement.setString(2, clientPassport.getName());
            statement.setString(3, clientPassport.getSurname());
            statement.setString(4, clientPassport.getPatronymic());
            statement.setString(5, clientPassport.getSex());
            statement.setDate(6, clientPassport.getBirthday());
            statement.setLong(7, dbPassport.getId());

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
        Optional<Client> dbClient = find(id);

        if (dbClient.isEmpty()) {
            return;
        }

        Client client = dbClient.get();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_DELETE);
            statement.setLong(1, id);
            statement.execute();

            statement = connection.prepareStatement(PassportDao.SQL_DELETE_BY_ID);
            statement.setLong(1, client.getPassport().getId());
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
    public List<Client> getAll() {
        try (Connection connection = dataSource.getConnection()) {
            List<Client> clients = new ArrayList<>();

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL);
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
        Passport passport = PassportDao.mapToPassport(resultSet);
        passport.setId(resultSet.getLong("passport_id"));
        client.setPassport(passport);
        client.setPhone(resultSet.getString("phone"));
        return client;
    }
}
