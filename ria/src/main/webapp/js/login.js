(function () {
    let loginForm = document.getElementById("login-form")
    let loginMsg = document.getElementById("login-msg")
    let signupForm = document.getElementById("signup-form")
    let signupMsg = document.getElementById("signup-msg")

    loginForm.addEventListener("submit", (event) => {
        event.preventDefault();
        if (loginForm.checkValidity()) {
            makeFormRequest("login", loginForm)
                .then(async response => {
                    if (!response.ok)
                        throw response
                    return response.json()
                })
                .then(json => {
                    sessionStorage.setItem("name", json.name)
                    sessionStorage.setItem("id", json.id)
                    window.location.href = "home"
                })
                .catch(async response => {
                    loginMsg.textContent = (await response.json()).error;
                })
        } else {
            loginForm.reportValidity()
        }
    })

    signupForm.addEventListener("submit", (event) => {
        event.preventDefault();
        if (signupForm.checkValidity()) {
            makeFormRequest("signup", signupForm)
                .then(async response => {
                    if (response.status !== 201)
                        throw response
                    signupMsg.textContent = "User account created successfully"
                })
                .catch(async response => {
                    signupMsg.textContent = (await response.json()).error
                })
        } else {
            signupForm.reportValidity()
        }
    })
})()
