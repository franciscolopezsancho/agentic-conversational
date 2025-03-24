package com.lb.domain;

import akka.javasdk.annotations.TypeName;

public sealed interface ConversationEvent {
    @TypeName("added-context")
    record AddedContext(String context) implements ConversationEvent{}
}



