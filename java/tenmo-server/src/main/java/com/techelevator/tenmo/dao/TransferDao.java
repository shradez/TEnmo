package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferDTO;

public interface TransferDao {

    TransferDTO findTransfersByUserId(int userId);
}
