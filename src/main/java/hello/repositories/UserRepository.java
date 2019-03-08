package hello.repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import hello.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{

	User findUserByUserName(String userName);

	Collection<User> findByUserNameStartsWithIgnoreCase(String filterText);

}
