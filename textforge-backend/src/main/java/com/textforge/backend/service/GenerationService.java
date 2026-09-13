package com.textforge.backend.service;

import com.textforge.backend.dto.*;
import com.textforge.backend.entity.Generation;
import com.textforge.backend.entity.User;
import com.textforge.backend.repository.GenerationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class GenerationService {

    private final RestClient openAiRestClient;
    private final GenerationRepository generationRepository;

    @Value("${openai.model}")
    private String model;

    public GenerationService(RestClient openAiRestClient, GenerationRepository generationRepository) {
        this.openAiRestClient = openAiRestClient;
        this.generationRepository = generationRepository;
    }

    public GenerationResponse generate(GenerateRequest request, User user) {
        Generation.Mode mode = parseMode(request.getMode());
        String prompt = buildPrompt(mode, request.getText());

        String outputText = callOpenAi(prompt);

        Generation generation = new Generation();
        generation.setMode(mode);
        generation.setInputText(request.getText());
        generation.setOutputText(outputText);
        generation.setUser(user);
        generationRepository.save(generation);

        return toResponse(generation);
    }

    public List<GenerationResponse> getHistory(User user) {
        return generationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Generation.Mode parseMode(String rawMode) {
        try {
            return Generation.Mode.valueOf(rawMode.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Mode invalide : doit être SUMMARIZE, REWRITE ou EXPAND");
        }
    }

    private String buildPrompt(Generation.Mode mode, String text) {
        return switch (mode) {
            case SUMMARIZE -> "Résume ce texte en français, de façon concise :\n\n" + text;
            case REWRITE -> "Reformule ce texte en français, en gardant le même sens :\n\n" + text;
            case EXPAND -> "Développe et enrichis ce texte en français, avec plus de détails :\n\n" + text;
        };
    }

    private String callOpenAi(String prompt) {
        OpenAiChatRequest request = new OpenAiChatRequest(
                model,
                List.of(new OpenAiChatRequest.Message("user", prompt)),
                0.7
        );

        OpenAiChatResponse response = openAiRestClient.post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .body(OpenAiChatResponse.class);

        if (response == null || response.choices().isEmpty()) {
            throw new IllegalStateException("Réponse vide de l'API OpenAI");
        }

        return response.choices().get(0).message().content();
    }

    private GenerationResponse toResponse(Generation g) {
        return new GenerationResponse(g.getId(), g.getMode().name(), g.getInputText(), g.getOutputText(), g.getCreatedAt());
    }
}