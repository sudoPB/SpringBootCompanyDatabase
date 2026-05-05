package company.api.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("Email already exists");
    }
}
