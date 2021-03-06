package com.techelevator.tenmo.dao;

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
    public boolean create(TransferDTO transfer) {
        int rows = 0;
        String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?);";
        try {
            rows = jdbcTemplate.update(sql, transfer.getTypeId(), transfer.getStatusId(), transfer.getAccountIdFrom(), transfer.getAccountIdTo(), transfer.getAmount());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return rows == 1;
    }

    @Override
    public List<TransferDTO> getTransfersByAccountId(int acctId) {
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfers WHERE account_from = ? OR account_to = ?;";
        List<TransferDTO> transfers = new ArrayList<>();
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, acctId, acctId);
            while (results.next()) {
                transfers.add(mapRowToTransfer(results));
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return transfers;
    }

    @Override
    public TransferDTO getTransferByTransferId(int transferId) {
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfers WHERE transfer_id = ?;";
        TransferDTO t = null;
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
            if (results.next()) {
                t = mapRowToTransfer(results);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return t;
    }

    @Override
    public List<TransferDTO> getPendingTransfersByAccountId(int acctId) {
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfers WHERE (account_from = ? OR account_to = ?) AND transfer_status_id = 1;";
        List<TransferDTO> transfers = new ArrayList<>();
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, acctId, acctId);
            while (results.next()) {
                transfers.add(mapRowToTransfer(results));
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return transfers;
    }

    @Override
    public TransferDTO approve(int transferId) {
        String sql = "UPDATE transfers " +
                "SET transfer_status_id = 2 " +
                "WHERE transfer_id = ?;";
        try {
            jdbcTemplate.update(sql, transferId);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return getTransferByTransferId(transferId);
    }

    @Override
    public TransferDTO reject(int transferId) {
        String sql = "UPDATE transfers " +
                "SET transfer_status_id = 3 " +
                "WHERE transfer_id = ?;";
        try {
            jdbcTemplate.update(sql, transferId);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return getTransferByTransferId(transferId);
    }


    private TransferDTO mapRowToTransfer(SqlRowSet rowSet) {
        TransferDTO transfer = new TransferDTO();
        transfer.setTransferId(rowSet.getInt("transfer_id"));
        transfer.setTypeId(rowSet.getInt("transfer_type_id"));
        transfer.setStatusId(rowSet.getInt("transfer_status_id"));
        transfer.setAccountIdFrom(rowSet.getInt("account_from"));
        transfer.setAccountIdTo(rowSet.getInt("account_to"));
        transfer.setAmount(rowSet.getBigDecimal("amount"));
        return transfer;
    }
}
