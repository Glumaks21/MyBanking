package com.mybanking.data.dao;

import com.mybanking.data.dao.client.PassportSqlDao;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractSqlDao<T> implements Dao<T> {
    private static final Logger LOGGER = Logger.getLogger(PassportSqlDao.class.getName());
    private final DataSource dataSource;

    public AbstractSqlDao(DataSource dataSource) {
        Objects.requireNonNull(dataSource);
        this.dataSource = dataSource;
    }

    public abstract Optional<T> find(long id);
    public abstract void save(T entity);
    public abstract void update(T entity);
    public abstract void delete(long id);
    public abstract List<T> getAll();

    public DataSource getDataSource() {
        return dataSource;
    }

    protected void fillStatement(PreparedStatement statement, Object... values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            statement.setObject(i + 1, values[i]);
        }
    }

    protected void tryToRollBack(Connection connection) {
        getLogger().info("Try to rollback");
        try {
            if (connection != null && connection.isValid(1)) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            getLogger().error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected Logger getLogger() {
        return LOGGER;
    }
}
