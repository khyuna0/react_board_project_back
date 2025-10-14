package com.khyuna0.home.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khyuna0.home.entity.Board;
import com.khyuna0.home.entity.SiteUser;
import com.khyuna0.home.repository.BoardRepository;
import com.khyuna0.home.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

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
	
	// 특정 게시글 아이디로 글 조회
	@GetMapping("/{id}")
	public ResponseEntity<?> getPost(@PathVariable("id") Long id) {
//		Board board = boardRepository.findById(id)
//				.orElseThrow(()-> new EntityNotFoundException("해당 글 없음"));
		
		Optional<Board> board = boardRepository.findById(id);
		if(board.isPresent()) { // 글 조회 성공
			return ResponseEntity.ok(board.get()); // 해당 아이디 글 반환
		} else { // 해당 글 조회 실패
			return ResponseEntity.status(404).body("해당 게시글은 존재하지 않습니다.");
		}
		
	}
}
