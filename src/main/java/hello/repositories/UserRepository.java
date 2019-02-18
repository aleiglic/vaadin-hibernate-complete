package hello.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import hello.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{

	User findUserByUserName(String userName);

}
