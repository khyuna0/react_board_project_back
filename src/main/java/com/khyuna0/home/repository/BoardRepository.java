package com.khyuna0.home.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.khyuna0.home.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {

}
