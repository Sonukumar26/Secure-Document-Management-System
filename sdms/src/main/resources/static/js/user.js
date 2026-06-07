const token = localStorage.getItem("token");

if (!token) {
    window.location.href = "/login.html";
}

/* ============================
   ROLE CHECK
============================ */
fetch("/auth/me", {
    headers: {
        "Authorization": "Bearer " + token
    }
})
.then(res => res.json())
.then(user => {
    // Allow USER and ADMIN both
    if (user.role !== "ROLE_USER" && user.role !== "ROLE_ADMIN") {
        showToast("Access denied");
        window.location.href = "/dashboard.html";
    }
})
.catch(() => {
    window.location.href = "/login.html";
});


/* ============================
   UPLOAD DOCUMENT
============================ */
function uploadDocument() {
    const file = document.getElementById("file").files[0];

    if (!file) {
        showToast("Select a file first", "error");
        return;
    }

    const formData = new FormData();
    formData.append("file", file);

    fetch("/api/documents/upload", {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token
        },
        body: formData
    })
    .then(res => {
        if (!res.ok) throw new Error();
        return res.json();
    })
    .then(() => {
        document.getElementById("uploadMsg").innerText =
            "✅ Upload successful";
        loadMyDocuments();
    })
    .catch(() => {
        document.getElementById("uploadMsg").innerText =
            "❌ Upload failed";
    });
}


/* ============================
   LOAD ONLY MY DOCUMENTS
============================ */
function loadMyDocuments() {

    fetch("/api/documents/my", {
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    .then(res => res.json())
    .then(docs => {

        const tbody = document.getElementById("docs");
        tbody.innerHTML = "";

        docs.forEach(doc => {

            tbody.innerHTML += `
                <tr>
                    <td>${doc.originalFileName || doc.fileName}</td>
                    <td>${doc.version}</td>
                    <td>${doc.uploadedAt}</td>
                    <td>
                        <button onclick="download(${doc.id})">⬇ Download</button>
                    </td>
                </tr>
            `;
        });
    });
}


/* ============================
   DOWNLOAD DOCUMENT
============================ */
function download(id) {

    fetch(`/api/documents/download/${id}`, {
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    .then(res => {

        const disposition = res.headers.get("Content-Disposition");
        let filename = "document";

        if (disposition && disposition.includes("filename=")) {
            filename = disposition.split("filename=")[1].replaceAll('"', "");
        }

        return res.blob().then(blob => ({ blob, filename }));
    })
    .then(({ blob, filename }) => {

        const url = URL.createObjectURL(blob);

        const a = document.createElement("a");
        a.href = url;
        a.download = filename;

        document.body.appendChild(a);
        a.click();
        a.remove();

        URL.revokeObjectURL(url);
    });
}


/* ============================
   LOGOUT
============================ */
function logout() {
    localStorage.removeItem("token");
    window.location.href = "/login.html";
}


/* ============================
   INIT
============================ */
loadMyDocuments();
