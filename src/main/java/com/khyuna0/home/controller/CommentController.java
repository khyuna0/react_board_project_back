package com.khyuna0.home.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khyuna0.home.dto.CommentDto;
import com.khyuna0.home.entity.Board;
import com.khyuna0.home.entity.Comment;
import com.khyuna0.home.entity.SiteUser;
import com.khyuna0.home.repository.BoardRepository;
import com.khyuna0.home.repository.CommentRepository;
import com.khyuna0.home.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping ("/api/commnets")
public class CommentController {
	
	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private UserRepository userRepository;
	
	// 댓글 작성 
	@PostMapping("/{boardId}")
	public ResponseEntity<?> writeComment(@PathVariable("boardId") Long boardId,
			@Valid @RequestBody CommentDto commentDto, Authentication auth) {
		
		// 원 게시글 존재 여부 확인
		Optional<Board> opBoard = boardRepository.findById(boardId); 
		if(opBoard.isEmpty()) {
			return ResponseEntity.status(404).body("해당 게시글이 존재하지 않습니다.");
		}
		
		// 로그인한 유저의 Siteuser 객체 가져오기
		SiteUser user = userRepository.findByUsername(auth.getName()).orElseThrow();
		
		Comment comment = new Comment();
		comment.setBoard(opBoard.get());
		comment.setAuthor(user);
		comment.setContent(commentDto.getContent());
		
		commentRepository.save(comment); // 작성된 comment 엔티티 db에 저장
		
		return ResponseEntity.ok(comment);
	}
	
}
