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

