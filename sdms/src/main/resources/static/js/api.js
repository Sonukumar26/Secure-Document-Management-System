const API_BASE = "/api";

function authHeaders() {
    return {
        "Authorization": "Bearer " + localStorage.getItem("token")
    };
}

function authJsonHeaders() {
    return {
        ...authHeaders(),
        "Content-Type": "application/json"
    };
}
