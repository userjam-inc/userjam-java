# **Userjam Java SDK**

A minimal, zero-dependency\* SDK for integrating Userjam tracking into Java 11+ applications.  
*Note: This SDK uses java.net.http.HttpClient (Java 11+) and depends only on jackson-databind for robust JSON serialization. It is designed to be dropped directly into your source tree.*

## **Installation**

Since this is a lightweight reference implementation, you don't need to add a repository to your build system.

1. **Copy the SDK**: Copy src/main/java/com/userjam/sdk/Userjam.java into your project's source path.  
2. **Add Dependency**: Ensure you have Jackson Databind available in your project.

### **Maven (pom.xml)**

\<dependency\>  
    \<groupId\>com.fasterxml.jackson.core\</groupId\>  
    \<artifactId\>jackson-databind\</artifactId\>  
    \<version\>2.15.2\</version\>  
\</dependency\>

### **Gradle (build.gradle)**

implementation 'com.fasterxml.jackson.core:jackson-databind:2.15.2'

## **Usage**

The SDK is asynchronous by default, returning CompletableFuture\<HttpResponse\<String\>\>.  
import com.userjam.sdk.Userjam;  
import java.util.Map;

public class App {  
    public void init() {  
        // 1\. Initialize with your API Key  
        Userjam.auth("YOUR\_API\_KEY\_UUID");  
    }

    public void logPurchase(String userId) {  
        // 2\. Track an event  
        Userjam.track(userId, "Purchase Completed", Map.of(  
            "amount", 99.99,  
            "currency", "USD"  
        ));  
          
        // 3\. Identify a user (optional)  
        Userjam.identify(userId, Map.of(  
            "email", "user@example.com",  
            "plan", "Pro"  
        ));  
    }  
}

## **Running the Example**

This repository includes a runnable example.

1. **Prerequisites**: Java 11+ and Maven.  
2. **Run with a generated test key**:  
   mvn compile exec:java \-Dexec.mainClass="ExampleUsage"

3. **Run with your real API key**:  
   mvn compile exec:java \-Dexec.mainClass="ExampleUsage" \-Dexec.args="YOUR\_REAL\_API\_KEY"  
