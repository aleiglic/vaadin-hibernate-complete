package hello.entities;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

import org.springframework.hateoas.Identifiable;



@Entity
@Table(name = "Customers")
public class Customer implements Identifiable<Long>{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String firstName;

	private String lastName;

	private LocalDate birthDate;
	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "customer_id")
	private Set<Purchase> purchases = new HashSet<Purchase>();
	
	public Customer() {
		this.birthDate = LocalDate.parse("2018-08-12");
	}

	public Customer(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = LocalDate.parse("2018-08-12");
	}
	
	public Customer(String firstName, String lastName, LocalDate birthDate) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
	}

	public Long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}
	
	public Integer getAge() {
		return Objects.isNull(birthDate) ? 0 : Period.between(birthDate, LocalDate.now()).getYears();
	}

	/*@Override
	public String toString() {
		return String.format("Customer[id=%d, firstName='%s', lastName='%s']", id,
				firstName, lastName);
	}*/
	
	@Override
	public String toString() {
		return String.format("%d: %s, %s", id, lastName, firstName);
	}

	public Set<Purchase> getPurchases() {
		return purchases;
	}

	public void setPurchases(Set<Purchase> purchases) {
		this.purchases = purchases;
	}
	
	public void addPurchase(Purchase p) {
		this.purchases.add(p);
	}
	
	public List<Purchase> getAllPurchases(){
		List<Purchase> list = new ArrayList<Purchase>();
		list.addAll(purchases);
		return list;
	}

}
