package company.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import company.api.exception.DepartmentNotFoundException;
import company.api.exception.DuplicateEmailException;
import company.api.exception.EmployeeNotFoundException;
import company.api.exception.MessageException;
import company.api.entity.Employee;
import company.api.repository.EmployeeRepository;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentService departmentService;

    // Salary Check Logic
    public boolean checkSalaryWithManger(Employee employee) {
        List<Employee> employees = getAllEmployees(employee.getDepartment());
        for (Employee emp : employees) {
            boolean isManager = emp.getRole().toLowerCase().contains("manager");
            boolean moreManagerSalary = emp.getSalary() <= employee.getSalary();
            if (isManager && moreManagerSalary) {
                throw new MessageException("Employee's salary exceeds manager's salary.");
            }
        }
        return true; // Salary is within acceptable range
    }

    // Create with Duplicate Email Check
    public Employee createEmployee(Employee details) {
        if(departmentService.departmentExists(details.getDepartment()) == false) {
             throw new DepartmentNotFoundException(details.getDepartment());
        }
        if (employeeRepository.existsByEmail(details.getEmail())) {
            throw new DuplicateEmailException();
        }
        if(details.getSalary() == null || details.getSalary() < 0) {
             throw new MessageException("Salary cannot be negative.");
        }
        checkSalaryWithManger(details);
        departmentService.departmentAddSalary(details.getDepartment(), details.getSalary());

        return employeeRepository.save(details);
    }

    // Fetch all with optional filtering
    public List<Employee> getAllEmployees(String department) {
        if (department != null && !department.isEmpty()) {
            return employeeRepository.findByDepartment(department);
        }
        return employeeRepository.findAll();
    }

    // Fetch one with Graceful 404
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    // Full Update (PUT)
    public Employee updateEmployee(Long id, Employee details) {
        Employee employee = getEmployeeById(id); // Reuses the 404 logic above

        checkSalaryWithManger(details);

        double newSalary = details.getSalary()!= null ? details.getSalary() - employee.getSalary() : 0;

        if(newSalary > 0) {
            departmentService.departmentAddSalary(employee.getDepartment(), newSalary);
        } else if (newSalary < 0) {
            departmentService.departmentSubtractSalary(employee.getDepartment(), -newSalary);
        }

        employee.setName(details.getName()!= null ? details.getName() : employee.getName());
        employee.setEmail(details.getEmail()!= null ? details.getEmail() : employee.getEmail());
        employee.setDepartment(details.getDepartment()!= null ? details.getDepartment() : employee.getDepartment());
        employee.setSalary(details.getSalary()!= null ? details.getSalary() : employee.getSalary());
        employee.setRole(details.getRole()!= null ? details.getRole() : employee.getRole());

        return employeeRepository.save(employee);
    }

    // Delete
    public Employee deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }
        Employee employee = employeeRepository.findById(id).get();
        departmentService.departmentSubtractSalary(employee.getDepartment(), employee.getSalary());
        employeeRepository.deleteById(id);
        throw new MessageException("Employee with ID " + id + " has been deleted.");
    }
}