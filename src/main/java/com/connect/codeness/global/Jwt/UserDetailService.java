package com.connect.codeness.global.Jwt;

import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {

	private final UserRepository userRepository;

	public UserDetailService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	//repository 검증(이메일, 비밀번호) 검증
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmailOrElseThrow(email);

		//UserDetails 객체 생성
		return new org.springframework.security.core.userdetails.User(
			user.getEmail(),
			user.getPassword(),
			Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
		);
	}
}
