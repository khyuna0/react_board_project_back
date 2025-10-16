package com.khyuna0.home.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
	
	@NotBlank ( message = "댓글을 입력해 주세요.")
	@Size( min = 1, message = "댓글은 최소 1글자 이상이어야 합니다.")
	private String content;
}
