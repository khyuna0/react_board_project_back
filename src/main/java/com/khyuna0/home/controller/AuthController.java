package com.khyuna0.home.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khyuna0.home.dto.SiteUserDto;
import com.khyuna0.home.entity.SiteUser;
import com.khyuna0.home.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final SecurityFilterChain filterChain;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

    AuthController(SecurityFilterChain filterChain) {
        this.filterChain = filterChain;
    } 
	
	// 가입 (validation 미적용)
//	@PostMapping("/signup")
//	public ResponseEntity<?> signup (@RequestBody SiteUser req) {
//		// 사용자 이름이 DB에 이미 존재하는 사용자 이름인지 확인
//		if(userRepository.findByUsername(req.getUsername()).isPresent()) {
//			// 참이면 -> 해당 username(아이디)가 존재하므로 가입 불가
//			// 거짓이면 가입 가능
//			return ResponseEntity.badRequest().body("이미 존재하는 사용자명 입니다."); // 가입 실패
//		}
//		// 비밀번호 암호화해서 엔티티에 다시 넣기
//		req.setPassword(passwordEncoder.encode(req.getPassword()));
//		userRepository.save(req);
//		
//		return ResponseEntity.ok("회원 가입 성공!"); // 가입 성공
////		return ResponseEntity.ok(req); -> 가입 성공 후 해당 엔티티 반환
//	}
//	
	// 가입 (validation 적용)
	@PostMapping("/signup")
	public ResponseEntity<?> signup (@RequestBody @Valid SiteUserDto siteUserDto, BindingResult bindingResult) {
		
		// 스프링 Validation 처리. 에러 종류만큼 
		if(bindingResult.hasErrors()) { // T -> 에러 존재
			Map<String, String> errors = new HashMap<>(); // 
			bindingResult.getFieldErrors().forEach(
				err -> {
					errors.put(err.getField(), err.getDefaultMessage());
//						{"username" : " "아이디는 최소 4글자 이상입니다." , 
//						 "password" : "비밀번호는 최소 4글자 이상입니다." }		
					} 
			);
			return ResponseEntity.badRequest().body(errors);
		}
		
		SiteUser siteUser = new SiteUser();
		siteUser.setUsername(siteUserDto.getUsername());
		siteUser.setPassword(siteUserDto.getPassword());
		// 사용자 이름이 DB에 이미 존재하는 사용자 이름인지 확인
		if(userRepository.findByUsername(siteUser.getUsername()).isPresent()) {
			// 참이면 -> 해당 username(아이디)가 존재하므로 가입 불가
			// 거짓이면 가입 가능
			Map<String, String> error = new HashMap<>();
			error.put("iderror", "이미 존재하는 아이디 입니다.");
			return ResponseEntity.badRequest().body(error); // 가입 실패
		}
		// 비밀번호 암호화해서 엔티티에 다시 넣기
		siteUserDto.setPassword(passwordEncoder.encode(siteUser.getPassword()));
		userRepository.save(siteUser);
		
		return ResponseEntity.ok("회원 가입 성공!"); // 가입 성공
//		return ResponseEntity.ok(req); -> 가입 성공 후 해당 엔티티 반환
	}
	
	// 로그인
	@GetMapping("/me") // me -> 현재 로그인한 사용자 정보를 가져오는 요청 (현재 로그인한 유저(나)의 정보, 관례)
	public ResponseEntity<?> me(Authentication auth) {
		return ResponseEntity.ok(Map.of("username", auth.getName()));
	}
	
	
}
