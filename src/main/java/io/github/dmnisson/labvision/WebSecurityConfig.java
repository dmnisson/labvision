package io.github.dmnisson.labvision;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.token.SecureRandomFactoryBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import io.github.dmnisson.labvision.auth.LabVisionUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private DriverManagerDataSource dataSource;
	
	@Autowired
	private Environment environment;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.authenticationProvider(daoAuthenticationProvider())
		.authorizeRequests()
			.antMatchers("/", "/welcome", "/css/**", "/webfonts/**", "/resetpassword/**").permitAll()
			.antMatchers("/student/**").hasRole("STUDENT")
			.antMatchers("/faculty/**").hasRole("FACULTY")
			.antMatchers("/admin/**").hasRole("ADMIN")
			.anyRequest().authenticated()
			.and()
		.rememberMe()
			.tokenRepository(tokenRepository())
			.and()
		.formLogin()
			.loginPage("/login")
			.permitAll()
			.successHandler(labvisionAuthenticationSuccessHandler())
			.and()
		.logout()
			.permitAll();
	}
	
	@Bean
	public PersistentTokenRepository tokenRepository() {
		JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
		tokenRepository.setDataSource(dataSource);
		return tokenRepository;
	}

	@Bean
	public LabVisionAuthenticationSuccessHandler labvisionAuthenticationSuccessHandler() {
		return new LabVisionAuthenticationSuccessHandler();
	}
	
	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		LabVisionUserDetailsManager bean = new LabVisionUserDetailsManager(dataSource);
		return bean;
	}
	
	@Bean
	public LabVisionUserDetailsManager userDetailsManager() {
		return (LabVisionUserDetailsManager) userDetailsService();
	}
	
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService());
		return provider;
	}
	
	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	@Bean(name = "secureRandom")
	public SecureRandomFactoryBean secureRandomFactoryBean() {
		SecureRandomFactoryBean factoryBean = new SecureRandomFactoryBean();
		factoryBean.setAlgorithm(environment.getProperty("app.secure-random-algorithm", "SHA1PRNG"));
		return factoryBean;
	}
	
	@Bean
	public SecureRandom secureRandom() throws Exception {
		return secureRandomFactoryBean().getObject();
	}
}
