package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

//5-2
@Controller
public class ArticleController {

    //5-4 브라우저 접속url 연결
    @GetMapping("/articles/new")
    public String newArticleForm() {
        //5-3. 뷰 페이지 연결
        return "articles/new";
    }

    @PostMapping("/articles/create")

//    public String createArticleForm() {
    //5-6. 뷰 페이지의 post form에 맞게 dto을 만들고 -> 받는 컨트롤러의 파라미터로 넣어준다.
    public String createArticleForm(ArticleForm form) {
        System.out.println("form = " + form);
        return "articles/new";
    }
}
