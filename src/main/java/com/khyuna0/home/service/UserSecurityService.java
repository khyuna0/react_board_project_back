package com.khyuna0.home.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.khyuna0.home.entity.SiteUser;
import com.khyuna0.home.repository.UserRepository;

@Service
public class UserSecurityService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	// Spring security -> 유저에게 받은 username과 password를 조회
	
	@Override // DB에서 조회
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		SiteUser siteUser = userRepository.findByUsername(username)
				.orElseThrow(()-> new UsernameNotFoundException("사용자 없음"));
		return org.springframework.security.core.userdetails.User
				.withUsername(siteUser.getUsername())
				.password(siteUser.getPassword())
				.authorities("USER")
				.build();
	}
}
