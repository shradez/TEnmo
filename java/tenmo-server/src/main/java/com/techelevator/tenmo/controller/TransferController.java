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

    public TransferController(AccountDao accountDao, UserDao userDao, TransferDao transferDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.transferDao = transferDao;
    }

    @RequestMapping(path = "/send", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public void send(@RequestBody TransferDTO transfer, Principal principal) throws Exception {
        String nameFrom = principal.getName();
        int userIdFrom = userDao.findIdByUsername(nameFrom);
        BigDecimal amountSubtractedFromPrincipalUser = transfer.getAmount();
        Account accountOfUserFrom = accountDao.findAccountByUserId(userIdFrom);
        if (!(amountSubtractedFromPrincipalUser.compareTo(accountOfUserFrom.getBalance()) > 0) && (transfer.getAmount().compareTo(BigDecimal.ZERO) > 0)) {
            transfer.setStatusId(2); // This sets to approved
            transfer.setTypeId(2); // This sets to send
            transferDao.create(transfer);
            //subtract from Principal User balance
            accountDao.updateBalance(userIdFrom, BigDecimal.valueOf(-1).multiply(amountSubtractedFromPrincipalUser));
            // add to balance principal is sending to
            int acctIdTo = transfer.getAccountIdTo();
            BigDecimal amountAddedToChosenUser = transfer.getAmount();
            accountDao.updateBalanceByAccountId(acctIdTo, amountAddedToChosenUser);
        } else { //ensure there is enough money in account and that amount is more than zero / not negative with thrown exception
            throw new Exception("Sorry, you need to enter an amount greater than zero and ensure you have enough funds in your account.");
        }

    }

   @RequestMapping(path = "/request", method = RequestMethod.POST)
   @ResponseStatus(value = HttpStatus.CREATED)
   public void request(@RequestBody TransferDTO transfer, Principal principal) {
        String nameRequester = principal.getName();
        int userIdRequester = userDao.findIdByUsername(nameRequester);
        transfer.setStatusId(1);
        transfer.setTypeId(1);
        transferDao.create(transfer);
   }

    @RequestMapping(path = "/getforuser", method = RequestMethod.GET)
    public List<TransferDTO> getTransfersByUserId(Principal principal) {
        String name = principal.getName();
        int userId = userDao.findIdByUsername(name);
        Account account = accountDao.findAccountByUserId(userId);
        int acctId = account.getAccountId();
        return transferDao.getTransfersByAccountId(acctId);
    }

    @RequestMapping(path = "/getpending", method = RequestMethod.GET)
    public List<TransferDTO> getPendingTransfers(Principal principal) {
        String name = principal.getName();
        int userId = userDao.findIdByUsername(name);
        Account account = accountDao.findAccountByUserId(userId);
        int acctId = account.getAccountId();
        return transferDao.getPendingTransfersByAccountId(acctId);
    }

    @RequestMapping(path = "/{transferId}", method = RequestMethod.GET)
    public TransferDTO getTransferByTransferId(@PathVariable int transferId) {
        return transferDao.getTransferByTransferId(transferId);
    }

    @RequestMapping(path = "/approve", method = RequestMethod.PUT)
    public TransferDTO approve(@RequestBody TransferDTO transfer) throws Exception {
        if (transfer.getStatusId() == 1) {
            accountDao.updateBalanceByAccountId(transfer.getAccountIdTo(), transfer.getAmount().multiply(BigDecimal.valueOf(-1)));
            accountDao.updateBalanceByAccountId(transfer.getAccountIdFrom(), transfer.getAmount());
            return transferDao.approve(transfer.getTransferId());
        } else {
            throw new Exception("Sorry, this request is not pending approval or rejection.");
        }
    }

    @RequestMapping(path = "/reject", method = RequestMethod.PUT)
    public TransferDTO reject(@RequestBody TransferDTO transfer) throws Exception {
        if (transfer.getStatusId() == 1) {
            return transferDao.reject(transfer.getTransferId());
        } else {
            throw new Exception("Sorry, this request is not pending approval or rejection.");
        }
    }
}
