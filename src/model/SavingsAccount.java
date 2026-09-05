package model;
import exception.InvalidAmountException;
import exception.InsufficientFundsException;

public class SavingsAccount extends Account {
    // A constant in Java (like const in C++). static means it belongs to the class.
    private static final double MINIMUM_BALANCE = 500.0;

    public SavingsAccount(String accountNumber, String holderName, double initialBalance) {
        // In Java, super() replaces C++ initialization lists to call the parent constructor.
        // Rule: It MUST be the very first line inside the constructor.
        super(accountNumber, holderName, initialBalance);
    }

    @Override
    public void withdraw(double amount) throws InvalidAmountException, InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero.");
        }
        
        if ((balance - amount) < MINIMUM_BALANCE) {
            throw new InsufficientFundsException("Withdrawal failed: Must maintain a minimum balance of $" + MINIMUM_BALANCE);
        }
        
        balance -= amount;
    }
}