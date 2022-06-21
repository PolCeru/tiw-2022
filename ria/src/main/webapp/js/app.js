(function () {
    /**
     * The html element where pages are rendered.
     * @type {HTMLElement}
     */
    let root = document.getElementById("page")

    /**
     * The global page router.
     * @type {PageRouter}
     */
    let pageRouter = new PageRouter()
    window.addEventListener("load", () => {
        pageRouter.start()
    })

    /**
     * The PageRouter clas handles page routing and rendering.
     * @constructor
     */
    function PageRouter() {
        this.header = new Header()

        let resolvePage = (page) => {
            switch (page) {
                case "account":
                    return new AccountDetailsPage()
                case "home":
                default:
                    return new HomePage()
            }
        }

        this.start = () => {
            let savedPage = sessionStorage.getItem("currentPage")
            this.previousPage = sessionStorage.getItem("previousPage")
            this.goToPage(savedPage ? savedPage : "home")
        }

        this.goToPage = (destinationPage) => {
            this.activePage = resolvePage(destinationPage)
            sessionStorage.setItem("previousPage", this.previousPage)
            this.previousPage = this.currentPage
            this.currentPage = destinationPage
            sessionStorage.setItem("currentPage", this.currentPage)
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

    /**
     * The Header class handles the rendering of the header.
     * @constructor
     */
    function Header() {
        let back = document.getElementById("back")
        back.addEventListener("click", () => {
            pageRouter.goBack()
        })

        document.getElementById("logout").addEventListener("click", () => {
            makeLogoutRequest()
                .then(response => {
                    if (response.ok && response.redirected) {
                        sessionStorage.clear()
                        window.location.href = response.url
                    }
                })
        })

        /**
         * The header text.
         * @type {HTMLElement}
         */
        this.text = document.getElementById("header-text")

        /**
         * Renders the header.
         */
        this.render = () => {
            if (pageRouter.currentPage === "home") {
                this.text.textContent = `Welcome back, ${sessionStorage.getItem("name")}!`
                back.style.visibility = "hidden"
            } else if (pageRouter.currentPage === "account") {
                this.text.textContent = `Account n. ${sessionStorage.getItem("currentAccount")}`
                back.style.visibility = "visible"
            }
        }
    }

    /**
     * The HomePage class handles the rendering of the home page.
     * @constructor
     */
    function HomePage() {
        /**
         * Loads the current user's account list and displays it.
         */
        this.loadAccountList = () => {
            makeGetRequest("account")
                .then(response => {
                    if (!response.ok) {
                        throw response
                    }
                    return response.json()
                })
                .then(json => {
                    this.renderAccountList(json.accounts)
                })
                .catch(async response => {
                    let error = (await response.json()).error
                    let accountList = document.getElementById("account-list")
                    accountList.innerHTML =
                        `<div class="detailRow">
                            <p class="error">${error}</p>
                        </div>`
                })
        }

        /**
         * Renders the account list.
         * @param {string[]} accounts - the list of account codes
         */
        this.renderAccountList = (accounts) => {
            let accountList = document.getElementById("account-list")
            accountList.innerHTML = ""

            accounts.forEach(account => {
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
        }

        /**
         * Renders the home page.
         */
        this.render = () => {
            root.innerHTML =
                `<div class="container container-home">
                    <div class="content">
                        <h2>Your accounts</h2>
                        <div id="account-list">
                            <div class="detailRow">
                                <p>Loading your accounts...</p>
                            </div>
                        </div>
                    </div>
                    <div class="content">
                        <h2>Open a new account</h2>
                        <form id="open-account">
                            <label>Initial balance:
                                <input type="number" step="0.01" min="0.01" name="balance" required>
                            </label><br>
                            <input class="sendButton" type="submit" value="Open">
                            <p id="account-create-error" class="error"></p>
                        </form>
                    </div>
                </div>`

            let form = document.getElementById("open-account")
            form.addEventListener("submit", (event) => {
                event.preventDefault()
                if (form.checkValidity()) {
                    makeFormRequest("account/open", form)
                        .then(async response => {
                            if (!response.ok)
                                throw response

                            document.getElementById("account-create-error").textContent = ""
                            this.loadAccountList()
                        })
                        .catch(async response => {
                            document.getElementById("account-create-error").textContent = (await response.json()).error
                        })
                } else {
                    form.reportValidity()
                }
            })
            this.loadAccountList()
        }
    }

    /**
     * The AccountDetailsPage class handles the rendering of the account details page.
     * @constructor
     */
    function AccountDetailsPage() {
        this.accountBook = new AccountBook().entries

        let userIDs = []
        let savedRecipientAccountIDs = []
        this.accountBook.then(entries => {
            entries.forEach(entry => {
                userIDs.push(entry.userID)
                savedRecipientAccountIDs.push(entry.savedCode)
            })
        })

        /**
         * Loads the current account info from the server and displays it.
         */
        this.loadAccountInfo = () => {
            makeGetRequestParams("get_account", {id: sessionStorage.getItem("currentAccount")})
                .then(async response => {
                    if (!response.ok)
                        throw response
                    return response.json()
                })
                .then(json => this.renderAccountInfo(json))
                .catch(async response => {
                    let error = (await response.json()).error
                    document.getElementById("account-info").innerHTML = `<p class="error">${error}</p>`
                })
        }

        /**
         * Loads the transfer list from the server and displays it.
         */
        this.loadTransferList = () => {
            makeGetRequestParams("transfer", {account: sessionStorage.getItem("currentAccount")})
                .then(async response => {
                    if (!response.ok)
                        throw response
                    return response.json()
                })
                .then(json => this.renderTransferList(json.transfers))
                .catch(async response => {
                    let error = (await response.json()).error
                    document.getElementById("transfer-container").innerHTML = `<p class="error">${error}</p>`
                })
        }

        /**
         * Registers the listener for the submit event of the transfer form.
         */
        this.registerTransferForm = () => {
            document.getElementById("senderAccountCode").value = sessionStorage.getItem("currentAccount")
            let recipientID_inputField = document.getElementsByName("recipientID")[0]
            let recipient_Autocomplete = new Autocomplete(recipientID_inputField, savedRecipientAccountIDs)
            let transferForm = document.getElementById("transfer-form")
            transferForm.addEventListener("submit", (event) => {
                event.preventDefault()
                if (transferForm.checkValidity()) {
                    makeFormRequest("do_transfer", transferForm)
                        .then(response => {
                            if (!response.ok)
                                throw response
                            return response.json()
                        })
                        .then(json => {
                            const buttons = [
                                {
                                    text: "Add to account book",
                                    onClick: (button) => {
                                        this.showAddToBookModal(button, transferForm.recipientAccountCode.value)
                                    }
                                }
                            ]
                            let transferModal = new Modal(TransferModal(json), buttons, undefined, this.loadTransferList)
                            transferModal.show()
                        })
                        .catch(async response => {
                            let errorModal = new Modal(ErrorModal((await response.json()).error()))
                            errorModal.show()
                        })
                } else {
                    transferForm.reportValidity()
                }
            })

            //TODO: Check client side account book and in case disable the button
            /**
             * Shows the AddToBook modal when the user clicks the add to book button.
             * @param {HTMLElement} button - the button that was clicked
             * @param {number} accountCode - the account code to be saved
             */
            this.showAddToBookModal = (button, accountCode) => {
                new Modal(AddToBookModal(accountCode),
                    undefined,
                    () => {
                        let nameForm = document.getElementById("account-name")
                        nameForm.addEventListener("submit", (event) => {
                            event.preventDefault()
                            if (nameForm.checkValidity()) {
                                makeFormRequest("add_to_book", nameForm)
                                    .then(response => {
                                        if (!response.ok)
                                            throw response
                                        let msg = document.getElementById("add-to-book-message")
                                        msg.className = ""
                                        msg.textContent = "Account added successfully"
                                    })
                                    .catch(async response => {
                                        console.log(response)
                                        let msg = document.getElementById("add-to-book-message")
                                        msg.classList.add("error")
                                        msg.textContent = (await response.json()).error
                                    })
                            } else
                                nameForm.reportValidity()
                        })
                    })
                    .show()
            }
        }

        /**
         * Renders the account info.
         * @param {{code: number, balance: number}} account - the account info
         */
        this.renderAccountInfo = (account) => {
            let formattedBalance = account.balance.toLocaleString(
                undefined,
                {minimumFractionDigits: 2}
            )
            document.getElementById("account-info").innerHTML =
                `<div class="detailRow">
                    <p>Code:</p>
                    <p>${account.code}</p>
                </div>
                <div class="detailRow">
                    <p>Balance: </p>
                    <p class="account-balance">$${formattedBalance}</p>
                </div>`
        }

        /**
         * A transfer object.
         * @typedef {{transferID: number, date: string, amount: number, sender: number, recipient: sender, reason: string}} Transfer
         */
        /**
         * Renders the given list of transfers to a table
         * @param {Transfer[]} transfers
         */
        this.renderTransferList = (transfers) => {
            let table = document.createElement("table")
            table.id = "transfer-list"
            table.classList.add("transferTable")
            table.innerHTML +=
                `<thead>
                    <tr>
                        <td>ID</td>
                        <td>Date</td>
                        <td>Amount</td>
                        <td>Sender</td>
                        <td>Recipient</td>
                        <td>Reason</td>
                    </tr>
                </thead>`

            let tbody = document.createElement("tbody")
            transfers.forEach(transfer => {
                let row = document.createElement("tr")
                let formattedAmount = transfer.amount.toLocaleString(
                    undefined,
                    {minimumFractionDigits: 2}
                )
                row.innerHTML +=
                    `<td>${transfer.transferID}</td>
                    <td>${new Date(transfer.date).toLocaleDateString()}</td>
                    <td>$${formattedAmount}</td>
                    <td>${transfer.sender}</td>
                    <td>${transfer.recipient}</td>
                    <td>${transfer.reason}</td>`

                tbody.append(row)
            })
            table.append(tbody)

            let transferContainer = document.getElementById("transfer-container")
            transferContainer.innerHTML = ""
            transferContainer.append(table)
        }

        /**
         * Renders the account details page.
         */
        this.render = () => {
            root.innerHTML =
                `<div class="container container-account">
                    <div class="leftWrapper">
                        <div class="content">
                            <h2>Account details</h2>
                            <div id="account-info"></div>
                        </div>
                        <div class="content">
                            <h2>New transfer</h2>
                            <form id="transfer-form" autocomplete="off">
                                <input id="senderAccountCode" name="senderAccountCode" type="hidden">
                                <label style="position: relative">Recipient user code:
                                    <input type="text" name="recipientID" value="1" required>
                                </label><br>
                                <label>Recipient account code:
                                    <input type="text" name="recipientAccountCode" value="1" required>
                                </label><br>
                                <label>Reason:
                                    <input type="text" name="reason" value="test" required>
                                </label><br>
                                <label>Transfer amount:
                                    <input type="number" step="0.01" min="0.01" name="amount" value="1" required>
                                </label><br>
                                <input class="sendButton" type="submit" value="Transfer">
                            </form>
                        </div>
                    </div>
                    <div id="vl"></div>
                    <div class="rightWrapper">
                        <div class="content transfer-list">
                            <h2>Transfer List</h2>
                            <div id="transfer-container"></div>
                        </div>
                    </div>
                </div>`

            this.loadAccountInfo()
            this.loadTransferList()
            this.registerTransferForm()
        }
    }

    /**
     * An account book entry.
     * @typedef {{userID: number, savedCode: number, name: string}} AccountBookEntry
     */
    /**
     * Handles the retrieval of the account book.
     * @constructor
     */
    function AccountBook() {
        /**
         * @type {Promise<AccountBookEntry[]>}
         */
        this.entries = new Promise(async resolve => {
            await makeGetRequest("account_book")
                .then(response => {
                    if (!response.ok)
                        throw response
                    return response.json()
                })
                .then(json => {
                    resolve(json.bookEntries)
                })
                .catch(async response => {
                    new Modal(ErrorModal((await response.json()).error())).show()
                })
        })
    }

    /**
     * Handles the autocomplete feature on the given input field.
     * @param {HTMLInputElement} textField - the text field to watch
     * @param {String[]} entries - the entries where the autocomplete will search for suggestions
     * @constructor
     */
    function Autocomplete(textField, entries) {
        document.body.addEventListener("click", () => {
            this.close()
        })
        textField.addEventListener("input", () => {
            this.close()
            let fieldValue = textField.value
            if (!fieldValue) return false
            this.listDiv = document.createElement("div")
            this.listDiv.setAttribute("class", "autocomplete-list")

            entries.forEach(pEntry => {
                let entry = pEntry.toString()
                if (entry.substring(0, fieldValue.length).toLowerCase() === fieldValue.toLowerCase()) {
                    let entryDiv = document.createElement("div")
                    entryDiv.classList.add("autocomplete-item")
                    entryDiv.innerHTML = `<span>${entry.substring(0, fieldValue.length)}</span>`
                    entryDiv.innerHTML += `<span style="color: #a1a1a1">${entry.substring(fieldValue.length) + "ciao"}</span>`
                    entryDiv.innerHTML += `<input type='hidden' value="${entry}">`

                    entryDiv.addEventListener("click", () => {
                        textField.value = entryDiv.getElementsByTagName("input")[0].value
                        this.close()
                    })
                    this.listDiv.append(entryDiv)
                }
            })

            if (this.listDiv.children.length > 0)
                textField.parentNode.append(this.listDiv)
        })

        /**
         * Closes the autocomplete panel.
         */
        this.close = () => {
            if (this.listDiv)
                this.listDiv.remove()
        }
    }

    /**
     * The callback that is called when one of the modal buttons are clicked.
     * @callback onClick
     * @param {HTMLElement} button
     */
    /**
     * A modal that is drawn on top of other elements on the page.
     * @param {string} child - the modal content as html string
     * @param {{text: string, onClick: onClick}[]} [buttons] - the buttons to be displayed
     * @param {*} [onShow] - callback function called after the modal is shown
     * @param {*} [onClose] - callback function called after the modal is closed
     * @constructor
     */
    function Modal(child, buttons, onShow, onClose) {
        /**
         * Closes this modal.
         */
        this.close = () => {
            if (this.modalOuter)
                this.modalOuter.remove()
            // Call the supplied onClose callback if there is one
            if (onClose)
                onClose()
        }

        /**
         * Shows this modal.
         */
        this.show = () => {
            this.modalOuter = document.createElement("div")
            this.modalOuter.classList.add("modal")
            root.append(this.modalOuter)

            let modalContent = document.createElement("div")
            modalContent.classList.add("content", "centered")
            this.modalOuter.append(modalContent)

            modalContent.innerHTML = child

            let modalButtons = document.createElement("div")
            modalButtons.classList.add("modal-buttons")
            if (buttons != null) {
                buttons.forEach(button => {
                    let buttonElement = document.createElement("a")
                    buttonElement.classList.add("button")
                    buttonElement.textContent = button.text
                    buttonElement.addEventListener("click", () => {
                        button.onClick(buttonElement)
                    })
                    modalButtons.append(buttonElement)
                })
            }
            // Always add the close button
            let buttonElement = document.createElement("a")
            buttonElement.classList.add("button")
            buttonElement.textContent = "Close"
            buttonElement.addEventListener("click", this.close)
            modalButtons.append(buttonElement)
            modalContent.append(modalButtons)

            // Call the supplied onShow callback if there is one
            if (onShow)
                onShow()
        }
    }

    /**
     * @typedef {{
     *      transferCode: number,
     *      amount: number,
     *      reason: string,
     *      date: string,
     *      senderUserCode: number,
     *      senderAccountCode: number,
     *      senderBalanceBefore: number,
     *      senderBalanceAfter: number,
     *      recipientUserCode: number,
     *      recipientAccountCode: number,
     *      recipientBalanceBefore: number,
     *      recipientBalanceAfter: number
     * }} TransferDetails
     */
    /**
     * The transfer result modal html body.
     * @param {TransferDetails} details - the details of the transfer
     * @returns {string} - the body of the modal that shows the transfer result, as html string
     */
    function TransferModal(details) {
        return (
            `<div class="upperWrapper">
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
                    <p>Balance before transfer ${details.senderBalanceBefore}</p>
                    <p>Balance after transfer ${details.senderBalanceAfter}</p>
                </div>
                <div id="vl"></div>
                <div class="recipient-details">
                    <h2>Recipient</h2>
                    <p>User code ${details.recipientUserCode}</p>
                    <p>Account code ${details.recipientAccountCode}</p>
                    <p>Balance before transfer ${details.recipientBalanceBefore}</p>
                    <p>Balance after transfer ${details.recipientBalanceAfter}</p>
                </div>
            </div>`
        )
    }

    /**
     * The error modal html body.
     * @param {string} message - the error message
     * @returns {string} - the body of the modal that shows an error message, as html string
     */
    function ErrorModal(message) {
        return (
            `<h1>The requested transfer could not be performed</h1>
            <p>${message}</p>`
        )
    }

    /**
     * The AddToBook modal html body.
     * @param {number} savedCode - the code of the account to add
     * @returns {string} - the body of the modal that shows the add to book functionality, as html string
     */
    function AddToBookModal(savedCode) {
        return (
            `<h2> Add to account book </h2>
            <form id="account-name">
                <label>Enter a custom name:
                    <input type="text" name="name" required>
                </label>
                    <input type="hidden" name="code" value="${savedCode}">
                    <input type="submit" class="sendButton" value="Save">
            </form>
            <p id="add-to-book-message"></p>`
        )
    }
})()
