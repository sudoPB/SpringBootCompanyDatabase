package com.example.company.Employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Used for the POST /employees requirement.
     * Checks if an email already exists in the database.
     */
    boolean existsByEmail(String email);

    /**
     * Used for the GET /employees requirement.
     * Fetches all employees belonging to a specific department.
     */
    List<Employee> findByDepartment(String department);
}