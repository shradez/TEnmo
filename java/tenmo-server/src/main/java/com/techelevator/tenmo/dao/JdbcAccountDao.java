package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Account> findAll() {
        return null;
    }

    @Override
    public Long findAccountIDByUserId(Long userID) {
        String sql = "SELECT account_id FROM accounts WHERE user_id = ?;";
        Long acctId = jdbcTemplate.queryForObject(sql, Long.class, userID);
        try {
            return acctId;
        } catch (NullPointerException ex) {
            System.err.println("Account ID can't be found" + ex.getMessage());
        }
        return null;
    }

    @Override
    public BigDecimal findBalanceByAccountID(Long accountId) {
        String sql = "SELECT balance FROM accounts WHERE account_id = ?;";
        BigDecimal balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, accountId);
        try {
            return balance;
        } catch (NullPointerException ex) {
            System.err.println("Balance can't be found" + ex.getMessage());
        }
        return null;
    }

/*    @Override
    public Long findAccountIDByUserId(Long userID) {
        return null;
    }
 */

}
