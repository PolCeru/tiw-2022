document.getElementById("login-button").addEventListener("click", (event) => {
    event.preventDefault();
    let form = event.target.closest("form")
    if (form.checkValidity()) {
        makeFormRequest('login', form, function (request) {
            if (request.readyState === XMLHttpRequest.DONE) {
                const response = JSON.parse(request.responseText)
                if (request.status === 200) {
                    sessionStorage.setItem("sessionId", response.sessionId);
                    window.location.navigate("home.html")
                } else {
                    document.getElementById("login-msg").textContent = response.message;
                }
            }
        })
    } else form.reportValidity()
})

document.getElementById("signup-button").addEventListener("click", (event) => {
    event.preventDefault();
    let form = event.target.closest("form")
    if (form.checkValidity()) {
        makeFormRequest('signup', form, function (request) {
            if (request.readyState === XMLHttpRequest.DONE) {
                const response = JSON.parse(request.responseText)
                if (response !== undefined && response.message !== 'undefined' && response.message !== 'null')
                    document.getElementById("signup-msg").textContent = response.message
                else
                    document.getElementById("signup-msg").textContent = "An unknown error occurred"
            }
        })
    } else
        form.reportValidity()
})
