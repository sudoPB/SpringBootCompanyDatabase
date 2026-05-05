package company.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "employees")
@Getter
@Setter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Column(name = "name")
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    @Size(max = 100, message = "Email should not exceed 100 characters")
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank(message = "Department is mandatory")
    @Column(name = "department")
    private String department;

    @NotNull(message = "Salary is mandatory")
    @Column(name = "salary")
    private Double salary;

    @NotBlank(message = "Role is mandatory")
    @Column(name = "role")
    private String role;
}