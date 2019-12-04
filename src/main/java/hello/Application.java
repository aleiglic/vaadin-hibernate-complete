package hello;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class Application extends SpringBootServletInitializer{
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}
	
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}
	
	public Application() {
        super();
        //setRegisterErrorPageFilter(false); // <- this one
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
