package com.khyuna0.home.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khyuna0.home.dto.BoardDto;
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
	
	// 게시글 작성 (validation x)
//	@PostMapping
//	public ResponseEntity<?> write(@RequestBody Board req, Authentication auth) {
//		
//		SiteUser siteUser = userRepository.findByUsername(auth.getName())
//				.orElseThrow(()-> new UsernameNotFoundException("사용자 없음"));
//		
//		Board board = new Board();
//		board.setTitle(req.getTitle());
//		board.setContent(req.getContent());
//		board.setAuthor(siteUser);
//		
//		boardRepository.save(board);
//
//		return ResponseEntity.ok(board);
//	}
	
	// 게시글 작성
	@PostMapping
	public ResponseEntity<?> write(@RequestBody BoardDto boardDto, 
			Authentication auth, BindingResult bindingResult) {
		
		// 유저의 로그인 여부 확인 
		if (auth == null) { // 참이면 로그인되지 않은 상태 -> 권한없음
			return ResponseEntity.status(401).body("로그인 후 글쓰기 가능합니다.");
		}
		
		SiteUser siteUser = userRepository.findByUsername(auth.getName())
				.orElseThrow(()-> new UsernameNotFoundException("사용자 없음"));
		
		if(bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(
					err -> {
						errors.put(err.getField(), err.getDefaultMessage());
						}
					);
			return ResponseEntity.badRequest().body(errors);
		}
		
		Board board = new Board();
		board.setTitle(boardDto.getTitle());
		board.setContent(boardDto.getContent());
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
	
	// 특정 아이디 글 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletePost(@PathVariable("id") Long id, Authentication auth) {
		Optional<Board> board = boardRepository.findById(id);
		
		if(board.isEmpty()) { // T -> 수정할 글 존재하지 않음
			return ResponseEntity.status(404).body("해당 게시글이 존재하지 않습니다.");
		}
		
		// 권한 확인
		if(auth == null || !auth.getName().equals(board.get().getAuthor().getUsername())) {
			return ResponseEntity.status(403).body("해당 글에 대한 삭제 권한이 없습니다.");
		}
		
		boardRepository.deleteById(id);
		return ResponseEntity.ok("삭제 성공");

	}
	
	// 글 수정 (권한 설정, 본인 작성글만 수정 가능)
	@PutMapping("/{id}") 
	public ResponseEntity<?> editPost(@PathVariable("id") Long id, @RequestBody Board updateBoard, Authentication auth) {
		Optional<Board> optional = boardRepository.findById(id);
		
		// 글 존재 여부 
		if(optional.isEmpty()) { // T -> 수정할 글 존재하지 않음
			return ResponseEntity.status(404).body("해당 게시글이 존재하지 않습니다.");
		}
		
		// 권한 확인
		if(auth == null || !auth.getName().equals(optional.get().getAuthor().getUsername())) {
			return ResponseEntity.status(403).body("해당 글에 대한 수정 권한이 없습니다.");
		}
		
		Board oldBoard = optional.get(); // 기존 게시글
		oldBoard.setTitle(updateBoard.getTitle());
		oldBoard.setContent(updateBoard.getContent());
		
		boardRepository.save(oldBoard);
		
		return ResponseEntity.ok(oldBoard);

	}
	
	
	
}
