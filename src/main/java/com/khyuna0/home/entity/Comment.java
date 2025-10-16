package com.khyuna0.home.entity;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	@ManyToOne
	private SiteUser author;
	
	// 댓글이 달릴 원 게시글 id
	@ManyToOne
	private Board board;
	
}
