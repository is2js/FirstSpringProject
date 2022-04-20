package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    // ---------1. create-----------------------------------------
    @GetMapping("/articles/new")
    public String newArticleForm() {
        return "articles/new";
    }

    @PostMapping("/articles/create")
    public String createArticleForm(ArticleForm form) {
        //1) dto 확인
        log.info(form.toString());

        //2) dto to nullId Entity
        Article article = form.toEntity();
        log.info("article.toString() = " + article.toString());

        //3) post확인용  responseId Entity
        Article saved = articleRepository.save(article);
        log.info("saved.toString() = " + saved.toString());

        //11-2. redirect는 ""empty의 whiteLabel처럼, String으로 redirect한다.
        // -> 이 때, 개별 create는 개별 데이터 생성이므로 -> 개별 데이터 read로 redirect한다.
        // -> 이 때, create후 받은 responseId entity에서 Id를 가져와야하므로 업으면 롬복으로 getter를 만들자
        final Long id = saved.getId();
//        return "";
        return "redirect:/articles/" + id;
    }
    // ---------create-----------------------------------------

    // ---------2. read-----------------------------------------
    @GetMapping("/articles/{id}")
    public String show(@PathVariable Long id, Model model) {
        log.info("id = " + id);
        final Article articleEntity = articleRepository.findById(id).orElse(null);

        model.addAttribute("article", articleEntity);

        return "articles/show";
    }

    @GetMapping("/articles")
    public String index(Model model) {
        final List<Article> articleEntityList = articleRepository.findAll();

        model.addAttribute("articleList", articleEntityList);

        return "articles/index";
    }
    // ---------read-----------------------------------------
}
