package project.wallet.exeption;

public class ConcurrentOperationException extends RuntimeException{
    public ConcurrentOperationException(String message) {
        super(message);
    }
}
