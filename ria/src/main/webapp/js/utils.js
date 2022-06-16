function makeFormRequest(url, formElement, callback, reset = true) {
    let request = new XMLHttpRequest()
    request.onreadystatechange = function () {
        callback(request)
    }

    request.open("POST", url)

    if (formElement == null) {
        request.send()
    } else {
        request.send(new FormData(formElement))
    }

    if (formElement !== null && reset === true)
        formElement.reset()
}
