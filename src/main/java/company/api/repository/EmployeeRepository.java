package company.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import company.api.entity.Employee;

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
    /**
     * Used for the POST /employees requirement.
     * Fetches all employees with a specific salary.
     */
    List<Employee> findBySalaryGreaterThanEqual(Double salary);

    @Query("SELECT SUM(e.salary) FROM Employee e WHERE e.department = :department")
    double calculateTotalSalaryByDepartment(String department);
}