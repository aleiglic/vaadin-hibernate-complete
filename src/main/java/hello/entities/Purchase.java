package hello.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "Purchases")
public class Purchase implements Comparable<Purchase> {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "purchase_id")
	private Set<PurchaseDetail> details = 
		new HashSet<PurchaseDetail>();
	
	private LocalDate date;

	public Purchase() {
		this.date = LocalDate.now();
	}
	
	public Purchase(LocalDate date) {
		this.date = date;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<PurchaseDetail> getDetails() {
		return details;
	}

	public void setDetails(Set<PurchaseDetail> details) {
		this.details = details;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public void addDetail(PurchaseDetail pd) {
		this.details.add(pd);
	}
	
	public void addDetail(Product p, Integer quantity) {
		this.details.add(new PurchaseDetail(p, quantity));
	}
	
	@Override
	public int compareTo(Purchase other) {
		return this.getDate().isBefore(other.getDate()) ? -1 : 1;
	}
}
