package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaadin.flow.spring.SpringServlet;

import hello.entities.User;
import hello.repositories.UserRepository;


@SuppressWarnings("unused")
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class Application extends SpringBootServletInitializer{

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}
	
	/*
	@Bean
	public CommandLineRunner loadData(CustomerRepository repository) {
		
		return (args) -> {
			// save a couple of customers
			repository.save(new Customer("Jack", "Bauer"));
			repository.save(new Customer("Chloe", "O'Brian"));
			repository.save(new Customer("Kim", "Bauer"));
			repository.save(new Customer("David", "Palmer"));
			repository.save(new Customer("Michelle", "Dessler"));

			// fetch all customers
			log.info("Customers found with findAll():");
			log.info("-------------------------------");
			for (Customer customer : repository.findAll()) {
				log.info(customer.toString());
			}
			log.info("");

			// fetch an individual customer by ID
			Customer customer = repository.findById(1L).get();
			log.info("Customer found with findOne(1L):");
			log.info("--------------------------------");
			log.info(customer.toString());
			log.info("");

			// fetch customers by last name
			log.info("Customer found with findByLastNameStartsWithIgnoreCase('Bauer'):");
			log.info("--------------------------------------------");
			for (Customer bauer : repository
					.findByLastNameStartsWithIgnoreCase("Bauer")) {
				log.info(bauer.toString());
			}
			log.info("");
			
		};
	}*/
	
	
	/*
	@Bean
	public CommandLineRunner registerNewUserAccount(UserRepository userRepository) {
	   
		return (args) -> {
			PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			
		    User user = new User();
		    user.setUserName("user");
		     
		    user.setPassword("test");
		    user.setAccountNonExpired(true);
		    user.setAccountNonLocked(true);
		    user.setCredentialsNonExpired(true);
		    user.setEnabled(true);
		    System.out.println(user.getPassword());
		    log.info("ADD USER:");
		    log.info(user.getPassword());
		    log.info(user.getPassword());
		    userRepository.save(user);
		};
	}*/

}
