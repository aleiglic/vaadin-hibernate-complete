package hello.entities;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import hello.authentication.Authorities;

@Entity
public class User implements UserDetails {

        /**
	 * 
	 */
	private static final long serialVersionUID = -217052938239238557L;
		@Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;
        @ManyToMany(fetch = FetchType.EAGER)
        Collection<Authorities> authorities;
        String password;
        String userName;
        Boolean accountNonExpired;
        Boolean accountNonLocked;
        Boolean credentialsNonExpired;
        Boolean enabled;

        @Transient
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        public User() {}
        
        public User(String userName, String password) {
        	this.userName = userName;
        	this.password = passwordEncoder.encode(password);
        	this.accountNonExpired = true;
        	this.accountNonLocked = true;
        	this.credentialsNonExpired = true;
        	this.enabled = true;
        }
        
        public Long getId() {
        	return id;
        }
        
		@Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return userName;
        }

        @Override
        public boolean isAccountNonExpired() {
            return accountNonExpired;
        }

        @Override
        public boolean isAccountNonLocked() {
            return accountNonLocked;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return credentialsNonExpired;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setAuthorities(Collection<Authorities> authorities) {
            this.authorities = authorities;
        }

        public void setPassword(String password) {
            this.password = passwordEncoder.encode(password);
        }

        public void setUsername(String userName) {
            this.userName = userName;
        }

        public void setAccountNonExpired(Boolean accountNonExpired) {
            this.accountNonExpired = accountNonExpired;
        }

        public void setAccountNonLocked(Boolean accountNonLocked) {
            this.accountNonLocked = accountNonLocked;
        }

        public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
            this.credentialsNonExpired = credentialsNonExpired;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
        

    }