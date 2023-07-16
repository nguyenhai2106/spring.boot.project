package com.donations.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.donations.security.oauth.CustomerOauth2UserService;
import com.donations.security.oauth.OAuth2LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	@Autowired
	private CustomerOauth2UserService customerOauth2UserService;

	@Autowired
	private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

	@Autowired
	private DatabaseLoginSuccessHandler databaseLoginSuccessHandler;

	@Bean
	public CustomerUserDetailsService customerUserDetailsService() {
		return new CustomerUserDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests().requestMatchers("/account_details", "/update_account_details", "/cart")
				.authenticated().anyRequest().permitAll().and()
				.formLogin(login -> login.loginPage("/login").usernameParameter("email")
						.successHandler(databaseLoginSuccessHandler).permitAll())
				.oauth2Login(login -> login.loginPage("/login").userInfoEndpoint()
						.userService(customerOauth2UserService).and().successHandler(oAuth2LoginSuccessHandler))
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").permitAll())
				.rememberMe(
						me -> me.key("1234567890_aBcDeFgHiJkLmNoPqRsTuVwXyZ").tokenValiditySeconds(14 * 24 * 60 * 60))
				.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));
		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer(HttpSecurity http) {
		return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**", "/style.css",
				"fontawesome-free-6.2.1-web/**");
	}
}
