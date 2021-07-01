package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.TransferDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TransferDTO findTransfersByUserId(int userId) {
        return null;


    }

    @Override
    public boolean create(TransferDTO transfer) {
        int rows = 0;
        String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            rows = jdbcTemplate.update(sql, transfer.getTypeId(), transfer.getStatusId(), transfer.getAccountIdFrom(), transfer.getAccountIdTo(), transfer.getAmount());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return rows == 1;
    }

    @Override
    public List<TransferDTO> getTransfersByAccountId(int acctID) {
        String sql = "SELECT transfer_id, account_from, account_to, amount " +
                "FROM transfers WHERE account_from = ? OR account_to = ?";
        List<TransferDTO> transfers = new ArrayList<>();
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, acctID, acctID);
            while (results.next()) {
                transfers.add(mapRowToTransfer(results));
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return transfers;

    }

    private TransferDTO mapRowToTransfer(SqlRowSet rowSet) {
        TransferDTO transfer = new TransferDTO();
        transfer.setAccountIdFrom(rowSet.getInt("account_from"));
        transfer.setAccountIdTo(rowSet.getInt("account_to"));
        transfer.setAmount(rowSet.getBigDecimal("amount"));
        return transfer;
    }
}
