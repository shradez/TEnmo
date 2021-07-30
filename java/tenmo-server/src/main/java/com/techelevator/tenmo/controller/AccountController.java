package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/account")
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private AccountDao accountDao;
    private UserDao userDao;

    public AccountController(AccountDao accountDao, UserDao userDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public BigDecimal getBalanceByAccountId(Principal principal) {
        String name = principal.getName();
        int userId = userDao.findIdByUsername(name);
        return accountDao.findBalanceByAccountID(userId);
    }

    @RequestMapping(path = "", method = RequestMethod.GET) //
    public Account getAccount(Principal principal) throws AccountNotFoundException {
        String name = principal.getName();
        int userId = userDao.findIdByUsername(name);
        return accountDao.findAccountByUserId(userId);
    }

    @RequestMapping(path = "/id/{userId}", method = RequestMethod.GET)
    public int getAccountIdByUserId(@PathVariable int userId) {
        return accountDao.findAccountIdByUserId(userId);
    }
}
