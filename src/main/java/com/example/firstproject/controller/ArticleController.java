package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
//8-3. controller속 log.info()사용을 위한 애노테이션
@Slf4j
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping("/articles/new")
    public String newArticleForm() {
        return "articles/new";
    }

    @PostMapping("/articles/create")
    public String createArticleForm(ArticleForm form) {
        //8-4. println대신 log.info()를 쓸 수 있게 된다.
//        System.out.println("form.toString() = " + form.toString());
        log.info(form.toString());

        Article article = form.toEntity();
        log.info("article.toString() = " + article.toString());

        Article saved = articleRepository.save(article);
        log.info("saved.toString() = " + saved.toString());

        return "";
    }
}
