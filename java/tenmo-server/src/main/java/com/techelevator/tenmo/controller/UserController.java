package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/users")
@PreAuthorize("isAuthenticated()")
public class UserController {

    private UserDao userDao;
    private AccountDao accountDao;

    public UserController(UserDao userDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<User> getAll() {
        return userDao.findAll();
    }

}
