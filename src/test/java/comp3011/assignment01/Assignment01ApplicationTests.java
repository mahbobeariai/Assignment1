package comp3011.assignment01;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Assignment01ApplicationTests {

    @LocalServerPort
    private int port;
    @Test
    void contextLoads() {
    }
    @Test
    void handlesManyRequestsAtTheSameTime() throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        ExecutorService executor = Executors.newFixedThreadPool(250);

        List<Future<HttpResponse<String>>> requests = new ArrayList<>();

        for (int i = 0; i < 250; i++) {
            Future<HttpResponse<String>> request = executor.submit(() -> {
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(
                                "http://localhost:" + port + "/api/v1/admin/uptime"))
                        .GET()
                        .build();
                return client.send(
                        httpRequest,
                        HttpResponse.BodyHandlers.ofString()
                );
            });
            requests.add(request);
        }

        for (Future<HttpResponse<String>> request : requests) {
            assertEquals(200, request.get().statusCode());
        }
        executor.shutdown();
    }
    
    @Test
    void tracksTokensCorrectlyWhenManyRequestsRunAtTheSameTime() {

        RequestTrackerService tracker = new RequestTrackerService();

        List<CompletableFuture<Void>> requests = new ArrayList<>();

        for (int i = 0; i < 250; i++) {
            requests.add(
                    CompletableFuture.runAsync(() -> {
                        tracker.addTokens(100, 20);
                    })
            );
        }
        CompletableFuture.allOf(
                requests.toArray(new CompletableFuture[0])
        ).join();

        Map<String, Long> stats = tracker.getStats();

        assertEquals(25000, stats.get("inputTokens"));
        assertEquals(5000, stats.get("outputTokens"));
    }
}