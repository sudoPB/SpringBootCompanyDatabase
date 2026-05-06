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
    private String name;


    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email should not exceed 100 characters")
    private String email;

    @NotBlank(message = "Department is mandatory")
    private String department;

    @NotNull(message = "Salary is mandatory")
    private Double salary;

    @NotBlank(message = "Role is mandatory")
    private String role;
}