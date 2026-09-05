package service;

import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;

import java.util.HashMap;
import java.util.Map;

public class Bank {
    // Best Practice: Use the Interface (Map) as the reference type, 
    // and the concrete class (HashMap) for the object implementation.
    private Map<String, Account> accounts;

    public Bank() {
        this.accounts = new HashMap<>();
    }

    public void createSavingsAccount(String accNum, String name, double initialBalance) {
        if (accounts.containsKey(accNum)) {
            System.out.println("Error: Account " + accNum + " already exists.");
            return;
        }
        Account account = new SavingsAccount(accNum, name, initialBalance);
        accounts.put(accNum, account);
        System.out.println("Savings Account created successfully.");
    }

    public void createCurrentAccount(String accNum, String name, double initialBalance) {
        if (accounts.containsKey(accNum)) {
            System.out.println("Error: Account " + accNum + " already exists.");
            return;
        }
        Account account = new CurrentAccount(accNum, name, initialBalance);
        accounts.put(accNum, account);
        System.out.println("Current Account created successfully.");
    }

    // Notice we are passing the exceptions UP to whoever calls this method (the UI layer)
    public void deposit(String accNum, double amount) throws InvalidAmountException {
        Account account = getAccount(accNum);
        if (account != null) {
            account.deposit(amount);
            System.out.println("Successfully deposited $" + amount);
        }
    }

    public void withdraw(String accNum, double amount) throws InvalidAmountException, InsufficientFundsException {
        Account account = getAccount(accNum);
        if (account != null) {
            account.withdraw(amount);
            System.out.println("Successfully withdrew $" + amount);
        }
    }

    public void transfer(String fromAccNum, String toAccNum, double amount) throws InvalidAmountException, InsufficientFundsException {
        Account fromAccount = getAccount(fromAccNum);
        Account toAccount = getAccount(toAccNum);

        if (fromAccount != null && toAccount != null) {
            // Order matters: withdraw first so if it throws an exception (e.g., insufficient funds),
            // the method stops executing and the deposit never happens.
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);
            System.out.println("Successfully transferred $" + amount);
        }
    }

    public void printStatement(String accNum) {
        Account account = getAccount(accNum);
        if (account != null) {
            System.out.println(account.toString());
        }
    }

    // Private helper method to prevent repeating the "is account null" check
    private Account getAccount(String accNum) {
        Account account = accounts.get(accNum);
        if (account == null) {
            System.out.println("Error: Account " + accNum + " not found.");
        }
        return account;
    }
}