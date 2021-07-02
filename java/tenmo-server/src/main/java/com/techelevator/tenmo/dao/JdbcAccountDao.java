package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account findAccountByUserId(int userID) {
        String sql = "SELECT account_id, user_id, balance FROM accounts WHERE user_id = ?;";
        Account a = null;
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userID);
            if (results.next()) {
                a = mapRowToAccount(results);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return a;
    }

    @Override
    public boolean updateBalance(int userId, BigDecimal amountToAdd) {
        int rows = 0;
        String sql = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?";
        try {
            rows = jdbcTemplate.update(sql, amountToAdd, userId);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return rows == 1;
    }

    @Override
    public boolean updateBalanceByAccountId(int acctId, BigDecimal amountToAdd) {
        int rows = 0;
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        try {
            rows = jdbcTemplate.update(sql, amountToAdd, acctId);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return rows == 1;
    }

    @Override
    public int findAccountIdByUserId(int userId) {
        String sql = "SELECT account_id, user_id, balance FROM accounts WHERE user_id = ?;";
        Account a = null;
        int id = 0;
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
                a = mapRowToAccount(results);
                id = a.getAccountId();
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return id;
    }

    @Override
    public BigDecimal findBalanceByAccountID(int userId) {
        String sql = "SELECT SUM(balance) FROM accounts JOIN users ON users.user_id = accounts.user_id WHERE accounts.user_id = ?;";
        BigDecimal balance = BigDecimal.ZERO;
        try {
            balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return balance;
    }



    private Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getInt("account_id"));
        account.setUserId(rowSet.getLong("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));
        return account;
    }
}
