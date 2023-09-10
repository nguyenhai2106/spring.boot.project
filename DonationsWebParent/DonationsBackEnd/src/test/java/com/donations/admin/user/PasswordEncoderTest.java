package com.donations.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.donations.common.entity.User;

public class PasswordEncoderTest {
	@Autowired
	private UserRepository repository;
	
	@Test
	public void testEncodePassword() {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String rawPassword = "1234qwer";
		String encodedPassword = passwordEncoder.encode(rawPassword);
		System.out.println(encodedPassword);
		boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
		User userHaiNT = repository.findById(1).get();
		System.out.println(userHaiNT);
		userHaiNT.setEnabled(true);
		userHaiNT.setPassword(encodedPassword);
		repository.save(userHaiNT);
		assertThat(matches).isTrue();
	}
}
