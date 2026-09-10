package comp3011.assignment01;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
public class TranscriptionService {

    public String transcribe(MultipartFile audio) {

        System.out.println("Service received: " + audio.getOriginalFilename());

        return "Done transcription";

    }
}