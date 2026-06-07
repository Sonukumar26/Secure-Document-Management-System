function viewVersions(fileName) {

    fetch(`/api/documents/versions/${encodeURIComponent(fileName)}`, {
        headers: authHeaders()
    })
    .then(res => res.json())
    .then(versions => {

        let html = "";
        versions.forEach(v => {
            html += `
              <li>
                v${v.version}
                <button onclick="downloadVersion('${fileName}', ${v.version})">
                  Download
                </button>
              </li>
            `;
        });

        document.getElementById("versions").innerHTML = html;
    });
}
function downloadVersion(fileName, version) {
    window.location =
      `/api/documents/download/${encodeURIComponent(fileName)}/v/${version}`;
}
