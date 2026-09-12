package comp3011.assignment01;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final Instant startTime = Instant.now();

    @GetMapping("/uptime")
    public long getUptime() {
        return (Instant.now().toEpochMilli() - startTime.toEpochMilli()) / 1000;
    }
    
    @PostMapping("/shutdown")
    public void shutdown() {
        System.exit(0);
    }
}