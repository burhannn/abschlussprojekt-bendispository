package Bendispository.Abschlussprojekt.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/").permitAll()
				.antMatchers("/admin").hasRole("ADMIN")
				.anyRequest().authenticated()
				.and().formLogin().permitAll()
				.and().logout().permitAll();
		http.userDetailsService(userDetailsService);
	}



/*
// Authentication
@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
{
	auth.inMemoryAuthentication()
			.withUser("rac")
			.password("secret")
			.roles("USER","ADMIN");
}

// Authorization
@Configuration
@Order(2)
public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
	protected void configure(HttpSecurity http) throws Exception {
		http
				.antMatcher("/**")
				.authorizeRequests()
				.anyRequest().hasRole("ADMIN")
				.and()
				.httpBasic()
				.and()
				.csrf()
				.disable();
	}
}

@Configuration
@Order(1)
public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter
{
	@Override
	public void configure(HttpSecurity http) throws Exception
	{
		http.antMatcher("/website/**").authorizeRequests()
				.anyRequest().hasRole("USER")

				.and()

				.formLogin().loginPage("/login.html")
				.failureUrl("/login.html?error=1")
				.loginProcessingUrl("/login")
				.permitAll()
				.and()
				.logout()
				.logoutSuccessUrl("/OverviewAllItems.html");
	}
}

*/
/*
@Override
protected void configure(HttpSecurity http) throws Exception {
	http.authorizeRequests()
			.antMatchers("/").permitAll()
			.antMatchers("/admin").hasRole("ADMIN")
			.anyRequest().authenticated();
	http.formLogin().permitAll();
	http.logout().permitAll();
}*/
}