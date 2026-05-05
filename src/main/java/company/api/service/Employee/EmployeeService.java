package company.api.service.Employee;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import company.api.exception.BudgetExceededException;
import company.api.exception.DepartmentNotFoundException;
import company.api.exception.DuplicateEmailException;
import company.api.exception.EmployeeNotFoundException;
import company.api.exception.MessageException;
import company.api.entity.Employee;
import company.api.repository.EmployeeRepository;
import company.api.service.Department.DepartmentService;
import jakarta.transaction.Transactional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentService departmentService;

    // 1. Create with Duplicate Email Check
    public Employee createEmployee(Employee employee) {
        if(departmentService.departmentExists(employee.getDepartment()) == false) {
             throw new DepartmentNotFoundException(employee.getDepartment());
        }
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new DuplicateEmailException();
        }
        if(employee.getSalary() == null || employee.getSalary() < 0) {
             throw new MessageException("Salary cannot be negative.");
        }
        departmentService.departmentAddSalary(employee.getDepartment(), employee.getSalary());

        return employeeRepository.save(employee);
    }

    // 2. Fetch all with optional filtering
    public List<Employee> getAllEmployees(String department) {
        if (department != null && !department.isEmpty()) {
            return employeeRepository.findByDepartment(department);
        }
        return employeeRepository.findAll();
    }

    // 3. Fetch one with Graceful 404
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    // 4. Full Update (PUT)
    public Employee updateEmployee(Long id, Employee details) {
        Employee existing = getEmployeeById(id); // Reuses the 404 logic above

        existing.setName(details.getName());
        existing.setEmail(details.getEmail());
        existing.setDepartment(details.getDepartment());
        existing.setSalary(details.getSalary());
        existing.setRole(details.getRole());

        return employeeRepository.save(existing);
    }

    // 5. Delete
    public Employee deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }
        departmentService.departmentSubtractSalary(employeeRepository.findById(id).get().getDepartment(), employeeRepository.findById(id).get().getSalary());
        employeeRepository.deleteById(id);
        throw new MessageException("Employee with ID " + id + " has been deleted.");
    }

    // 6. The Smart Salary Adjustment Logic
    @Transactional
    public void adjustSalaries(String department, double percentage) {
        // 1. Fetch all employees in the department
        List<Employee> employees = employeeRepository.findByDepartment(department);
        if (employees.isEmpty()) {
            throw new DepartmentNotFoundException(department);
        }

        // 2. Calculate the projected total after the hike
        Double currentTotal = employeeRepository.calculateTotalSalaryByDepartment(department);
        if (currentTotal == null) {
            currentTotal = 0.0;
        }
        double projectedIncrease = currentTotal * (percentage / 100);
        double finalTotal = currentTotal + projectedIncrease;

        // CONSTRAINT 1: The Budget Cap ($500,000)
        if (finalTotal > 500000) {
            throw new BudgetExceededException("Hike rejected: Department budget would reach $" +
                    finalTotal + " (Cap: $500,000)");
        }

        // 3. Apply the hike with CONSTRAINT 2: Seniority Protection
        for (Employee emp : employees) {
            double newSalary = emp.getSalary() * (1 + (percentage / 100));

            if (emp.getManagerId() != null) {
                Employee manager = employeeRepository.findById(emp.getManagerId())
                        .orElse(null);

                if (manager != null && newSalary > manager.getSalary()) {
                    // Cap at Manager's salary level
                    newSalary = manager.getSalary();
                }
            }
            emp.setSalary(newSalary);
        }

        employeeRepository.saveAll(employees);
    }
}