package company.api.exception;

public class DepartmentNotFoundException extends RuntimeException {
    public DepartmentNotFoundException(String department) {
        super("Department not found: " + department);
    }
}
