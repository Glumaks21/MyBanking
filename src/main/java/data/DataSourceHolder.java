package data;


import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DataSourceHolder {
    private static DataSource dataSource = null;
    static {
        try (FileInputStream fis = new FileInputStream("db.properties")) {
            Properties fileProperties = new Properties();
            fileProperties.load(fis);

            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setUrl(fileProperties.getProperty("url"));
            dataSource.setUsername(fileProperties.getProperty("user"));
            dataSource.setPassword(fileProperties.getProperty("password"));
            dataSource.setMinIdle(Integer.parseInt(fileProperties.getProperty("min_idle")));
            dataSource.setMaxIdle(Integer.parseInt(fileProperties.getProperty("max_idle")));
            DataSourceHolder.dataSource = dataSource;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
