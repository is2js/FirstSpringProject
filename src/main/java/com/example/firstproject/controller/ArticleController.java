package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping("/articles/new")
    public String newArticleForm() {
        return "articles/new";
    }

    @PostMapping("/articles/create")

    public String createArticleForm(ArticleForm form) {
        //7-1. dto와 entity, save후 응답entity는 toString을 정의한 뒤 dto,entity.toString().soutv로 확인하자.
        System.out.println("form.toString() = " + form.toString());

        Article article = form.toEntity();
        System.out.println("article.toString() = " + article.toString());

        Article saved = articleRepository.save(article);
        System.out.println("saved.toString() = " + saved.toString());

        return "articles/new";
    }
}
