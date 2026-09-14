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
        stats.put("inputTokens", 0L);
        stats.put("outputTokens", 0L);
    }
    public synchronized void addTokens(long inputTokens, long outputTokens) {
        stats.put("inputTokens",
                stats.get("inputTokens") + inputTokens);

        stats.put("outputTokens",
                stats.get("outputTokens") + outputTokens);
    }
    public Map<String, Long> getStats() {
        return stats;
    }
}