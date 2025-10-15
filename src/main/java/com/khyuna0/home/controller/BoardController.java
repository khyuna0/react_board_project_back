package com.khyuna0.home.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.khyuna0.home.dto.BoardDto;
import com.khyuna0.home.entity.Board;
import com.khyuna0.home.entity.SiteUser;
import com.khyuna0.home.repository.BoardRepository;
import com.khyuna0.home.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/board")
public class BoardController {
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private UserRepository userRepository;
	
//	//전체 게시글 조회 (페이징 처리 x) 
//	@GetMapping
//	public List<Board> list() {
//		return boardRepository.findAll();
//	}
	
	// 페이징 처리된 전체 게시글 조회
	@GetMapping
	public ResponseEntity<?> pagingList(@RequestParam(name = "page" , defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size) {
		
		if (page < 0) { // page -> 사용자가 요청한 페이지의 번호
			page = 0;
		}
		if (size <= 0) { // 한 페이지당 보여질 글의 개수
			size = 10;
		}
		// .domain.Pageable(인터페이스) 선택 , pageable 객체 생성 -> findAll() 에서 사용
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		
		Page<Board> boardPage = boardRepository.findAll(pageable); 
		// DB에서 페이징된 게시글만 조회, 반환 타입 Page<Board> 로 바뀜
		
		// boardPage가 포함하는 정보
		// 1. 해당 페이지의 글 리스트 - boardPage.getContent()
		// 2. 현재 페이지 번호 - boardPage.getNumber()
		// 3. 전체 페이지 수 - boardPage.getTotalpages()
		// 4. 전체 게시글 수 - boardPage.getTotalElements()
		
		Map<String, Object> pagingResponse = new HashMap<>(); // json 형식 전달
		pagingResponse.put("posts", boardPage.getContent()); // 페이징된 현재 페이지에 해당하는 게시글 리스트 10개
		pagingResponse.put("currentPage", boardPage.getNumber()); // 현재 페이지 번호
		pagingResponse.put("totalPages", boardPage.getTotalPages()); // 전체 페이지 수 int
		pagingResponse.put("totalItems", boardPage.getTotalElements()); // 전체 게시글 수 long
		
		
		return ResponseEntity.ok(pagingResponse);
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
	
	//게시글 작성(유효성 체크->제목과 내용은 5글자 이상)
	@PostMapping
	public ResponseEntity<?> write(@Valid @RequestBody BoardDto boardDto, 
									BindingResult bindingResult, 
									Authentication auth) {
		
		//사용자의 로그인 여부 확인
		if (auth == null) { //참이면 로그인 x -> 글쓰기 권한 없음 -> 에러코드 반환  
			return ResponseEntity.status(401).body("로그인 후 글쓰기 가능합니다.");
		}
		
		//Spring Validation 결과 처리
		if(bindingResult.hasErrors()) { //참이면 유효성 체크 실패->error 발생
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(
				err -> {
					errors.put(err.getField(), err.getDefaultMessage());					
				}
			);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
		}
		
		//auth.getName() -> 로그인한 유저 이름		
		SiteUser siteUser = userRepository.findByUsername(auth.getName())
				.orElseThrow(()->new UsernameNotFoundException("사용자 없음"));
		//siteUser->현재 로그인한 유저의 레코드
		
		Board board = new Board();
		board.setTitle(boardDto.getTitle()); //유저가 입력한 글 제목
		board.setContent(boardDto.getContent()); //유저가 입력한 글 내용
		board.setAuthor(siteUser); //유저 정보
		
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
