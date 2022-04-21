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

    // ---------3. edit-----------------------------------------
    // 12-2. show(개별조회) -> 이어진 -> edit 의 controller 개발  by /articles/{{article.id}}/edit
    //       - 개별조회에서부터 context가 이어지면, 화면조회도 아닌, post로 데이터를 가지고 오는 것도 아닌, url에서 id를 가지고 온다.
    //
    @GetMapping("/articles/{id}/edit")
    // 12-2-1. 템플렛엔진의 요청url에서 /articles/{{article.id}}/edit 에서 route는 {{}} 대신 -> {}로 일단 바꿔준다.
    // 12-2-2. route mapping method를 edit()로 작성하자. 템플릿엔진이라면 항상  public String이 응답이니, return "";로 먼저 작성해두자.
    //public String edit() {}
    public String edit(@PathVariable Long id, Model model) {
        //12-3. 웹에서 `edit`란 수정준비용 화면으로 개별조회id를 url으로부터 받아와 1) `수정할 데이터를 DB에서 조회`후 -> 2) `form에 채운 화면`을 뿌려주는 것
        //12-3-1. 수정할 데이터를 db에서 꺼내오면 response Id Entity
        final Article articleEntity = articleRepository.findById(id).orElse(null);

        //12-3-2. 웹에서 수정이란? db에서 조회된 데이터를 채운 form화면을 뿌려주는 것
        // -> model에 데이터를 넣어주면 됨.
        model.addAttribute("article", articleEntity);

        //12-2-3.
        return "articles/edit";
    }
    // ---------edit-----------------------------------------

}
