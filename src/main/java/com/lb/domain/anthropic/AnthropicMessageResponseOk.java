package com.lb.domain.anthropic;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AnthropicMessageResponseOk(
        String id,
        String type,
        String role,
        String model,
        List<Content> content,

        @JsonProperty("stop_reason")
        String stopReason,

        @JsonProperty("stop_sequence")
        String stopSequence,

        Usage usage
) {
    // Nested Content record for the content array
    public record Content(
            String type,
            String text
    ) {}

    // Nested Usage record
    public record Usage(
            @JsonProperty("input_tokens")
            int inputTokens,

            @JsonProperty("cache_creation_input_tokens")
            int cacheCreationInputTokens,

            @JsonProperty("cache_read_input_tokens")
            int cacheReadInputTokens,

            @JsonProperty("output_tokens")
            int outputTokens
    ) {}
}