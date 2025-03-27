package com.lb.api;

import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import com.lb.application.ConversationEntity;

import java.util.concurrent.CompletionStage;

@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint("/ask")
public class ConversationEndpoint {

  ComponentClient componentClient;

  public ConversationEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post("/{conversationId}")
  public CompletionStage<String> ask(String conversationId, Question question) {
    return componentClient.forEventSourcedEntity(conversationId)
            .method(ConversationEntity::ask)
            .invokeAsync(question.ask()).toCompletableFuture();
}
}

