package com.lb.domain.anthropic;

import java.util.Optional;

public record AnthropicResponse(Optional<AnthropicMessageResponseOk> success, Optional<AnthropicMessageResponseError> error) {
}
