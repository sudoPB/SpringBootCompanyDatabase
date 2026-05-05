package company.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		System.out.println("Starting API application...");
		SpringApplication.run(ApiApplication.class, args);
	}

}
