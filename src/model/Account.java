package model;

import exception.InsufficientFundsException;
import exception.InvalidAmountException;

public abstract class Account {

    private String accountNumber;
    private String holderName;
    protected double balance;

    public Account() {}

    public Account(String accountNumber, String holderName, double initialBalance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = initialBalance;
    }

    public void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero.");
        }
        this.balance += amount;
    }

    public abstract void withdraw(double amount) throws InvalidAmountException, InsufficientFundsException;

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Account: " + accountNumber + " | Holder: " + holderName + " | Balance: $" + balance;
    }
}