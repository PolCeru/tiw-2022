package it.polimi.tiw.project4.schemas;

import it.polimi.tiw.project4.beans.Transfer;

import java.util.List;

public class TransfersResponse {
    private List<Transfer> transfers;

    public TransfersResponse(List<Transfer> transfers) {
        this.transfers = transfers;
    }

    public List<Transfer> getTransfers() {
        return transfers;
    }
}
