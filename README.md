# agentic-conversational


You can ask anthropic LLM: 

```shell
curl -XPOST localhost:9000/ask/123 -d '{"ask": "Whats the history of this chat"}' -H "Content-type: application/json"
```

Where 123 is the id of the conversation.