package com.lb.domain.anthropic;

public record AnthropicMessageResponseError(String type, Error error) {
    public record Error(String type, String message) {}
}
