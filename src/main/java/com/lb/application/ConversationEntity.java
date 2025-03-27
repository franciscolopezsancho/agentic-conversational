package com.lb.application;

import akka.javasdk.annotations.ComponentId;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lb.domain.Conversation;
import com.lb.domain.ConversationEvent;
import com.zaxxer.hikari.HikariDataSource;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.langchain4j.model.anthropic.AnthropicChatModelName.CLAUDE_3_5_SONNET_20240620;

@ComponentId("conversation")
public class ConversationEntity extends EventSourcedEntity<Conversation, ConversationEvent> {

    HikariDataSource dataSource;
    Logger log = LoggerFactory.getLogger(ConversationEntity.class);
    AnthropicChatModel model;
    SQLTools sqlTools;


    @Override
    public Conversation emptyState(){
        return new Conversation(new ArrayList<>());
    }

    public ConversationEntity(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(CLAUDE_3_5_SONNET_20240620)
                .build();
        sqlTools = new SQLTools(dataSource);
    }



    public Effect<String> ask(String question){
        List<ToolSpecification> toolsSpecs = ToolSpecifications.toolSpecificationsFrom(sqlTools);
        var questionWithContext = String.format("this is the question: %s. This is the context: %s",question, currentState());
        ChatRequest chatRequest = ChatRequest.builder()
                .messages(new UserMessage(questionWithContext))
                .toolSpecifications(toolsSpecs)
                .build();
        ChatResponse answer = model.chat(chatRequest);

        log.debug(answer.toString());

        Optional<String> toolResponse =  answer.aiMessage().toolExecutionRequests()
                    .stream()
                    .filter( toolExecutionRequest -> toolExecutionRequest.name().equals("executeQuery"))
                    .findFirst() // Should be only one
                    .flatMap(sqlTools::executeQuerySafely);

        String finalResponse = answer.aiMessage().text() + toolResponse;
        var event = new ConversationEvent.AddedContext(String.format("question at %d is %s. And response is %s", System.currentTimeMillis(), question, finalResponse));
        return effects()
                .persist(event)
                .thenReply(__ -> finalResponse);
    }

    @Override
    public Conversation applyEvent(ConversationEvent conversationEvent) {
        return switch (conversationEvent) {
            case ConversationEvent.AddedContext added ->
                currentState().addContext(added.context());
        };
    }
}
