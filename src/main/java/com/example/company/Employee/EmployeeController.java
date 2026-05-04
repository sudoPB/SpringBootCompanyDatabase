package com.example.company.Employee;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository userRepository;

    @GetMapping
    public List<Employee> getAllUsers(@RequestParam(required = false) String department) {
        if (department != null) {
            return userRepository.findByDepartment(department);
        }
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Employee user) {
        // 1. Check for duplicate emails using the repository method
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(409).body("Error: Email already exists.");
        }

        // 2. Save and return the new employee if email is unique
        Employee savedEmployee = userRepository.save(user);
        return ResponseEntity.status(201).body(savedEmployee);
    }

    @PutMapping("/{id}")
    public Employee updateUser(@PathVariable Long id, @RequestBody Employee user) {
        Employee existingUser = userRepository.findById(id).get();
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setDepartment(user.getDepartment());
        existingUser.setSalary(user.getSalary());
        existingUser.setRole(user.getRole());
        return userRepository.save(existingUser);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        try {
            userRepository.findById(id).get();
            userRepository.deleteById(id);
            return "User deleted successfully";
        } catch (Exception e) {
            return "User not found";
        }
    }

}