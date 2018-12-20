package hello.projections;

import java.time.LocalDate;

public interface CustomerProj {
	public Long getId();
	public String getFirstName();
	public String getLastName();
	public LocalDate getBirthDate();
	public Integer getAge();
}
