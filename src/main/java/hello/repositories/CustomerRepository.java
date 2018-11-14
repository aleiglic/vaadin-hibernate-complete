package hello.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.entities.Customer;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	List<Customer> findByLastNameStartsWithIgnoreCase(String lastName);
}
