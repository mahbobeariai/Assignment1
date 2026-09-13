package comp3011.assignment01;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class RequestTrackerService {
    private final ConcurrentMap<String, Long> stats =
            new ConcurrentHashMap<>();

    public RequestTrackerService() {
        stats.put("totalRequests", 0L);
        stats.put("successfulRequests", 0L);
        stats.put("failedRequests", 0L);
    }
    public synchronized void requestStarted() {
        stats.put("totalRequests",
                stats.get("totalRequests") + 1);
    }
    public synchronized void requestSucceeded() {
        stats.put("successfulRequests",
                stats.get("successfulRequests") + 1);
    }
    public synchronized void requestFailed() {
        stats.put("failedRequests",
                stats.get("failedRequests") + 1);
    }
    public Map<String, Long> getStats() {
        return stats;
    }
}