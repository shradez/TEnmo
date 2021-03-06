package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    BigDecimal findBalanceByAccountID(int userId);
    Account findAccountByUserId (int userID);
    boolean updateBalanceByUserID(int userID, BigDecimal amountToAdd);
    boolean updateBalanceByAccountId(int acctID, BigDecimal amountToAdd);
    int findAccountIdByUserId (int acctId);



}
