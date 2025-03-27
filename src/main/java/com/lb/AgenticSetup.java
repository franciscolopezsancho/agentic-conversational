package com.lb;


import akka.javasdk.DependencyProvider;
import akka.javasdk.ServiceSetup;
import akka.javasdk.annotations.Setup;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Setup
public class AgenticSetup implements ServiceSetup {

    @Override
    public DependencyProvider createDependencyProvider() {

        String url = System.getenv("DB_URL");
        String database = System.getenv("DB_NAME");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASSWORD");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:postgresql://%s:5432/%s", url, database));
        config.setUsername(user);
        config.setPassword(pass);

        //TODO add close
        var datasource = new HikariDataSource(config);
            return new DependencyProvider() {
                @Override
                public <T> T getDependency(Class<T> clazz) {
                    if (clazz == HikariDataSource.class) {
                        return (T) datasource;
                    } else {
                        throw new RuntimeException("No such dependency found: " + clazz);
                    }
                }
            };

    }

}
