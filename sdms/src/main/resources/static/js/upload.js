function uploadDocument() {

    const fileInput = document.getElementById("file");
    const file = fileInput.files[0];

    if (!file) {
        showToast("Please select a file", "error");
        return;
    }

    const formData = new FormData();
    formData.append("file", file);

    fetch("/api/documents/uploads", {
        method: "POST",
        headers: authHeaders(),
        body: formData
    })
    .then(res => {
        if (!res.ok) throw new Error("Upload failed");
        return res.json();
    })
    .then(() => {
        showToast("Upload successful");
        loadDocuments();
    })
    .catch(() => showToast("Upload error", "error"));
}
