package com.mybanking.data.dao.client;

import com.mybanking.data.dao.AbstractSqlDao;
import com.mybanking.data.entity.client.Client;
import com.mybanking.data.entity.client.Passport;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;


public class ClientSqlDao extends AbstractSqlDao<Client> {
    static String SQL_SELECT_BY_ID = "SELECT * FROM clients c " +
            "JOIN passports p ON (c.passport_id = p.id) " +
            "WHERE c.id = ?;";

    static String SQL_SELECT_BY_PASSPORT_NUMBER = "SELECT * FROM clients c " +
            "JOIN passports p ON(c.passport_id = p.id) " +
            "WHERE p.number = ?;";
    
    static String SQL_SELECT_BY_PHONE = "SELECT * FROM clients c " +
            "JOIN passports p ON(c.passport_id = p.id) " +
            "WHERE c.phone = ?;";
    
    static String SQL_INSERT = "INSERT INTO clients(passport_id, phone) " +
            "VALUES (?, ?);";

    static String SQL_UPDATE_BY_ID = "UPDATE clients SET passport_id = ?, phone = ? " +
            "WHERE id = ?;";
    
    static String SQL_DELETE = "DELETE FROM clients " +
            "WHERE id = ?;";
    
    static String SQL_SELECT_ALL = "SELECT * FROM clients c " +
            "JOIN passports p ON(c.passport_id = p.id);";

    public ClientSqlDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<Client> find(long id) {
        Client client = null;
        try (Connection connection = getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            fillStatement(statement, id);

            getLogger().info("Execute query: " + statement.toString().substring(statement.toString().indexOf(" ") + 1));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                client = mapToClient(resultSet);
            }

            return Optional.ofNullable(client);
        } catch (SQLException e) {
            getLogger().error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Optional<Client> findByPassport(Passport passport) {
        Client client = null;
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_PASSPORT_NUMBER)) {
            fillStatement(statement, passport.getNumber());

            getLogger().info("Execute query: " + statement.toString().substring(statement.toString().indexOf(" ") + 1));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                client = mapToClient(resultSet);
            }

            return Optional.ofNullable(client);
        } catch (SQLException e) {
            getLogger().error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Optional<Client> findByPhone(String phone) {
        Client client = null;
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_PHONE)) {
            fillStatement(statement, phone);

            getLogger().info("Execute query: " + statement.toString().substring(statement.toString().indexOf(" ") + 1));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                client = mapToClient(resultSet);
            }

            return Optional.ofNullable(client);
        } catch (SQLException e) {
            getLogger().error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void save(Client client) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getDataSource().getConnection();
            connection.setAutoCommit(false);

            statement = connection.prepareStatement(SQL_INSERT);
            fillStatement(statement,
                    client.getPassport().getId(),
                    client.getPhone());

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

    public void update(Client client) {
        Connection connection = null;
        try {
            connection = getDataSource().getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_BY_ID);
            fillStatement(statement,
                    client.getPassport().getId(),
                    client.getPhone(),
                    client.getId());

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
        try {
            connection = getDataSource().getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_DELETE);
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
    public List<Client> getAll() {
        List<Client> clients = new ArrayList<>();
        try (Connection connection = getDataSource().getConnection();
             Statement statement = connection.createStatement()) {

            getLogger().info("Execute query: " + statement.toString().substring(statement.toString().indexOf(" ") + 1));
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL);
            while (resultSet.next()) {
                clients.add(mapToClient(resultSet));
            }

            return clients;
        } catch (SQLException e) {
            getLogger().error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

     public static  Client mapToClient(ResultSet resultSet) throws SQLException {
        Passport passport = PassportSqlDao.mapToPassport(resultSet).
                setId(resultSet.getLong("passport_id"));
        return  new Client().
                setId(resultSet.getLong("id")).
                setPassport(passport).
                setPhone(resultSet.getString("phone"));
    }
}
