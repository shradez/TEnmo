package com.techelevator.tenmo.model;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

public class TransferDTO {
    private int transferId;
    @NotEmpty
    private int typeId;
    @NotEmpty
    private int statusId;
    @NotEmpty
    private BigDecimal amount;
    @NotEmpty
    private int accountIdFrom;
    @NotEmpty
    private int accountIdTo;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getAccountIdFrom() {
        return accountIdFrom;
    }

    public void setAccountIdFrom(int accountIdFrom) {
        this.accountIdFrom = accountIdFrom;
    }

    public int getAccountIdTo() {
        return accountIdTo;
    }

    public void setAccountIdTo(int accountIdTo) {
        this.accountIdTo = accountIdTo;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int transferStatusId) {
        this.statusId = transferStatusId;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }
}
