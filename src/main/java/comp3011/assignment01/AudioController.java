package comp3011.assignment01;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class AudioController {
    private final TranscriptionService transcriptionService;

    public AudioController(TranscriptionService transcriptionService) {
        this.transcriptionService = transcriptionService;
    }

    @PostMapping("/audio")
    public String receiveAudio(@RequestParam("audio") MultipartFile audio) {

//        System.out.println("Received audio: " + audio.getOriginalFilename());
//
//        return "Audio received.";
    	return transcriptionService.transcribe(audio);
    }
}