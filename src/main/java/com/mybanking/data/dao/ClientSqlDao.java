package com.mybanking.data.dao;

import com.mybanking.data.entity.Client;
import com.mybanking.data.entity.Passport;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;


public class ClientSqlDao extends AbstractSqlDao<Client> {
    static String SQL_SELECT_BY_ID = "SELECT * FROM clients c " +
            "JOIN passports p ON (c.passport_id = p.id) " +
            "WHERE p.number = ?;";

    static String SQL_SELECT_BY_PASSPORT_NUMBER = "SELECT * FROM clients c " +
            "JOIN passports p ON(c.passport_id = p.id) " +
            "WHERE c.id = ?;";
    
    static String SQL_SELECT_BY_PHONE = "SELECT * FROM clients c " +
            "JOIN passports p ON(c.passport_id = p.id) " +
            "WHERE c.phone = ?;";
    
    static String SQL_INSERT = "INSERT INTO clients(passport_id, phone) " +
            "VALUES ((SELECT id FROM passports WHERE number = ?), ?);";

    static String SQL_UPDATE_BY_ID = "UPDATE clients SET phone = ? WHERE id = ?;";

    static String SQL_SELECT_PASSPORT_ID_BY_ID = "SELECT passport_id FROM clients WHERE id = ?;";
    
    static String SQL_DELETE = "DELETE FROM clients WHERE id = ?;";
    
    static String SQL_SELECT_ALL = "SELECT * FROM clients c " +
            "JOIN passports p ON(c.passport_id = p.id);";

    public ClientSqlDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<Client> find(long id) {
        try (Connection connection = getDataSource().getConnection()) {
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

    public Optional<Client> findByPassport(Passport passport) {
        try (Connection connection = getDataSource().getConnection()) {
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

    public Optional<Client> findByPhone(String phone) {
        try (Connection connection = getDataSource().getConnection()) {
            Client client = null;

            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_PHONE);
            statement.setString(1, phone);

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
            connection = getDataSource().getConnection();
            connection.setAutoCommit(false);

            Passport clientPassport = client.getPassport();

            PreparedStatement statement = connection.prepareStatement(PassportSqlDao.SQL_INSERT);
            fillStatement(statement,
                    clientPassport.getNumber(),
                    clientPassport.getName(),
                    clientPassport.getSurname(),
                    clientPassport.getPatronymic(),
                    clientPassport.getSex(),
                    clientPassport.getBirthday());

            statement.execute();

            statement = connection.prepareStatement(SQL_INSERT);
            fillStatement(statement,
                    clientPassport.getNumber(),
                    client.getPhone());
            
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

    public void update(Client client) {
        Connection connection = null;
        try {
            connection = getDataSource().getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_BY_ID);
            fillStatement(statement,  client.getPhone(), client.getId());

            statement.execute();

            Passport clientPassport = client.getPassport();
            
            String sqlQuery = PassportSqlDao.SQL_UPDATE_BY_ID;
            sqlQuery = sqlQuery.replace("id = ?",
                    "id = " + createSubquery(SQL_SELECT_PASSPORT_ID_BY_ID));
            
            statement = connection.prepareStatement(sqlQuery);
            fillStatement(statement,
                    clientPassport.getNumber(),
                    clientPassport.getName(),
                    clientPassport.getSurname(),
                    clientPassport.getPatronymic(),
                    clientPassport.getSex(),
                    clientPassport.getBirthday(),
                    client.getId());

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
            connection = getDataSource().getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_DELETE);
            statement.setLong(1, id);
            statement.execute();

            statement = connection.prepareStatement(PassportSqlDao.SQL_DELETE_BY_ID);
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
        try (Connection connection = getDataSource().getConnection()) {
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

     static  Client mapToClient(ResultSet resultSet) throws SQLException {
        Passport passport = PassportSqlDao.mapToPassport(resultSet).
                setId(resultSet.getLong("passport_id"));
        return  new Client().
                setId(resultSet.getLong("id")).
                setPassport(passport).
                setPhone(resultSet.getString("phone"));
    }
}
