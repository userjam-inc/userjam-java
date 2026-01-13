package com.userjam.sdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Userjam SDK for Java 11+
 * A minimal client for the UserJam tracking API.
 *
 * DEPENDENCY REQUIRED: com.fasterxml.jackson.core:jackson-databind
 */
public class Userjam {

    private static final String REPORT_URL = "https://api.userjam.com/api/report";
    
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Jackson ObjectMapper is thread-safe and should be reused
    private static final ObjectMapper mapper = new ObjectMapper();

    private static volatile String trackingKey;

    /**
     * Initialize the SDK with your tracking key.
     * @param key The API Key (UUID) provided by UserJam.
     */
    public static void auth(String key) {
        trackingKey = key;
    }

    /**
     * Track an event for a specific user.
     *
     * @param userId     The unique identifier for the user.
     * @param event      The name of the event (e.g., "Signed Up").
     * @param properties Optional metadata associated with the event.
     * @return A CompletableFuture containing the HTTP response.
     */
    public static CompletableFuture<HttpResponse<String>> track(String userId, String event, Map<String, Object> properties) {
        validateConfig();

        Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("type", "track");
        payload.put("event", event);
        payload.put("userId", userId);
        payload.put("timestamp", Instant.now().toString());
        if (properties != null) {
            payload.put("properties", properties);
        }

        return sendRequest(payload);
    }

    /**
     * Overload for track without properties.
     */
    public static CompletableFuture<HttpResponse<String>> track(String userId, String event) {
        return track(userId, event, null);
    }

    /**
     * Identify a user with specific traits.
     *
     * @param userId The unique identifier for the user.
     * @param traits Key-value pairs describing the user (e.g., email, name).
     * @return A CompletableFuture containing the HTTP response.
     */
    public static CompletableFuture<HttpResponse<String>> identify(String userId, Map<String, Object> traits) {
        validateConfig();

        Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("type", "identify");
        payload.put("userId", userId);
        payload.put("timestamp", Instant.now().toString());
        if (traits != null) {
            payload.put("traits", traits);
        }

        return sendRequest(payload);
    }

    // --- Private Helpers ---

    private static void validateConfig() {
        if (trackingKey == null || trackingKey.isEmpty()) {
            throw new IllegalStateException("Userjam: Tracking key not set. Call Userjam.auth('KEY') first.");
        }
    }

    private static CompletableFuture<HttpResponse<String>> sendRequest(Map<String, Object> payloadMap) {
        String jsonBody;
        try {
            jsonBody = mapper.writeValueAsString(payloadMap);
        } catch (JsonProcessingException e) {
            CompletableFuture<HttpResponse<String>> errorFuture = new CompletableFuture<>();
            errorFuture.completeExceptionally(new RuntimeException("Userjam: Failed to serialize JSON payload", e));
            return errorFuture;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(REPORT_URL))
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + trackingKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}