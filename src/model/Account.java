package model;
import exception.InvalidAmountException;
import exception.InsufficientFundsException;

public abstract class Account {
    private String accountNumber;
    private String holderName;
    protected double balance;

    public Account(String accountNumber, String holderName, double initialBalance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = initialBalance;
    }

// Add the 'throws' clause to the signature
    public void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            // Use 'throw new' to trigger the exception
            throw new InvalidAmountException("Deposit amount must be greater than zero.");
        }
        this.balance += amount;
    }

    // Abstract methods must also declare the exceptions they might throw
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
