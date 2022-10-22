package com.mybanking.data.dao;

import com.mybanking.data.entity.app.Account;
import com.mybanking.data.entity.Client;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppAccountSqlDao extends AbstractSqlDao<Account> {
    static String SQL_SELECT_BY_ID = "SELECT * FROM app_accounts ac " +
            "JOIN clients c ON(ac.client_id = c.id) " +
            "JOIN passports p ON(c.passport_id = p.id)" +
            "WHERE ac.id = ?;";

    static String SQL_SELECT_BY_PHONE = "SELECT * FROM app_accounts ac " +
            "JOIN clients c ON(ac.client_id = c.id) " +
            "JOIN passports p ON(c.passport_id = p.id)" +
            "WHERE c.phone = ?;";

    static String SQL_INSERT = "INSERT INTO app_accounts(client_id, email) " +
            "VALUES ((SELECT c.id FROM clients c " +
            "JOIN passports p ON (c.passport_id = p.id) " +
            "WHERE number = ?), ?);";

    static String SQL_UPDATE_BY_ID = "UPDATE app_accounts SET email = ? WHERE id = ?;";

    static String SQL_DELETE_BY_ID = "DELETE FROM app_accounts WHERE id = ?;";

    static String SQL_SELECT_ALL = "SELECT * FROM app_accounts ac " +
            "JOIN clients c ON(ac.client_id = c.id) " +
            "JOIN passports p ON(c.passport_id = p.id);";

    public AppAccountSqlDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<Account> find(long id) {
        try (Connection connection = getDataSource().getConnection()) {
            Account account = null;

            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID);
            fillStatement(statement, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                account = mapToAppAccount(resultSet);
            }

            return Optional.ofNullable(account);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Account> findByPhone(String phone) {
        try (Connection connection = getDataSource().getConnection()) {
            Account account = null;

            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_PHONE);
            fillStatement(statement, phone);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                account = mapToAppAccount(resultSet);
            }

            return Optional.ofNullable(account);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Account account) {
        Connection connection = null;
        try {
            connection = getDataSource().getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_INSERT);
            fillStatement(statement,
                    account.getClient().getPassport().getNumber(),
                    account.getEmail());
            statement.execute();

            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                    connection.setAutoCommit(false);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Account account) {
        Connection connection = null;
        try {
            connection = getDataSource().getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_BY_ID);
            fillStatement(statement,
                    account.getEmail(),
                    account.getId());
            statement.execute();

            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                    connection.setAutoCommit(false);
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
            connection = getDataSource().getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_DELETE_BY_ID);
            fillStatement(statement, id);

            statement.execute();

            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                    connection.setAutoCommit(false);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Account> getAll() {
        try (Connection connection = getDataSource().getConnection()) {
            List<Account> accounts = new ArrayList<>();
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL);
            while (resultSet.next()) {
                accounts.add(mapToAppAccount(resultSet));
            }

            return accounts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Account mapToAppAccount(ResultSet resultSet) throws SQLException {
        Client client = ClientSqlDao.mapToClient(resultSet).
                setId(resultSet.getLong("client_id"));
        return new Account().setId(resultSet.getLong("id")).
                setClient(client).
                setEmail(resultSet.getString("email"));
    }
}
