let mediaRecorder;
let recordedParts = [];

const startButton = document.getElementById("startButton");
const stopButton = document.getElementById("stopButton");
const status = document.getElementById("status");
const transcription = document.getElementById("transcription");

startButton.addEventListener("click", startRecording);
stopButton.addEventListener("click", stopRecording);

async function startRecording() {
    try {
	const stream = await navigator.mediaDevices.getUserMedia({
        audio: true
    });

    mediaRecorder = new MediaRecorder(stream);
	recordedParts = [];
	
	mediaRecorder.addEventListener("dataavailable", function(event) {
	    recordedParts.push(event.data);
	});
	mediaRecorder.addEventListener("stop", function() {

	           const audioBlob = new Blob(recordedParts, {
	               type: "audio/webm"
	           });

	           uploadAudio(audioBlob);

	           stream.getTracks().forEach(function(track) {
	               track.stop();
	           });
	       });
    mediaRecorder.start();

    status.textContent = "Recording...";
    startButton.disabled = true;
	stopButton.disabled = false;
	} catch (error) {
	    status.textContent = "Could not access the microphone.";
	}
}
function stopRecording() {

	mediaRecorder.stop();
    status.textContent = "Recording stopped.";

    startButton.disabled = false;
    stopButton.disabled = true;
}
async function uploadAudio(audioBlob) {

    const formData = new FormData();

    formData.append("audio", audioBlob, "recording.webm");
	try{

	    const response = await fetch("/api/audio", {
	        method: "POST",
	        body: formData
	    });
		if (!response.ok) {
		    throw new Error("Could not upload the recording.");
		}
	
		const text = await response.text();
	
		transcription.textContent = text;
	} catch (error) {
	    transcription.textContent = error.message;
	    status.textContent = "Something went wrong.";
	}
}