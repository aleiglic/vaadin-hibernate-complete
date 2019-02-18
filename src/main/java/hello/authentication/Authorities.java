package hello.authentication;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;

@Entity
public class Authorities implements GrantedAuthority {

        /**
	 * 
	 */
	private static final long serialVersionUID = 341078375685823336L;

		@Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;

        String authority;

        @Override
        public String getAuthority() {
            return authority;
        }

        public void setAuthority(String authority) {
            this.authority = authority;
        }

}