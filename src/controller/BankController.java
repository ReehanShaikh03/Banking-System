package controller;

import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import service.Bank;

import java.util.Scanner;

public class BankController {
    public static void main(String[] args) {
        Bank bank = new Bank();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("=== Welcome to the Java Banking System ===");

        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Create Savings Account");
            System.out.println("2. Create Current Account");
            System.out.println("3. Deposit");
            System.out.println("4. Withdraw");
            System.out.println("5. Transfer");
            System.out.println("6. Print Statement");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            // Critical: Consume the leftover newline character after reading an int
            scanner.nextLine(); 

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter Account Number: ");
                        String accNum = scanner.nextLine();
                        System.out.print("Enter Holder Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter Initial Balance: ");
                        double balance = scanner.nextDouble();
                        bank.createSavingsAccount(accNum, name, balance);
                        break;
                    case 2:
                        System.out.print("Enter Account Number: ");
                        String CurraccNum = scanner.nextLine();
                        System.out.print("Enter Holder Name: ");
                        String Currname = scanner.nextLine();
                        System.out.print("Enter Initial Balance: ");
                        double Currbalance = scanner.nextDouble();
                        bank.createCurrentAccount(CurraccNum, Currname, Currbalance);
                        break;
                    case 3:
                        System.out.print("Enter Account Number: ");
                        String depAcc = scanner.nextLine();
                        System.out.print("Enter Amount to Deposit: ");
                        double depAmt = scanner.nextDouble();
                        bank.deposit(depAcc, depAmt);
                        break;
                    case 4:
                        System.out.print("Enter Account Number: ");
                        String withAcc = scanner.nextLine();
                        System.out.print("Enter Amount to Withdraw: ");
                        double withAmt = scanner.nextDouble();
                        bank.withdraw(withAcc, withAmt);
                        break;
                    case 5:
                        System.out.print("Enter Source Account Number: ");
                        String fromAcc = scanner.nextLine();
                        System.out.print("Enter Destination Account Number: ");
                        String toAcc = scanner.nextLine();
                        System.out.print("Enter Amount to Transfer: ");
                        double transAmt = scanner.nextDouble();
                        bank.transfer(fromAcc, toAcc, transAmt);
                        break;
                    case 6:
                        System.out.print("Enter Account Number: ");
                        String printAcc = scanner.nextLine();
                        bank.printStatement(printAcc);
                        break;
                    case 7:
                        running = false;
                        System.out.println("Exiting system. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (InvalidAmountException | InsufficientFundsException e) {
                // Java Multi-catch block: cleanly handles our custom business logic errors
                System.out.println("Transaction Error: " + e.getMessage());
            } catch (Exception e) {
                // Catch-all for unexpected errors (like entering text when a double was expected)
                System.out.println("Unexpected Error: " + e.getMessage());
                scanner.nextLine(); // Clear the bad input
            }
        }
        
        // Close the scanner to prevent memory leaks (releasing the system I/O resource)
        scanner.close();
    }
}