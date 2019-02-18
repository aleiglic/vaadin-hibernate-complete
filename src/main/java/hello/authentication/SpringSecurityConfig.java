package hello.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	/*
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().exceptionHandling()
				.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
				.accessDeniedPage("/accessDenied").and().authorizeRequests()
				.antMatchers("/VAADIN/**", "/PUSH/**", "/UIDL/**", "/login", "/login/**", "/error/**",
						"/accessDenied/**", "/vaadinServlet/**")
				.permitAll().antMatchers("/authorized", "/**").fullyAuthenticated();
	}*/
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable().exceptionHandling()
			.accessDeniedPage("/login?error").and()
			.authorizeRequests()
				.antMatchers("/login/**", "/login?error", "/VAADIN/**", 
						"/PUSH/**", "/UIDL/**", "/login", "/login/**", "/error/**",
						"/accessDenied/**", "/vaadinServlet/**", "/logout").permitAll()
				.antMatchers("/frontend/webapp/styles/**").permitAll()
				.regexMatchers(HttpMethod.POST, "/\\?v-r=.*").permitAll()
				.antMatchers("/**").fullyAuthenticated()
				/*.anyRequest().authenticated()*/
				.and()
			.formLogin()
				.defaultSuccessUrl("/")
				.and()
			.httpBasic()
				.and()
			.logout()                                                                
	            //.logoutUrl("mylogout")                                                 
	            .logoutSuccessUrl("/login")                                           
	            .invalidateHttpSession(true)                                             
	            .deleteCookies()                                       
	            .and()	
			;
	}

	@Bean
	public DaoAuthenticationProvider createDaoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}