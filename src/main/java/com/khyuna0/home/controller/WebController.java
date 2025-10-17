package com.khyuna0.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
   
   @RequestMapping(value = { "/", "/{path:^(?!api$).*$}/**" })
    public String forward() {
        return "forward:/index.html";
    }
//   컨트롤러가 없으면 리액트 라우터 요청이 동작 안함
//   api 요청 -> 스프링부트 요청 적용
//	 api 요청 제외한 요청 React 라우터 요청으로 변경 -> 리액트의 "/" 로 시작하는 요청으로 변경
}
