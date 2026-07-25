package com.rahul.finflow.core.service.tracker;

import com.rahul.finflow.api.dto.tracker.AiInsightResponse;
import com.rahul.finflow.api.dto.tracker.CategorySpendResponse;
import com.rahul.finflow.api.dto.tracker.DashboardSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAdvisorService {

    private final DashboardService dashboardService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public AiInsightResponse getFinancialAdvice(int month, int year, String email) {
        // 1. Fetch the exact same data the dashboard uses
        DashboardSummaryResponse data = dashboardService.getDashboardSummary(month, year, email);

        // 2. Build the Prompt for the AI
        String prompt = buildPrompt(data, month, year);

        // 3. Call the AI Provider (OpenAI / Gemini / Claude)
        // We will implement the actual API call next based on your preference!
        String aiText = callGeminiApi(prompt);

        return parseAiResponse(aiText);
    }

    private String callGeminiApi(String prompt) {
        try {
            // Build the specific JSON structure Gemini expects
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // Make the POST request
            String fullUrl = geminiApiUrl + geminiApiKey;
            JsonNode responseNode = restTemplate.postForObject(fullUrl, requestEntity, JsonNode.class);

            // Parse the response
            if (responseNode != null && responseNode.has("candidates")) {
                return responseNode.at("/candidates/0/content/parts/0/text").asText();
            }
            return "Unable to generate advice at this time.";

        } catch (Exception e) {
            log.error("Failed to connect to Gemini API: {}", e.getMessage());
            return "AI Advisor is currently taking a coffee break. Check back later!";
        }
    }

    private String buildPrompt(DashboardSummaryResponse data, int month, int year) {
        // Find their highest spending category
        String topCategory = data.categoryBreakDown().stream().max((c1, c2)->c1.totalSpent().compareTo(c2.totalSpent())).map(CategorySpendResponse::categoryName).orElse("None");

        // Format active goals into a readable string
        String goalsStr = data.activeGoals().isEmpty() ? "No active goals." :
                data.activeGoals().stream()
                        .map(g -> g.name() + " (" + g.progressPercentage() + "% complete)")
                        .collect(Collectors.joining(", "));

        return String.format("""
            You are an expert, empathetic financial advisor for an app called FinFlow.
            Analyze the following user data for month %d, year %d:
            - Total Income: ₹%s
            - Total Expense: ₹%s
            - Net Balance: ₹%s
            - Highest Spending Category: %s
            - Active Savings Goals: %s
            
            Keep your response under 3 sentences. Give a brief summary of their financial health, 
            and provide ONE highly specific, actionable piece of advice based on their top spending 
            category or their savings goals. Do not use robotic language.
            """,
                month, year,
                data.totalIncome(), data.totalExpense(), data.netBalance(),
                topCategory, goalsStr
        );
    }

    private AiInsightResponse parseAiResponse(String aiText) {
        // For now, we will just map the raw text into our DTO.
        // Once we hook up the real AI, we can instruct it to return JSON!
        return new AiInsightResponse("Monthly Snapshot", aiText);
    }
}