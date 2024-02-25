package com.Bank.MoneyBank;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "King Bank: A Banking Management System",
				description = """
                     Backend REST APIS for a banking management system. 
                     This system has been secured using jwt security. 
                     Apart from the sign-up and login endpoints, every endpoint was secured.
                     This system was designed to mimic a typical banking system.
                     All endpoints with the pattern /api/v1/officer/** will need to use a jwt token generated from calling the **/officer/login endpoint\s
                     Likewise all endpoints with the pattern /api/v1/iCustomer/** will also need a jwt token generated from calling the **/iCustomer/login endpoint\s
                     You can use the character '1' as id for officer for endpoints that require officerId because an officer of id '1'is automatically generated
                     I implore you to use an actual email when interacting with these endpoints because they've been programmed to send emails to the email you'll be using 
                     """
				,
				version = "v1.0",
				contact = @Contact(
						name = "Ikechukwu Ihueze",
						email = "gosple531@gmail.com"
				),
				license = @License(
						name = "My property"
				)
		)
)

public class MoneyBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneyBankApplication.class, args);
	}

}
