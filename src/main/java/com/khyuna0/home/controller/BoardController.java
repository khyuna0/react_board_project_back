package com.khyuna0.home.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khyuna0.home.entity.Board;
import com.khyuna0.home.entity.SiteUser;
import com.khyuna0.home.repository.BoardRepository;
import com.khyuna0.home.repository.UserRepository;

@RestController
@RequestMapping("/api/board")
public class BoardController {
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	//전체 게시글 조회
	@GetMapping
	public List<Board> list() {
		return boardRepository.findAll();
	}
	
	// 게시글 작성
	@PostMapping
	public ResponseEntity<?> write(@RequestBody Board req, Authentication auth) {
		
		SiteUser siteUser = userRepository.findByUsername(auth.getName())
				.orElseThrow(()-> new UsernameNotFoundException("사용자 없음"));
		
		Board board = new Board();
		board.setTitle(req.getTitle());
		board.setContent(req.getContent());
		board.setAuthor(siteUser);
		
		boardRepository.save(board);

		return ResponseEntity.ok(board);
	}
}
