package company.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import company.api.entity.Department;
import company.api.exception.DepartmentNotFoundException;
import company.api.exception.MessageException;
import company.api.repository.DepartmentRepository;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public void departmentAddSalary(String department, Double salary) {
        Department dept = departmentRepository.findByDepartment(department);
        if (dept == null) {
            throw new DepartmentNotFoundException(department);
        }
        double newTotalSalary = dept.getTotalSalary() + salary;
        if (dept.getBudget() != null && newTotalSalary > dept.getBudget()) {
            throw new MessageException("Adding this employee's salary would exceed the department's budget.");
        }
        dept.setTotalSalary(dept.getTotalSalary()+salary);
        System.out.println("Updated total salary after addition for department " + department + ": " + dept.getTotalSalary());
        departmentRepository.save(dept);
    }

    public void departmentSubtractSalary(String department, Double salary) {
        Department dept = departmentRepository.findByDepartment(department);
        if (dept == null) {
            throw new DepartmentNotFoundException(department);
        }
        double newTotalSalary = dept.getTotalSalary() - salary;
        if (newTotalSalary < 0) {
            newTotalSalary = 0.0; // Prevent negative total salary
        }
        dept.setTotalSalary(newTotalSalary);
        departmentRepository.save(dept);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartment(String department) {
        Department dept = departmentRepository.findByDepartment(department);
        if (dept == null) {
            throw new DepartmentNotFoundException(department);
        }
        return dept;
    }

    public boolean departmentExists(String department) {
        return departmentRepository.existsByDepartment(department);
    }

    public Department createDepartment(Department details) {
        if(departmentExists(details.getDepartment())) {
             throw new MessageException("Department already exists.");
        }
        if(details.getTotalSalary() == null ||details.getTotalSalary() < 0) {
             details.setTotalSalary(0.0);
        }
        return departmentRepository.save(details);
    }

    public Department updateDepartment(Long id, Department details) {
        Department existing = null;
        if(id != null) {
            existing = departmentRepository.findById(id).orElse(null);
        } else {
            existing = departmentRepository.findByDepartment(details.getDepartment());
        }
        if (existing == null) {
            throw new DepartmentNotFoundException("Department not found with ID: " + id);
        }
        existing.setDepartment(details.getDepartment());
        existing.setBudget(details.getBudget());
        existing.setTotalSalary(details.getTotalSalary());
        return departmentRepository.save(existing);
    }

    public Department deleteDepartmentById(Long id) {
        Department existing = departmentRepository.findById(id).orElse(null);
        if (existing == null) {
            throw new DepartmentNotFoundException("Department not found with ID: " + id);
        }
        departmentRepository.delete(existing);
        throw new MessageException("Department " + existing.getDepartment() + " with ID " + id + " has been deleted successfully.");
    }
}
