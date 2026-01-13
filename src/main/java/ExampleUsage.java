import com.userjam.sdk.Userjam;

import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Example class demonstrating how to integrate the Userjam SDK.
 * * To run this with a generated key:
 * mvn compile exec:java -Dexec.mainClass="ExampleUsage"
 * * To run this with your real API key:
 * mvn compile exec:java -Dexec.mainClass="ExampleUsage" -Dexec.args="YOUR_REAL_API_KEY"
 */
public class ExampleUsage {

    public static void main(String[] args) {
        System.out.println("--- Starting Userjam SDK Example ---");

        // 1. Authenticate
        String apiKey;
        if (args.length > 0 && args[0] != null && !args[0].isEmpty()) {
            apiKey = args[0];
            System.out.println("Using provided API Key: " + apiKey);
        } else {
            apiKey = UUID.randomUUID().toString();
            System.out.println("No API Key provided. Using generated fake key: " + apiKey);
            System.out.println("Tip: You can pass your real key as a command line argument.");
        }
        
        Userjam.auth(apiKey);

        String userId = "user_12345";

        // 2. Identify a User
        // Added 'created_at' using native Java Instant (ISO8601)
        System.out.println("Sending Identify request...");
        
        CompletableFuture<HttpResponse<String>> identifyTask = Userjam.identify(userId, Map.of(
            "name", "Jane Doe",
            "email", "jane@example.com",
            "created_at", Instant.now().toString(), // ISO-8601 format (e.g., 2023-10-05T14:30:00Z)
            "is_active", true
        )).whenComplete((res, ex) -> {
            if (ex != null) {
                System.err.println("Identify failed: " + ex.getMessage());
            } else {
                System.out.println("Identify success: " + res.statusCode());
            }
        });

        // 3. Track an Event
        System.out.println("Sending Track request...");
        CompletableFuture<HttpResponse<String>> trackTask = Userjam.track(userId, "Button Clicked", Map.of(
            "button_id", "signup_header",
            "page", "landing_page"
        )).whenComplete((res, ex) -> {
            if (ex != null) {
                System.err.println("Track failed: " + ex.getMessage());
            } else {
                System.out.println("Track success: " + res.statusCode());
            }
        });

        // 4. Wait for completion (For demonstration purposes only)
        // In a real web server (Spring/Micronaut), you wouldn't block the main thread like this.
        CompletableFuture.allOf(identifyTask, trackTask).join();
        System.out.println("--- Example Complete ---");
    }
}