package dao;

import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {

    public boolean save(Account account, String accountType) {
        String sql = "INSERT INTO accounts (account_number, holder_name, balance, account_type) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, account.getHolderName());
            pstmt.setDouble(3, account.getBalance());
            pstmt.setString(4, accountType);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Database error during account saving: " + e.getMessage());
            return false;
        }
    }

    public Account findByAccountNumber(String accNum) {
        String sql = "SELECT account_number, holder_name, balance, account_type FROM accounts WHERE account_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String accountNumber = rs.getString("account_number");
                    String holderName = rs.getString("holder_name");
                    double balance = rs.getDouble("balance");
                    String type = rs.getString("account_type");
                    
                    if ("SAVINGS".equalsIgnoreCase(type)) {
                        return new SavingsAccount(accountNumber, holderName, balance);
                    } else if ("CURRENT".equalsIgnoreCase(type)) {
                        return new CurrentAccount(accountNumber, holderName, balance);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during account retrieval: " + e.getMessage());
        }
        
        return null;
    }

    public boolean updateBalance(Account account) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, account.getBalance());
            pstmt.setString(2, account.getAccountNumber());
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Database error during balance update: " + e.getMessage());
            return false;
        }
    }
}
