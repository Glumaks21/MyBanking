package com.mybanking.data;

import com.mybanking.data.DataSourceHolder;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceHolderTest {

    @DisplayName("Test connection")
    @Test
    public void testGetDataSource() throws SQLException {
        DataSource dataSource = DataSourceHolder.getDataSource();
        Connection connection = dataSource.getConnection();

        Assertions.assertNotNull(connection);
        Assertions.assertTrue(connection.isValid(1));
    }
}