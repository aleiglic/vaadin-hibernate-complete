package hello.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.entities.PurchaseDetail;

public interface PurchaseDetailRepository 
	extends JpaRepository<PurchaseDetail, Long> {
	
}
