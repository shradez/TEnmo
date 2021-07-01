package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.TransferDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/transfers")
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private AccountDao accountDao;
    private UserDao userDao;
    private TransferDao transferDao;

    private List<TransferDTO> transfers = new ArrayList<>();

    public TransferController(AccountDao accountDao, UserDao userDao, TransferDao transferDao) {
        this.accountDao =accountDao;
        this.userDao = userDao;
        this.transferDao = transferDao;
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public void create(@RequestBody TransferDTO transfer) {
        transfers.add(transfer);
        transferDao.create(transfer);

    }
}
