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
    @GetMapping("/articles/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        final Article articleEntity = articleRepository.findById(id).orElse(null);

        model.addAttribute("article", articleEntity);

        return "articles/edit";
    }

    //13-2. @PatchMaping대신 임시로 PostMapping으로 한다. form태그 때문
    //   + post form데이터는 Dto-ArticleForm form을 파라미터로 받는다.
    //       -> 만약 @RestController라면, @RequestBody로? -> 이것도 객체(dto)로 받을 수 있는 듯하다.
    @PostMapping("/articles/update")
    //13-3. 생성form으로 만든 수정form이지만, id도 같이보냄에 따라, dto도 id필드를 추가해주자.
    public String update(ArticleForm form) {
        // post되는 데이터(dto)는 일단 한번 찍어준다.
        log.info(form.toString());

        //13-4
        //13-4-1) dto toEntity
        final Article articleEntity = form.toEntity();
        log.info(articleEntity.toString());

        //13-4-2) entity save db
        // -> id없이 save시도하여 db에서 id가 자동배정되는 create와 달리,  -> update는 [id] + [수정완료된 1row 데이터]를 가진 dto to Entity로 가지고 있다.
        // 1) `[id]로 찾아서 존재하는 데이터인지` db에 물어보고, db에 해당 데이터가 없으면 null을 받는다.
        final Article target = articleRepository.findById(articleEntity.getId()).orElse(null);
        // 2) if null이 아닌 경우 == 데이터가 있는 경우에만, **해당id의 데이터 [수정완료된 1row데이터]로 update**를 통해 db에 저장한다.
        if (target != null) {
            articleRepository.save(articleEntity);
        }
        // 3) 수정완료되었으면, 실제 db(h2)에서 수정된 데이터를 확인해본다.

        //13-4-3) 개별조회id 중 edit은 개별조회show로 redirect시켜줘야한다.
        return "redirect:/articles/" + articleEntity.getId();
        //13-5 실제 db에서도 update 쳐보기
//        UPDATE article
//        SET
//            title = '가나다라',
//            content = 'AAAA'
//        WHERE
//            id = 1;
    }
    // --------------------------------------------------

}
