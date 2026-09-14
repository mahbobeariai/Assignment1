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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Assignment01ApplicationTests {

    @LocalServerPort
    private int port;
    @Test
    void contextLoads() {
    }
    @Test
    void handlesManyRequestsAtTheSameTime() {

        HttpClient client = HttpClient.newHttpClient();

        List<CompletableFuture<HttpResponse<String>>> requests =
                new ArrayList<>();
        for (int i = 0; i < 250; i++) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "http://localhost:" + port + "/api/v1/admin/uptime"))
                    .GET()
                    .build();

            requests.add(
                    client.sendAsync(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    )
            );
        }
        CompletableFuture.allOf(
                requests.toArray(new CompletableFuture[0])
        ).join();

        for (CompletableFuture<HttpResponse<String>> request : requests) {
            assertEquals(200, request.join().statusCode());
        }
    }
}