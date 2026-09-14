package comp3011.assignment01;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.net.http.HttpClient;
import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class TranscriptionService {
    
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RequestTrackerService requestTrackerService;
    
    public TranscriptionService(RequestTrackerService requestTrackerService) {
        this.requestTrackerService = requestTrackerService;
    }
    
	public CompletableFuture<String> transcribe(MultipartFile audio) {
		// Read the API key from the environment so it is not stored in the code.
        String apiKey = System.getenv("OPENAI_API_KEY");
		
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY is not set.");
        }
        
        byte[] audioBytes;
        String boundary = UUID.randomUUID().toString();
        ByteArrayOutputStream body = new ByteArrayOutputStream();

        try {
            audioBytes = audio.getBytes();
            // multipart request.
            body.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
            body.write(("Content-Disposition: form-data; name=\"model\"\r\n\r\n")
                    .getBytes(StandardCharsets.UTF_8));
            body.write(("gpt-4o-mini-transcribe\r\n").getBytes(StandardCharsets.UTF_8));
           
            body.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
            body.write(("Content-Disposition: form-data; name=\"file\"; filename=\"recording.webm\"\r\n")
                    .getBytes(StandardCharsets.UTF_8));
            body.write(("Content-Type: audio/webm\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            body.write(audioBytes);
            body.write("\r\n".getBytes(StandardCharsets.UTF_8));
            body.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
            
        } catch (IOException e) {
            throw new IllegalStateException("Could not prepare the audio request.", e);
        }
        
        URI openAiUrl = URI.create(
        		"https://api.openai.com/v1/audio/transcriptions"
        );
          
        
        HttpRequest request = HttpRequest.newBuilder()
        	    .uri(openAiUrl)
        	    .header("Authorization", "Bearer " + apiKey)
        	    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
        	    .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
        	    .build();
        
     // Send the audio to the OpenAI transcription service.
        return httpClient.sendAsync(
                request,
                HttpResponse.BodyHandlers.ofString()
            )
        		.thenApply(response -> {
        		    try {
        		        if (response.statusCode() != 200) {
        		            return "Transcription failed.";
        		        }

        		        JsonNode json = objectMapper.readTree(response.body());
        		        if (json.has("text")) {
        		            JsonNode usage = json.get("usage");
        		            if (usage != null) {
        		                long inputTokens =
        		                        usage.get("input_tokens").asLong();

        		                long outputTokens =
        		                        usage.get("output_tokens").asLong();

        		                requestTrackerService.addTokens(
        		                        inputTokens,
        		                        outputTokens
        		                );
        		            }
        		            return json.get("text").asText();
        		        }
        		        return "Transcription failed.";

        		    } catch (Exception e) {
        		        return "Could not read the transcription response.";
        		    }
        		})
        		.exceptionally(e -> {
        		    return "Could not contact the transcription service.";
        		});
    }
}