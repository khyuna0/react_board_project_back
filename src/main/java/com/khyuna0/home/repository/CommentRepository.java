package com.khyuna0.home.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.khyuna0.home.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	
}
