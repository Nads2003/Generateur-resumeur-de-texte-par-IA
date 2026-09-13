package com.textforge.backend.dto;

import java.util.List;

public record OpenAiChatRequest(
        String model,
        List<Message> messages,
        double temperature
) {
    public record Message(String role, String content) {}
}