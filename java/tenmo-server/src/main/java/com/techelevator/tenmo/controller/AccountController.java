package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private AccountDao accountDao;
    private UserDao userDao;

    public AccountController(AccountDao accountDao, UserDao userDao){
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public BigDecimal getBalanceByAccountId(Principal principal) {
        String name = principal.getName();
        int userId = userDao.findIdByUsername(name);
        return accountDao.findBalanceByAccountID(userId);
    }

    @RequestMapping(path = "/{userId}", method = RequestMethod.GET)
    public Account getAccountByUserId(@PathVariable int userId, Principal principal) throws AccountNotFoundException {
        String name = principal.getName();
        userId = userDao.findIdByUsername(name);
        return accountDao.findAccountByUserId(userId);
    }

    // Do they want us to be able to update balance as part of transfers from the API?
    //@RequestMapping(path = "/balance/update", method = RequestMethod.PUT)
    //public Account updateBalance(@RequestParam )
}
