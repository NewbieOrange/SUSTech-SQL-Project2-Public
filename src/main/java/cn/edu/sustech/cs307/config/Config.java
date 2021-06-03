package cn.edu.sustech.cs307.config;

import cn.edu.sustech.cs307.factory.ServiceFactory;

import java.io.FileInputStream;
import java.util.Properties;

public final class Config {
    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(new FileInputStream("config.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ServiceFactory getServiceFactory() {
        try {
            return (ServiceFactory) Class.forName(properties.getProperty("serviceFactory"))
                    .getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getJdbcUrl() {
        return properties.getProperty("jdbcUrl");
    }

    public static String getSQLUsername() {
        return properties.getProperty("username");
    }

    public static String getSQLPassword() {
        return properties.getProperty("password");
    }
}
