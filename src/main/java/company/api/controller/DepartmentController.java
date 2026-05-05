package company.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import company.api.entity.Department;
import company.api.service.Department.DepartmentService;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    
    @Autowired
    private DepartmentService departmentBudgetService;


    @GetMapping
    public List<Department> getAllDepartmentBudgets() {
        return departmentBudgetService.getAllDepartments();
    }

    @GetMapping("/{department}")
    public Department getDepartmentBudget(@PathVariable String department) {
        return departmentBudgetService.getDepartment(department);
    }

    @PostMapping
    public Department createOrUpdateDepartmentBudget(@RequestBody Department details) {
        return departmentBudgetService.createDepartment(details);
    }

    @PutMapping("/{id}")
    public Department updateDepartmentBudgetById(@PathVariable Long id, @RequestBody Department details) {
        return departmentBudgetService.updateDepartment(id,details);
    }

    @DeleteMapping("/{id}")
    public Department deleteDepartmentBudgetById(@PathVariable Long id) {
        return departmentBudgetService.deleteDepartmentById(id);
    }
}