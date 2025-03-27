package com.lb.application;

import com.zaxxer.hikari.HikariDataSource;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.sql.*;

public class SQLTools {

    private final HikariDataSource dataSource;

    public SQLTools(HikariDataSource dataSource){
        this.dataSource = dataSource;
    }

    @Tool("Queries to the database `sales` with many different tables")
    String executeQuery(@P("The query to the database") String sql) {
        String finalSql =
                String.format("SELECT row_to_json(t) as result FROM (%s) t;", sql);
        System.out.println("##########2");
        System.out.println(finalSql);
        System.out.println("##########2");
       try (Connection connection = dataSource.getConnection()) {
          PreparedStatement statement = connection.prepareStatement(finalSql);
          ResultSet resultSet = statement.executeQuery();
          StringBuilder response = new StringBuilder();
          while (resultSet.next()) {
              response.append(resultSet.getString("result"));
          }
           System.out.println("##########33");
           System.out.println(response);
           System.out.println("##########33");
          return response.toString();
       } catch (SQLException ex){
           return ex.getMessage();
       }
    }
}

