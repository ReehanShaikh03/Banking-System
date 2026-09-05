package model;
import exception.InvalidAmountException;
import exception.InsufficientFundsException;

public class CurrentAccount extends Account {
    private double overdraftLimit;

    public CurrentAccount(String accountNumber, String holderName, double initialBalance) {
        super(accountNumber, holderName, initialBalance);
        this.overdraftLimit = 1000;
    }

    @Override
    public void withdraw(double amount) throws InvalidAmountException, InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero.");
        }
        
        if ((balance - amount) < -overdraftLimit) {
            throw new InsufficientFundsException("Withdrawal failed: Overdraft limit of $" + overdraftLimit + " exceeded.");
        }
        
        balance -= amount;
    }

    // Optional: Overriding toString to include the overdraft limit
    @Override
    public String toString() {
        return super.toString() + " | Overdraft Limit: $" + overdraftLimit;
    }
}