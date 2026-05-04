package com.example.company.Employee;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    public List<Employee> getAllUsers(@RequestParam(required = false) String department) {
        if (department != null) {
            return employeeRepository.findByDepartment(department);
        }
        return employeeRepository.findAll();
    }

    @GetMapping("/TotalSalary")
    public Double getTotalSalary(@RequestParam(required = false) String department) {
        if (department != null) {
            return employeeRepository.findByDepartment(department).stream()
                .mapToDouble(Employee::getSalary)
                .sum();
        }
        return employeeRepository.findAll().stream()
                .mapToDouble(Employee::getSalary)
                .sum();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            employeeRepository.findById(id).get();
            return employeeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Employee not found");
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Employee employee) {
        // 1. Check for duplicate emails using the repository method
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            return ResponseEntity.status(409).body("Error: Email already exists.");
        }

        // 2. Save and return the new employee if email is unique
        Employee savedEmployee = employeeRepository.save(employee);
        return ResponseEntity.status(201).body(savedEmployee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Employee employee) {
        try {
            employeeRepository.findById(id).get();
            Employee existingUser = employeeRepository.findById(id).get();
            existingUser.setName(employee.getName());
            existingUser.setEmail(employee.getEmail());
            existingUser.setDepartment(employee.getDepartment());
            existingUser.setSalary(employee.getSalary());
            existingUser.setRole(employee.getRole());
            return ResponseEntity.ok(employeeRepository.save(existingUser));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Employee not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            employeeRepository.findById(id).get();
            employeeRepository.deleteById(id);
            return ResponseEntity.ok("Employee deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Employee not found");
        }
    }

}