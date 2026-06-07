function loadDocuments() {

    fetch("/api/documents", {
        headers: authHeaders()
    })
    .then(res => res.json())
    .then(docs => {

        const table = document.getElementById("docs");
        table.innerHTML = "";

        docs.forEach(doc => {
            table.innerHTML += `
              <tr>
                <td>${doc.originalFileName}</td>
                <td>v${doc.version}</td>
                <td>${doc.uploadedBy}</td>
                <td>
                  <button onclick="downloadLatest('${doc.originalFileName}')">
                    Download
                  </button>
                  <button onclick="viewVersions('${doc.originalFileName}')">
                    Versions
                  </button>
                </td>
              </tr>
            `;
        });
    });
}

function downloadLatest(fileName) {
    window.location =
        `/api/documents/download/latest/${encodeURIComponent(fileName)}`;
}
