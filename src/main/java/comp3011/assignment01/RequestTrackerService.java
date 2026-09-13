package comp3011.assignment01;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class RequestTrackerService {
    private final Map<String, Long> stats = new HashMap<>();

    public RequestTrackerService() {
        stats.put("totalRequests", 0L);
        stats.put("successfulRequests", 0L);
        stats.put("failedRequests", 0L);
    }
    public void requestStarted() {
        stats.put("totalRequests",
                stats.get("totalRequests") + 1);
    }
    public void requestSucceeded() {
        stats.put("successfulRequests",
                stats.get("successfulRequests") + 1);
    }
    public void requestFailed() {
        stats.put("failedRequests",
                stats.get("failedRequests") + 1);
    }
    public Map<String, Long> getStats() {
        return stats;
    }
}