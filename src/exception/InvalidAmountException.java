package exception;

// Extending 'Exception' makes this a "Checked Exception"
public class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        // Pass the message up to the parent Exception class
        super(message);
    }
}       