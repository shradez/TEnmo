package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;
import com.techelevator.view.ConsoleService;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
    private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
    private static final String[] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};
    private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
    private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
    private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
    private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
    private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountService accountService;
    private UserService userService;
    private TransferService transferService;

    public static void main(String[] args) {
        App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new AccountService(API_BASE_URL), new UserService(API_BASE_URL), new TransferService(API_BASE_URL));
        app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, AccountService accountService, UserService userService, TransferService transferService) {
        this.console = console;
        this.authenticationService = authenticationService;
        this.accountService = accountService;
        this.userService = userService;
        this.transferService = transferService;
    }

    public void run() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");

        registerAndLogin();
        mainMenu();
    }

    private void mainMenu() {
        while (true) {
            String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
                viewCurrentBalance();
            } else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
                viewTransferHistory();
            } else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
                viewPendingRequests();
            } else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
                sendBucks();
            } else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
                requestBucks();
            } else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else {
                // the only other option on the main menu is to exit
                exitProgram();
            }
        }
    }

    private void viewCurrentBalance() {
        String token = currentUser.getToken();
        Account a = accountService.getAccount(currentUser.getUser().getId(), token);
        BigDecimal balance = a.getBalance();
        System.out.format("Your current balance is: " + NumberFormat.getCurrencyInstance().format(balance) + "\n");

    }

    private void viewTransferHistory() {
        String token = currentUser.getToken();
        Transfer[] transfers = transferService.getUserTransferHistory(token);
        List<String> pastTransfers = new ArrayList<>();
        for (Transfer transfer : transfers) {
            if (transfer.getAccountIdFrom() == accountService.getAccountIdByUserId(currentUser.getUser().getId(), token)) {
                pastTransfers.add(transfer.getTransferId() + "       To: " + userService.getUsernameByAcctId(transfer.getAccountIdTo(), token) + "         " + transfer.getAmount());
            } else {
                pastTransfers.add(transfer.getTransferId() + "     From: " + userService.getUsernameByAcctId(transfer.getAccountIdFrom(), token) + "         " + transfer.getAmount());
            }

        }
        //ensure they have a transfer history
        if (pastTransfers.size() > 0) {
            System.out.println("---------------------------------");
            System.out.println("Transfers");
            System.out.println("ID         From/To         Amount");
            System.out.println("---------------------------------");
            for (String string : pastTransfers) {
                System.out.println(string);
            }
            System.out.println("--------");

            Integer choice = console.getUserInputInteger("Please enter transfer ID to view details (0 to cancel)");
            if (choice == 0) {
                System.out.println("\nView details cancelled.");
                // User gets sent back to main menu
            } else {
                int count = 0;
                for (Transfer transfer : transfers) {
                    String usernameFrom = userService.getUsernameByAcctId(transfer.getAccountIdFrom(), token);
                    String usernameTo = userService.getUsernameByAcctId(transfer.getAccountIdTo(), token);
                    String type = null;
                    String status = null;
                    if (choice == transfer.getTransferId()) {
                        if (transfer.getTypeId() == 1) type = "Request";
                        if (transfer.getTypeId() == 2) type = "Send";
                        if (transfer.getStatusId() == 1) status = "Pending";
                        if (transfer.getStatusId() == 2) status = "Approved";
                        if (transfer.getStatusId() == 3) status = "Rejected";
                        System.out.println("\n-------------------------\nTransfer Details\n-------------------------");
                        System.out.println("Id: " + transfer.getTransferId() + "\nFrom: " + usernameFrom + "\nTo: " + usernameTo + "\nType: " + type + "\nStatus: " + status + "\nAmount: " + NumberFormat.getCurrencyInstance().format(transfer.getAmount()));
                    } else {
                        count++;
                    }
                    if (count == transfers.length) {
                        // If count is same as length of list, we know we got to the end of the last iteration of the for loop without a match, therefore the user did not enter a valid transfer ID.
                        System.out.println("\nSorry, you did not enter a valid Transfer ID.");
                    }
                }
            }
            //if they do not have a transfer history...
        } else {
            System.out.println("You have no past transfers.");
        }
    }

    private void viewPendingRequests() {
        String token = currentUser.getToken();
        Transfer[] transfers = transferService.getUserTransferHistory(token);
        List<String> pendingRequests = new ArrayList<>();
        List<Transfer> pendingTransfers = new ArrayList<>();
        int count = 0;
        for (Transfer transfer : transfers) {
            if (transfer.getStatusId() == 1 && transfer.getAccountIdFrom() != accountService.getAccountIdByUserId(currentUser.getUser().getId(), token)) {
                pendingRequests.add(transfer.getTransferId() + "       " + userService.getUsernameByAcctId(transfer.getAccountIdFrom(), token) + "        " + transfer.getAmount());
                pendingTransfers.add(transfer);
            }
        }
        //ensure they have pending transactions
        if (pendingRequests.size() > 0) {
            System.out.println("---------------------------------");
            System.out.println("Transfers");
            System.out.println("ID         To         Amount");
            System.out.println("---------------------------------");
            for (String string : pendingRequests) {
                System.out.println(string);
            }
            System.out.println("--------");
            Integer choice = console.getUserInputInteger("Please enter transfer ID to approve/reject (0 to cancel)");
            if (choice == 0) {
                System.out.println("\nView details cancelled.");
                // User gets sent back to main menu
            } else {
//this moves on to use case 9
                for (Transfer transfer : pendingTransfers) {
                    if (choice == transfer.getTransferId()) {
                        System.out.println("1: Approve");
                        System.out.println("2: Reject");
                        System.out.println("0: Don't approve or reject");
                        System.out.println("--------");
                        Integer choice2 = console.getUserInputInteger("Please choose an option");
                        if (choice2 == 0) {
                            System.out.println("\n You did not approve or reject the pending transfer.");
                        } else if (choice2 == 1) {
                            transferService.approveTransferStatus(token, transfer);
                            System.out.println("\n Transfer approved.");
                        } else if (choice2 == 2) {
                            transferService.rejectTransferStatus(token, transfer);
                            System.out.println("\n Transfer rejected.");
                        } else {
                            System.out.println("\nSorry, you did not select a valid option.");
                        }
                    } else {
                        count++;
                    }
                    if (count == pendingTransfers.size()) {
                        System.out.println("\nSorry, You did not enter a valid transfer ID.");
                    }
                }
            }

//if they don't have pending transactions...
        } else {
            System.out.println("You have no pending requests.\n");
        }
    }

    private void sendBucks() {
        User userFrom = currentUser.getUser();
        User userTo = null;
        BigDecimal amountToTransferBD = null;
        String token = currentUser.getToken();
        User[] users = userService.getAll(token);
        System.out.println("-------------------------");
        System.out.println("Users");
        System.out.println("ID         Name");
        System.out.println("-------------------------");
        for (User user : users) {
            System.out.println(user.getId().toString() + "   |   " + user.getUsername().toString());
        }
        System.out.println("--------");
        Integer choice = console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel)");
        if (choice == 0) {
            System.out.println("\nTransfer cancelled.");
            // User gets sent back to main menu
        } else {
            int count = 0;
            for (User user : users) {
                if (choice.equals(currentUser.getUser().getId())) {
                    System.out.print("\nSorry, please choose a user ID other than yours. \n");
                    break;
                } else if (choice.equals(user.getId())) {
                    userTo = user; // If statement here to catch if user is trying to send to themself.
                    System.out.println("\nYou chose user: " + userTo.getUsername().toString());
                    // User gets sent back to main menu

                    // Prompt for amount, then save that to a variable
                    String amountToTransfer = console.getUserInput("Enter amount: ");
                    amountToTransferBD = new BigDecimal(amountToTransfer);
                    System.out.println(NumberFormat.getCurrencyInstance().format(amountToTransferBD));
                    int userFromAcctId = accountService.getAccountIdByUserId(userFrom.getId(), token);
                    int userToAcctId = accountService.getAccountIdByUserId(userTo.getId(), token);
                    transferService.createSend(token, userFromAcctId, userToAcctId, amountToTransferBD);
                    System.out.println("Transfer created!");
                } else {
                    count++;
                }
                if (count == users.length) {
                    System.out.println("\nSorry you did not enter a valid user ID.");
                }
            }
        }
    }


    private void requestBucks() {
        User userFrom = currentUser.getUser();
        User userTo = null;
        BigDecimal amountToRequestBD = null;
        String token = currentUser.getToken();
        User[] users = userService.getAll(token);
        System.out.println("-------------------------");
        System.out.println("Users");
        System.out.println("ID         Name");
        System.out.println("-------------------------");
        for (User user : users) {
            System.out.println(user.getId().toString() + "   |   " + user.getUsername().toString());
        }
        System.out.println("--------");
        Integer choice = console.getUserInputInteger("Enter ID of user you are requesting from (0 to cancel)");
        if (choice == 0) {
            System.out.println("\nTransfer cancelled.");
            // User gets sent back to main menu
        } else {
            int count = 0;
            for (User user : users) {
                if (choice.equals(currentUser.getUser().getId())) {
                    System.out.print("\nSorry, please choose a user ID other than yours. \n");
                    break;
                } else if (choice.equals(user.getId())) {
                    userTo = user; // If statement here to catch if user is trying to send to themself.
                    System.out.println("\nYou chose user: " + userTo.getUsername().toString());
                    // User gets sent back to main menu

                    // Prompt for amount, then save that to a variable
                    String amountToTransfer = console.getUserInput("Enter amount: ");
                    amountToRequestBD = new BigDecimal(amountToTransfer);
                    System.out.println(NumberFormat.getCurrencyInstance().format(amountToRequestBD));
                    int userFromAcctId = accountService.getAccountIdByUserId(userFrom.getId(), token);
                    int userToAcctId = accountService.getAccountIdByUserId(userTo.getId(), token);
                    transferService.createRequest(token, userFromAcctId, userToAcctId, amountToRequestBD);
                    System.out.println("Transfer created!");
                } else {
                    count++;
                }
                if (count == users.length) {
                    System.out.println("\nSorry you did not enter a valid user ID.");
                }
            }
        }
    }


    private void exitProgram() {
        System.exit(0);
    }

    private void registerAndLogin() {
        while (!isAuthenticated()) {
            String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
            if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
                register();
            } else {
                // the only other option on the login menu is to exit
                exitProgram();
            }
        }
    }

    private boolean isAuthenticated() {
        return currentUser != null;
    }

    private void register() {
        System.out.println("Please register a new user account");
        boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                authenticationService.register(credentials);
                isRegistered = true;
                System.out.println("Registration successful. You can now login.");
            } catch (AuthenticationServiceException e) {
                System.out.println("REGISTRATION ERROR: " + e.getMessage());
                System.out.println("Please attempt to register again.");
            }
        }
    }

    private void login() {
        System.out.println("Please log in");
        currentUser = null;
        while (currentUser == null) //will keep looping until user is logged in
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                currentUser = authenticationService.login(credentials);
            } catch (AuthenticationServiceException e) {
                System.out.println("LOGIN ERROR: " + e.getMessage());
                System.out.println("Please attempt to login again.");
            }
        }
    }

    private UserCredentials collectUserCredentials() {
        String username = console.getUserInput("Username");
        String password = console.getUserInput("Password");
        return new UserCredentials(username, password);
    }
}
