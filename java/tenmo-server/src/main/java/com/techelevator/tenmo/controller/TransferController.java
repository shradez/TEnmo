package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.TransferDTO;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
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
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.transferDao = transferDao;
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public void create(@RequestBody TransferDTO transfer, Principal principal) throws Exception {
        String nameFrom = principal.getName();
        int userIdFrom = userDao.findIdByUsername(nameFrom);
        BigDecimal amountSubtractedFromPrincipalUser = transfer.getAmount();
        Account accountOfUserFrom = accountDao.findAccountByUserId(userIdFrom);
//TODO thorw exception if the if statement is false
        if (!(amountSubtractedFromPrincipalUser.compareTo(accountOfUserFrom.getBalance()) > 0) && (transfer.getAmount().compareTo(BigDecimal.ZERO) > 0)) {
            transfers.add(transfer);
            transferDao.create(transfer);
            transfer.setStatusId(2); // This sets to approved
            //subtract from Principal User balance
            //TODO put in a negative number updates balance and does not create a transfer.
            accountDao.updateBalance(userIdFrom, BigDecimal.valueOf(-1).multiply(amountSubtractedFromPrincipalUser));
            // add to balance principal is sending to
            int acctIdTo = transfer.getAccountIdTo();
            BigDecimal amountAddedToChosenUser = transfer.getAmount();
            accountDao.updateBalanceByAccountId(acctIdTo, amountAddedToChosenUser);
        } else { //ensure there is enough money in account and that amount is more than zero / not negative with thrown exception
            throw new Exception("Sorry, you need to enter an amount greater than zero and ensure you have enough funds in your account.");
        }

    }

    @RequestMapping(path = "/getforuser", method = RequestMethod.GET)
    public List<TransferDTO> getTransfersByUserId(Principal principal) {
        String name = principal.getName();
        int userId = userDao.findIdByUsername(name);
        Account account = accountDao.findAccountByUserId(userId);
        int acctId = account.getAccountId();
        return transferDao.getTransfersByAccountId(acctId);
    }
}
