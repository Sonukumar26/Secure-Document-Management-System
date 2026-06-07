function authFetch(url, options = {}) {
    const token = localStorage.getItem("token");

    if (!token) {
        window.location.href = "/login.html";
        return;
    }

    options.headers = {
        ...(options.headers || {}),
        "Authorization": "Bearer " + token
    };

    return fetch(url, options)
        .then(res => {
            if (res.status === 401 || res.status === 403) {
                localStorage.removeItem("token");
                window.location.href = "/login.html";
                throw new Error("Unauthorized");
            }
            return res;
        });
}


authFetch("/auth/me")
    .then(res => res.json())
.then(user => {
    if (user.role !== "ROLE_ADMIN") {
        showToast("Access denied");
        window.location.href = "/dashboard.html";
    }
})
.catch(() => {
    localStorage.removeItem("token");
    window.location.href = "/login.html";
});


/* 📤 Upload */
function uploadDocument() {
    const file = document.getElementById("file").files[0];
    if (!file) {
        showToast("Select a file", "error");
        return;
    }

    const formData = new FormData();
    formData.append("file", file);

    fetch("/api/documents/uploads", {
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
        loadDocuments();
        loadAuditLogs();
    })
    .catch(() => {
        document.getElementById("uploadMsg").innerText =
            "❌ Upload failed";
    });
}

/* 📄 Load ALL documents */
function loadDocuments() {
    authFetch("/api/documents")
        .then(res => res.json())
        .then(docs => {
            const tbody = document.getElementById("docs");
            tbody.innerHTML = "";

            docs.forEach(doc => {
                tbody.innerHTML += `
                    <tr>
                        <td>${doc.originalFileName || doc.fileName}</td>
                        <td>${doc.version}</td>
                        <td>${doc.uploadedBy}</td>
                        <td>${doc.active}</td>
                        <td>
                            <button onclick="download(${doc.id})">⬇</button>
                            <button onclick="deleteDoc(${doc.id})">🗑</button>
                            <button onclick="rollback('${doc.originalFileName}', ${doc.version})">Rollback</button>
                            <button onclick="showVersions('${doc.originalFileName}')">Versions</button>
                        </td>
                    </tr>
                `;
            });
        });
}


/* ⬇ Download */
function download(id) {
    authFetch(`/api/documents/download/${id}`)
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


/* 🗑 Delete */
function deleteDoc(id) {
    if (!confirm("Delete document?")) return;

    fetch(`/api/documents/${id}`, {
        method: "DELETE",
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    .then(() => {
        loadDocuments();
        loadAuditLogs();
    });
}

/* 🔁 Rollback */
function rollback(fileName, version) {
    if (!confirm(`Rollback ${fileName} to v${version}?`)) return;

    fetch(`/api/documents/rollback/${fileName}/v/${version}`, {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    .then(() => {
        loadDocuments();
        loadAuditLogs();
    });
}

/* 📜 Version list */
function showVersions(fileName) {
    fetch(`/api/documents/versions/${fileName}`, {
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    .then(res => res.json())
    .then(versions => {
        const div = document.getElementById("versions");
        div.innerHTML = `<h4>${fileName}</h4>`;

        versions.forEach(v => {
            div.innerHTML += `
                <p>
                    v${v.version}
                    | ${v.uploadedAt}
                    | Active: ${v.active}
                    <button onclick="download(${v.id})">⬇</button>
                    <button onclick="rollback('${fileName}', ${v.version})">
                        Rollback
                    </button>
                </p>
            `;
        });
    });
}

/* 📜 Audit logs */
let currentPage = 0;

function loadAuditLogs() {

    const search = document
        .getElementById("searchUser").value;

    authFetch(`/api/admin/audit?search=${search}&page=${currentPage}&size=5`)
    .then(r => r.json())
    .then(data => {

        const tbody = document.getElementById("audit");
        tbody.innerHTML = "";

        data.content.forEach(log => {

            tbody.innerHTML += `
            <tr>
                <td>${log.username}</td>
                <td>${log.action}</td>
                <td>${log.fileName}</td>
                <td>${log.timestamp}</td>
            </tr>
            `;
        });

    });
}

function nextPage() {
    currentPage++;
    loadAuditLogs();
}

function prevPage() {
    if (currentPage > 0) {
        currentPage--;
        loadAuditLogs();
    }
}

/* 🚪 Logout */
function logout() {
    localStorage.removeItem("token");
    window.location.href = "/login.html";
}


/* 🚀 Load everything */
loadDocuments();
loadAuditLogs();
