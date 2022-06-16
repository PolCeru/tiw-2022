package it.polimi.tiw.project4.controllers;

public enum TransferErrorMessage {
    NON_EXISTANT_RECIPIENT("The user you are trying to transfer to doesn't exist."),
    NON_EXISTANT_RECIPIENT_ACCOUNT("The account you are trying to transfer to doesn't exist."),
    RECIPIENT_SAME_AS_SENDER("You can't transfer money to the same account you're transferring from."),
    NEGATIVE_AMOUNT("The amount you're trying to transfer is negative."),
    INSUFFICIENT_FUNDS("You don't have enough money to make this transfer.");


    public final String message;

    TransferErrorMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }

    public static TransferErrorMessage parseCode(int code) throws IllegalArgumentException {
        var values = TransferErrorMessage.values();
        if (code < 0 || code >= values.length)
            throw new IllegalArgumentException("Invalid transfer error code: " + code);

        return values[code];
    }
}
