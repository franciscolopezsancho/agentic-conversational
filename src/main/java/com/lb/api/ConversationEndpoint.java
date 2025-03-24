package com.lb.api;

import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import com.lb.application.ConversationEntity;
import com.lb.domain.anthropic.AnthropicMessageResponseOk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * This is a simple Akka Endpoint that returns "Hello World!".
 * Locally, you can access it by running `curl http://localhost:9000/hello`.
 */
// Opened up for access from the public internet to make the service easy to try out.
// For actual services meant for production this must be carefully considered, and often set more limited
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint("/ask")
public class ConversationEndpoint {

  ComponentClient componentClient;

  public ConversationEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post("/{conversationId}")
  public CompletionStage<List<String>> ask(String conversationId, Question question) {
    return componentClient.forEventSourcedEntity(conversationId)
            .method(ConversationEntity::ask)
            .invokeAsync(question.ask()).thenApply( response -> {
                if (response.success().isPresent()) {
                    return response.success().get().content().stream().map(AnthropicMessageResponseOk.Content::text).toList();
                }
                if (response.error().isPresent()) {
                    return new ArrayList<String>(List.of(response.error().get().error().message()));
                }
                return new ArrayList<String>(List.of("Missing success and error responses from Anthropic."));
  });
}
}

