package Bendispository.Abschlussprojekt.Security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/").permitAll()
				.antMatchers("/admin").hasRole("ADMIN")
				.anyRequest().authenticated();
		http.formLogin().permitAll();
		http.logout().permitAll();
	}

}