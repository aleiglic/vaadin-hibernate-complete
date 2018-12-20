package hello.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import hello.entities.Purchase;

public interface PurchaseRepository 
	extends JpaRepository<Purchase, Long> {	
}
