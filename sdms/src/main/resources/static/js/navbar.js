function loadUserInfo() {
    fetch("/auth/me", {
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        }
    })
    .then(res => {
        if (!res.ok) throw new Error();
        return res.json();
    })
    .then(data => {
        document.getElementById("navUser").innerText =
            data.username + " (" + data.role + ")";
    })
    .catch(() => {
        window.location.href = "/login.html";
    });
}

function logout() {
    localStorage.clear();
    window.location.href = "/login.html";
}

window.onload = loadUserInfo;
