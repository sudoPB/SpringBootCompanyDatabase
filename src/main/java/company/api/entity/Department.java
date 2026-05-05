package company.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "department_budgets")
@Getter
@Setter
public class Department {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Department is mandatory")
    @Column(name = "department", unique = true)
    private String department;

    @NotNull(message = "Budget is mandatory")
    @Column(name = "budget")
    private Double budget;

    @Column(name = "total_salary")
    private Double totalSalary;
}
