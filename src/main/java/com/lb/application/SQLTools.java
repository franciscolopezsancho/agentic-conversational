package com.lb.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

public class SQLTools {

    private final Logger log = LoggerFactory.getLogger(SQLTools.class);
    private final HikariDataSource dataSource;

    public SQLTools(HikariDataSource dataSource){
        this.dataSource = dataSource;
    }

    @Tool("Queries to the database `sales` with many different tables")
    String executeQuery(@P("The query to the database") String sql) {
        String finalSql =
                String.format("SELECT row_to_json(t) as result FROM (%s) t;", sql);
       try (Connection connection = dataSource.getConnection()) {
          PreparedStatement statement = connection.prepareStatement(finalSql);
          ResultSet resultSet = statement.executeQuery();
          StringBuilder response = new StringBuilder();
          while (resultSet.next()) {
              response.append(resultSet.getString("result"));
          }
          return response.toString();
       } catch (SQLException ex){
           return ex.getMessage();
       }
    }


    public static String extractSanitizeSqlQuery(String jsonResponse) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        if (!rootNode.has("sql")) {
            throw new IllegalArgumentException("No 'sql' field found in JSON response");
        }
        return rootNode.get("sql").asText().replaceAll(";","");
    }

    public Optional<String> executeQuerySafely(ToolExecutionRequest toolExecutionRequest){
        try{
            String query = extractSanitizeSqlQuery(toolExecutionRequest.arguments());
            return Optional.of(executeQuery(query));
        } catch (JsonProcessingException ex){
            log.error(ex.getMessage());
            return Optional.empty();
        }
    }

}



