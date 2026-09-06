package service;

import dao.AccountDAO;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;

public class Bank {
    // Delegate all storage operations to the DAO
    private AccountDAO accountDAO;

    public Bank() {
        this.accountDAO = new AccountDAO();
    }

    public String createSavingsAccount(String accNum, String name, double initialBalance) {
        if (initialBalance < 0) {
            String msg = "Error: Initial balance cannot be negative.";
            System.out.println(msg);
            return msg;
        }
        if (accountDAO.findByAccountNumber(accNum) != null) {
            String msg = "Error: Account " + accNum + " already exists.";
            System.out.println(msg);
            return msg;
        }
        Account account = new SavingsAccount(accNum, name, initialBalance);
        if (accountDAO.save(account, "SAVINGS")) {
            String msg = "Savings Account created successfully.";
            System.out.println(msg);
            return msg;
        }
        String msg = "Error saving account to database.";
        System.out.println(msg);
        return msg;
    }

    public String createCurrentAccount(String accNum, String name, double initialBalance) {
        if (initialBalance < 0) {
            String msg = "Error: Initial balance cannot be negative.";
            System.out.println(msg);
            return msg;
        }
        if (accountDAO.findByAccountNumber(accNum) != null) {
            String msg = "Error: Account " + accNum + " already exists.";
            System.out.println(msg);
            return msg;
        }
        Account account = new CurrentAccount(accNum, name, initialBalance);
        if (accountDAO.save(account, "CURRENT")) {
            String msg = "Current Account created successfully.";
            System.out.println(msg);
            return msg;
        }
        String msg = "Error saving account to database.";
        System.out.println(msg);
        return msg;
    }

    public String deposit(String accNum, double amount) throws InvalidAmountException {
        Account account = getAccount(accNum);
        if (account != null) {
            account.deposit(amount);
            // Persist the updated balance back to PostgreSQL
            accountDAO.updateBalance(account);
            String msg = "Successfully deposited $" + amount + ". New balance: $" + account.getBalance();
            System.out.println(msg);
            return msg;
        }
        throw new InvalidAmountException("Account " + accNum + " not found.");
    }

    public String withdraw(String accNum, double amount) throws InvalidAmountException, InsufficientFundsException {
        Account account = getAccount(accNum);
        if (account != null) {
            account.withdraw(amount);
            // Persist the updated balance back to PostgreSQL
            accountDAO.updateBalance(account);
            String msg = "Successfully withdrew $" + amount + ". Remaining balance: $" + account.getBalance();
            System.out.println(msg);
            return msg;
        }
        throw new InvalidAmountException("Account " + accNum + " not found.");
    }

    public String transfer(String fromAccNum, String toAccNum, double amount) throws InvalidAmountException, InsufficientFundsException {
        Account fromAccount = getAccount(fromAccNum);
        if (fromAccount == null) {
            throw new InvalidAmountException("Source account " + fromAccNum + " not found.");
        }
        Account toAccount = getAccount(toAccNum);
        if (toAccount == null) {
            throw new InvalidAmountException("Destination account " + toAccNum + " not found.");
        }

        // Withdraw and deposit in memory first
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
        
        // Update both accounts in the database
        accountDAO.updateBalance(fromAccount);
        accountDAO.updateBalance(toAccount);
        String msg = "Successfully transferred $" + amount + " from " + fromAccNum + " to " + toAccNum;
        System.out.println(msg);
        return msg;
    }

    public String printStatement(String accNum) {
        Account account = getAccount(accNum);
        if (account != null) {
            String stmt = account.toString();
            System.out.println(stmt);
            return stmt;
        }
        String msg = "Error: Account " + accNum + " not found.";
        System.out.println(msg);
        return msg;
    }

    public Account getAccount(String accNum) {
        Account account = accountDAO.findByAccountNumber(accNum);
        if (account == null) {
            System.out.println("Error: Account " + accNum + " not found.");
        }
        return account;
    }
}