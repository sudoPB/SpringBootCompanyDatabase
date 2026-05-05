package company.api.exception;

public class DuplicateDepartment extends RuntimeException {
    public DuplicateDepartment(String department) {
        super("Department '" + department + "' already exists.");
    }
}