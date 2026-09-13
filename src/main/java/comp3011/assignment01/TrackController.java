package comp3011.assignment01;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/global")
public class TrackController {
    private final RequestTrackerService requestTrackerService;

    public TrackController(RequestTrackerService requestTrackerService) {
        this.requestTrackerService = requestTrackerService;
    }
    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        return requestTrackerService.getStats();
    }
}