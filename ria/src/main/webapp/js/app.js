(function () {
    let name = sessionStorage.getItem("name")
    let currentAccountID
    let root = document.getElementById("page")

    let pageRouter = new PageRouter()
    window.addEventListener("load", () => {
        pageRouter.start()
        pageRouter.render()
    })

    function PageRouter() {
        this.header = new Header()

        this.start = () => {
            this.currentPage = "home"
            this.activePage = new HomePage()
        }

        this.goToPage = (destinationPage) => {
            if (destinationPage === "home") {
                this.activePage = new HomePage()
            } else if (destinationPage === "account") {
                this.activePage = new AccountDetailsPage()
            }
            this.previousPage = this.currentPage
            this.currentPage = destinationPage
            this.render()
        }

        this.goBack = () => {
            this.goToPage(this.previousPage)
        }

        this.render = () => {
            this.header.render()
            this.activePage.render()
        }
    }

    function Header() {
        this.back = document.getElementById("back")
        this.logout = document.getElementById("logout")
        this.text = document.getElementById("header-text")

        this.back.addEventListener("click", () => {
            pageRouter.goBack()
        })

        this.logout.addEventListener("click", () => {
            makeLogoutRequest().then((response) => {
                if (response.ok && response.redirected) {
                    sessionStorage.clear()
                    window.location.href = response.url
                }
            })
        })

        this.render = () => {
            if (pageRouter.currentPage === "home") {
                this.text.textContent = `Welcome back, ${name}!`
                this.back.style.visibility = "hidden"
            } else if (pageRouter.currentPage === "account") {
                this.text.textContent = `Account n. ${currentAccountID}`
                this.back.style.visibility = "visible"
            }
        }
    }

    function HomePage() {
        this.populateAccountList = () => {
            makeGetRequest("accounts").then((response) => {
                if (response.ok && response.status === 200) {
                    response.json().then(json => {
                        let accountList = document.getElementById("account-list")
                        accountList.innerHTML = ""

                        json.accounts.forEach((account) => {
                            let row = document.createElement("div")
                            row.classList.add("detailRow")

                            let code = document.createElement("p")
                            code.textContent = account
                            row.append(code)

                            let button = document.createElement("a")
                            button.classList.add("button")
                            button.textContent = "Details"
                            button.addEventListener("click", () => {
                                sessionStorage.setItem("currentAccount", account)
                                pageRouter.goToPage("account")
                            })
                            row.append(button)

                            accountList.append(row)
                        })
                    })
                }
            })
        }

        this.render = () => {
            root.innerHTML = `<div class="container container-home">
                <div class="content">
                    <h2>Your accounts</h2>
                    <div id="account-list">
                        <div class="detailRow">
                            <p>Loading your accounts...</p>
                        </div>
                    </div>
                </div>
                
                <div id="hl"></div>
                
                <div class="content">
                    <h2>Open a new account</h2>
                    <form>
                        <label>Initial balance:
                            <input type="number" step="1" min="0" name="balance" required>
                        </label><br>
                        <input class="sendButton" type="submit" value="Open">
                        <p id="account-create-error"></p>
                    </form>
                </div>
            </div>`

            this.populateAccountList()
        }
    }

    function AccountDetailsPage() {
    }

    /*
    buttons: [
        {
            text: buttonText,
            onclick: function
        },
    ]
    */
    function Modal(child, buttons) {
        this.modalOuter = null

        this.close = () => {
            if (this.modalOuter !== null)
                this.modalOuter.remove()
        }

        this.render = () => {
            this.modalOuter = document.createElement("div")
            this.modalOuter.classList.add("modal")
            root.append(this.modalOuter)

            let modalContent = document.createElement("div")
            modalContent.classList.add("content", "centered")
            this.modalOuter.append(modalContent)

            modalContent.innerHTML = child

            let modalButtons = document.createElement("div")
            modalButtons.classList.add("buttons")
            for (let button of buttons) {
                let buttonElement = document.createElement("a")
                buttonElement.classList.add("button")
                buttonElement.textContent = button.text
                buttonElement.addEventListener("click", button.onclick)
                modalButtons.append(buttonElement)
            }
        }
    }

    function TransferModal(details) {
        return `<div class="upperWrapper">
                <h1>Transfer n. ${details.transferCode}</h1>
                <div class="transfer-details">
                    <p>${details.transferCode}</p>
                    <p>$ ${details.amount}</p>
                    <p>${details.reason}</p>
                    <p>${details.date}</p>
                </div>
            </div>
            <div class="lowerWrapper">
                <div class="sender-details">
                    <h2>Sender</h2>
                    <p>User code ${details.senderUserCode}</p>
                    <p>Account Code ${details.senderAccountCode}</p>
                    <p>Balance before transfer ${details.senderAccountBalanceBefore}</p>
                    <p>Balance after transfer ${details.senderAccountBalanceAfter}</p>
                </div>
                <div id="vl"></div>
                <div class="recipient-details">
                    <h2>Recipient</h2>
                    <p>User code ${details.recipientUserCode}</p>
                    <p>Account code ${details.recipientAccountCode}</p>
                    <p>Balance before transfer ${details.recipientAccountBalanceBefore}</p>
                    <p>Balance after transfer ${details.recipientAccountBalanceAfter}</p>
                </div>
            </div>`
    }

    function ErrorModal(message) {
        return `<h1>The requested transfer could not be performed</h1>
            <p>${message}</p>`
    }

})()
