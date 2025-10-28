package br.com.alura.screenmatch.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;

public class ConsultaChatGpt {


    public static String getTraducao(String texto) {
        String openaiApiKey = System.getenv("OPENAI_API_KEY");
        OpenAiService servico = new OpenAiService(openaiApiKey);

        CompletionRequest request = CompletionRequest.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt("traduza para o português o texto: " + texto)
                .maxTokens(1000)
                .temperature(0.7)
                .build();

        CompletionResult result = servico.createCompletion(request);
        return result.getChoices().get(0).getText();
    }
}