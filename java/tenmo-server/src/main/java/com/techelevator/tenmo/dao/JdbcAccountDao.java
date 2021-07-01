package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    public boolean updateBalance(int userID, BigDecimal amountToAdd) {
        int rows = 0;
        String sql = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?";
        try {
            rows = jdbcTemplate.update(sql, amountToAdd, userID);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return rows == 1;
    }

    @Override
    public boolean updateBalanceByAccountId(int acctID, BigDecimal amountToAdd) {
        int rows = 0;
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        try {
            rows = jdbcTemplate.update(sql, amountToAdd, acctID);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return rows == 1;
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
        account.setAccountId(rowSet.getLong("account_id"));
        account.setUserId(rowSet.getLong("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));
        return account;
    }
}
