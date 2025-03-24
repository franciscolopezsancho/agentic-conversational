package com.lb.application;

import akka.javasdk.annotations.ComponentId;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lb.domain.anthropic.AnthropicMessageResponseError;
import com.lb.domain.anthropic.AnthropicResponse;
import com.lb.domain.anthropic.AnthropicMessageResponseOk;
import com.lb.domain.Conversation;
import com.lb.domain.ConversationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@ComponentId("conversation")
public class ConversationEntity extends EventSourcedEntity<Conversation, ConversationEvent> {

    Logger log = LoggerFactory.getLogger(ConversationEntity.class);
    ObjectMapper mapper = new ObjectMapper();

    HttpClient client;
    public ConversationEntity() {
        client = HttpClient.newHttpClient();
    }


    public Effect<AnthropicResponse> ask(String question){
        // TODO
        // TODO use anthropic messages https://docs.anthropic.com/en/api/client-sdks#java
        HttpRequest request = HttpRequest
                .newBuilder(URI.create("https://api.anthropic.com/v1/messages"))
                        .header("x-api-key", System.getenv("ANTHROPIC_API_KEY") ) //TODO add var
                .header("anthropic-version", "2023-06-01")
                .header("content-type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{" +
                                "\"model\": \"claude-3-5-sonnet-20241022\",\n" +
                                "\"max_tokens\": 1024,\n" +
                                "\"messages\": [\n" +
                                "{\"role\": \"user\", \"content\": \" " + question+"\"}\n" +
                                "]\n" +
                                "}"))
                .build();

        log.debug("Requesting uri: " + request.uri());
        log.debug("Requesting method: " + request.method());
        log.debug("Requesting headers: " + request.headers());
        log.debug("Requesting body: " + request.bodyPublisher());

        try {
            HttpResponse<String> response =  client.send(request, HttpResponse.BodyHandlers.ofString());
            log.debug("Response: " + response.statusCode());
            log.debug("Response: " + response.body());
            switch (response.statusCode()) {
                case 200 -> {
                    AnthropicMessageResponseOk aResponse = mapper.readValue(response.body(), AnthropicMessageResponseOk.class);
                    return effects().reply(new AnthropicResponse(Optional.of(aResponse), Optional.empty()));
                }
                default -> {
                   AnthropicMessageResponseError aResponse = mapper.readValue(response.body(), AnthropicMessageResponseError.class);
                   return effects().reply(new AnthropicResponse(Optional.empty(), Optional.of(aResponse)));
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Conversation applyEvent(ConversationEvent conversationEvent) {
        return switch (conversationEvent) {
            case ConversationEvent.AddedContext added ->
                currentState().addContext(added.context());
        };
    }
}
