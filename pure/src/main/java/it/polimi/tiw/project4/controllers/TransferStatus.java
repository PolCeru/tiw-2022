package it.polimi.tiw.project4.controllers;

public enum TransferStatus {
    SUCCESS,
    ERROR;

    public static TransferStatus parseStatus(String status) throws IllegalArgumentException {
        return TransferStatus.valueOf(status.toUpperCase());
    }
}
