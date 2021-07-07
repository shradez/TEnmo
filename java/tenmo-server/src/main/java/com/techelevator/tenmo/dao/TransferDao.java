package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferDTO;
import java.util.List;

// Ask Katie to go over the necessity of having these interfaces, does it just provide an extra layer of separation between the models and methods?

public interface TransferDao {

    public boolean create(TransferDTO transfer);

    public List<TransferDTO> getTransfersByAccountId(int acctId);

    public TransferDTO getTransferByTransferId(int transferId);

    public List<TransferDTO> getPendingTransfersByAccountId(int acctId);

    public TransferDTO approve(int transferId);

    public TransferDTO reject(int transferId);
}
