package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.TransferDTO;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    TransferDTO findTransfersByUserId(int userId);

    public boolean create(TransferDTO transfer);

    public List<TransferDTO> getTransfersByAccountId(int acctID);
}
