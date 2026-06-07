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
                showToast("Session expired. Please login again.");
                localStorage.removeItem("token");
                window.location.href = "/login.html";
                throw new Error("Unauthorized");
            }
            return res;
        });
}

function login() {

    const username =
        document.getElementById("username").value;

    const password =
        document.getElementById("password").value;

    fetch("/auth/login", {   // ✅ FIXED PATH
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ username, password })
    })
    .then(res => {
        if (!res.ok) throw new Error("Login failed");
        return res.json();
    })
    .then(data => {
        localStorage.setItem("token", data.token);
        localStorage.setItem("role", data.role);
        window.location.href = "dashboard.html"; // ✅ redirect
    })
    .catch(() => {
        document.getElementById("error").innerText =
            "Invalid username or password";
    });

   
}

 function showToast(message, type="success") {

    const t = document.getElementById("toast");

    t.innerText = message;

    if(type === "error"){
        t.style.background = "#e74c3c";
    } else {
        t.style.background = "#2ecc71";
    }

    t.style.display = "block";

    setTimeout(() => {
        t.style.display = "none";
    }, 3000);
}
