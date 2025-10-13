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
	// username이 존재하지 않으면 "사용자 없음"으로 에러 발생
	// username이 존재하면 password 확인 -> 성공 시 권한 부여 (단, 비밀번호가 암호화 되어 있어야 함)
	
	@Override // DB에서 조회
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		SiteUser siteUser = userRepository.findByUsername(username)
				.orElseThrow(()-> new UsernameNotFoundException("사용자 없음"));
		return org.springframework.security.core.userdetails.User
				.withUsername(siteUser.getUsername())
				.password(siteUser.getPassword())
				.authorities("USER") // 권한 부여
				.build(); // UserDetails 객체 생성 후 반환
	}
}
