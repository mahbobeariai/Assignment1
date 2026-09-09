let mediaRecorder;

const startButton = document.getElementById("startButton");
const stopButton = document.getElementById("stopButton");
const status = document.getElementById("status");

startButton.addEventListener("click", startRecording);
stopButton.addEventListener("click", stopRecording);

async function startRecording() {
    const stream = await navigator.mediaDevices.getUserMedia({
        audio: true
    });

    mediaRecorder = new MediaRecorder(stream);
    mediaRecorder.start();

    status.textContent = "Recording...";
    startButton.disabled = true;
	stopButton.disabled = false;
}
function stopRecording() {

    mediaRecorder.stop();

    status.textContent = "Recording stopped.";

    startButton.disabled = false;
    stopButton.disabled = true;
}