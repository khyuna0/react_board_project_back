package com.khyuna0.home.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Board {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 게시판 번호
	
	private String title; // 제목
	private String content; // 내용
	
	@CreationTimestamp // 자동으로 insert 시 현재 날짜 시간 삽입
	private LocalDateTime createDate; // 게시판 글 쓴 날짜
	
//	@UpdateTimestamp 업데이트 시 시간 자동 삽입
	
	@ManyToOne
	private SiteUser author; // siteUser 조인, N : 1 , 게시판 글쓴이
	
	@OneToMany(mappedBy = "board", cascade = CascadeType.ALL , orphanRemoval = true)
	private List<Comment> comments = new ArrayList<>();
}
