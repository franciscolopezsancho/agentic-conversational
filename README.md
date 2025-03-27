# agentic-conversational

```shell
docker compose up -d
```

```shell
export DB_URL=localhost
export DB_NAME=sales_db
export DB_USER=postgres
export DB_PASSWORD=mysecretpassword
```



You can ask anthropic LLM: 

```shell
curl -XPOST localhost:9000/ask/123 -d '{"ask": "Whats the history of this chat"}' -H "Content-type: application/json"
```

Where 123 is the id of the conversation.

We'll use a RAG to enhance the knowledge of the LLM