# agentic-conversational

This agent has not only memory but access to a local DB. 
You can ask in natural language to do queries to this DB.

## Start the database
```shell
docker compose up -d
```

Then run the init dll
```shell
docker exec -i postgres-container psql -U postgres -d sales_db  < db.ddl 
```

## Start the application

Add these env vars
```shell
export DB_URL=localhost
export DB_NAME=sales_db
export DB_USER=postgres
export DB_PASSWORD=mysecretpassword
export ANTHROPIC_API_KEY=[your-anthropic-key]
```
Start the app
```shell
mvn clean compile exec:java 
```
Now you can ask the agent, how many products are in the DB: 

```shell
curl -XPOST localhost:9000/ask/123 -d '{"ask": "how many products is there in the db?"}' -H "Content-type: application/json" -i
```
where `123` is the id of the conversation that will hold the memory. 