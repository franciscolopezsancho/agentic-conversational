package com.lb.domain;

import java.util.List;

public record Conversation(List<String> contexts){


    public Conversation addContext(String context){
        contexts.add(context);
        return new Conversation(contexts);
    }
}
