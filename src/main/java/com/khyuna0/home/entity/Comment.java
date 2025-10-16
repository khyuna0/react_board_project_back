package com.khyuna0.home.entity;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 500)
	private String content; // 댓글 내용
	
	@CreationTimestamp
	private LocalDate createDate; // 댓글 입력 시간
	
	// 로그인한 사용자의 이름 (사용자 정보)
	@ManyToOne(fetch = FetchType.LAZY) // ManyToOne 일때 필수 - 불필요한 조인 방지, 성능 향상
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@JoinColumn(name = "author_id") // 외래키 이름 지정 가능, 확인용으로 사용 가능
	private SiteUser author;
	
	// 댓글이 달릴 원 게시글 id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id")
//	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@JsonIgnore
	private Board board;
	
}
