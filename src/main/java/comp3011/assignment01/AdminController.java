package comp3011.assignment01;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
	// Store the time when the server starts so it can be used to calculate uptime.
    private final Instant startTime = Instant.now();
    private boolean shutdownInProgress = false;
    @GetMapping("/uptime")
    public Map<String, Object> getUptime() {

        Instant now = Instant.now();

        Map<String, Object> result = new HashMap<>();

        result.put("utcServerStart", startTime);
        result.put("utcNow", now);
        // Calculate how long the server has been running in seconds.
        result.put("serverUptimeSeconds",
                (now.toEpochMilli() - startTime.toEpochMilli()) / 1000.0);

        return result;
    }
    // Request a graceful shutdown.
    @PostMapping("/shutdown")
    public ResponseEntity<Map<String, String>> shutdown() {

        if (shutdownInProgress) {
            Map<String, String> result = new HashMap<>();
            result.put("message", "Shutdown already in progress.");

            return ResponseEntity.status(409).body(result);
        }
        shutdownInProgress = true;

        Map<String, String> result = new HashMap<>();
        result.put("message", "Graceful shutdown requested.");

        Thread shutdownThread = new Thread(() -> {
            try {
                Thread.sleep(100);
                System.exit(0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        shutdownThread.start();

        return ResponseEntity.accepted().body(result);
    }
}