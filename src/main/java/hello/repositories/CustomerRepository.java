package hello.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hello.entities.Customer;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
	
	//@Query("SELECT c FROM Customer c WHERE lastName LIKE CONCAT('%', :lastName, '%')")
	@Query(value = "SELECT * FROM customers c WHERE c.last_name  AND active = true LIKE CONCAT('%', :lastName, '%') LIMIT 100", nativeQuery = true)
	List<Customer> findByLastNameStartsWithIgnoreCase(
		@Param(value = "lastName") String lastName
	);
	
	List<Customer> findAll();
	
	@Query(value = "SELECT * FROM customers WHERE active = true LIMIT 100", nativeQuery = true)
	List<Customer> findTop100();
}
