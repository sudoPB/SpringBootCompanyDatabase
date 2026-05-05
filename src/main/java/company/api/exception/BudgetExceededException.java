package company.api.exception;

public class BudgetExceededException extends RuntimeException {
    public BudgetExceededException(String department) {
        super("Budget exceeded for department: " + department);
    }
}
