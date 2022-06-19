(function () {
    let loginForm = document.getElementById("login-form")
    let loginMsg = document.getElementById("login-msg")
    let signupForm = document.getElementById("signup-form")
    let signupMsg = document.getElementById("signup-msg")

    loginForm.addEventListener("submit", (event) => {
        event.preventDefault();
        if (loginForm.checkValidity()) {
            makeFormRequest("login", loginForm).then(response => {
                if (response.ok) {
                    response.json().then(json => {
                        sessionStorage.setItem("name", json.name);
                        sessionStorage.setItem("id", json.id);
                        window.location.href = "home";
                    })
                } else {
                    response.json().then(json => {
                        loginMsg.textContent = json.error;
                    })
                }
            })
        } else {
            loginForm.reportValidity()
        }
    })

    signupForm.addEventListener("submit", (event) => {
        event.preventDefault();
        if (signupForm.checkValidity()) {
            makeFormRequest("signup", signupForm).then(response => {
                if (response.ok && response.status === 201) {
                    signupMsg.textContent = "User account created successfully"
                } else {
                    response.json().then(json => {
                        signupMsg.textContent = json.error;
                    })
                }
            })
        } else {
            signupForm.reportValidity()
        }
    })
})()
