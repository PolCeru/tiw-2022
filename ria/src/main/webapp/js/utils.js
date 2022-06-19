function makeFormRequest(url, formElement) {
    return fetch(url, {
        method: "POST",
        body: new URLSearchParams(new FormData(formElement)),
    })
}

function makeLogoutRequest() {
    return fetch("logout", {
        method: "GET",
    })
}

function makeGetRequest(url) {
    return fetch(url, {
        method: "GET",
    })
}
