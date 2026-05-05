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

    public boolean checkSalaryWithManger(Employee employee) {
        List<Employee> employees = getAllEmployees(employee.getDepartment());
        for (Employee emp : employees) {
            if (emp.getRole().toLowerCase().contains("manager") && emp.getSalary() <= employee.getSalary()) {
                throw new MessageException("Employee's salary exceeds manager's salary.");
            }
        }
        return true; // Salary is within acceptable range
    }

    // 1. Create with Duplicate Email Check
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
        Employee employee = getEmployeeById(id); // Reuses the 404 logic above

        checkSalaryWithManger(details);

        departmentService.departmentAddSalary(details.getDepartment(), details.getSalary());
        departmentService.departmentSubtractSalary(employee.getDepartment(), employee.getSalary());

        employee.setName(details.getName());
        employee.setEmail(details.getEmail());
        employee.setDepartment(details.getDepartment());
        employee.setSalary(details.getSalary());
        employee.setRole(details.getRole());

        return employeeRepository.save(employee);
    }

    // 5. Delete
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