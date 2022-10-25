package com.mybanking.data.dao.app;

import com.mybanking.data.dao.AbstractSqlDao;
import com.mybanking.data.entity.app.PasswordHash;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PasswordHashesSqlDao extends AbstractSqlDao<PasswordHash> {
    static String SQL_SELECT_BY_ACCOUNT_ID = "SELECT * FROM password_hashes WHERE account_id = ?;";

    static String SQL_INSERT = "INSERT INTO password_hashes(account_id, hash) " +
            "VALUES(?, ?);";

    static String SQL_DELETE_BY_ACCOUNT_ID = "DELETE FROM password_hashes " +
            "WHERE account_id = ?;";

    static String SQL_UPDATE_BY_ACCOUNT_ID = "UPDATE password_hashes SET hash = ? " +
            "WHERE account_id = ?;";

    static String SQL_SELECT_ALL = "SELECT * FROM password_hashes;";

    public PasswordHashesSqlDao(DataSource dataSource) {
        super(dataSource);
    }

    static PasswordHash mapToAppAccountPasswordHash(ResultSet resultSet) throws SQLException {
        return new PasswordHash().
                setAccountId(resultSet.getLong("account_id")).
                setHash(resultSet.getString("hash"));
    }

    @Override
    public Optional<PasswordHash> find(long id) {
        try (Connection connection = getDataSource().getConnection()) {
            PasswordHash passwordHash = null;

            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ACCOUNT_ID);
            fillStatement(statement, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                passwordHash = mapToAppAccountPasswordHash(resultSet);
            }

            return Optional.ofNullable(passwordHash);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(PasswordHash passwordHash) {
        Connection connection = null;
        try {
            connection = getDataSource().getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_INSERT);
            fillStatement(statement,
                    passwordHash.getAccountId(),
                    passwordHash.getHash());
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
    public void update(PasswordHash passwordHash) {
        Connection connection = null;
        try {
            connection = getDataSource().getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_BY_ACCOUNT_ID);
            fillStatement(statement,
                    passwordHash.getAccountId(),
                    passwordHash.getHash());
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

            PreparedStatement statement = connection.prepareStatement(SQL_DELETE_BY_ACCOUNT_ID);
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
    public List<PasswordHash> getAll() {
        try (Connection connection = getDataSource().getConnection()) {
            List<PasswordHash> passwordHashes = new ArrayList<>();
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL);
            while (resultSet.next()) {
                passwordHashes.add(mapToAppAccountPasswordHash(resultSet));
            }

            return passwordHashes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
