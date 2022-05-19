package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleDto;
import com.example.firstproject.dto.CommentDto;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import com.example.firstproject.service.CommentService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    //22-7. 하위도메인의 CRUD는 service단위로 하기 위해 service를 주입한다.
    @Autowired
    private CommentService commentService;

    // ---------1. create-----------------------------------------
    @GetMapping("/articles/new")
    public String newArticleForm() {
        return "articles/new";
    }

    @PostMapping("/articles/create")
    public String createArticleForm(ArticleDto form) {
        log.info(form.toString());

        Article article = form.toEntity();
        log.info("article.toString() = " + article.toString());

        Article saved = articleRepository.save(article);
        log.info("saved.toString() = " + saved.toString());

        final Long id = saved.getId();

        return "redirect:/articles/" + id;
    }
    // ---------create-----------------------------------------

    // ---------2. read-----------------------------------------
    @GetMapping("/articles/{id}")
    public String show(@PathVariable Long id, Model model) {
        log.info("id = " + id);
        final Article articleEntity = articleRepository.findById(id).orElse(null);

        //22-6. 하위도메인CRUD는 상위repo가 포함된 복잡CRUD라서, repo가 아닌 개발된 service단위로 데이터를 처리한다.
        // 댓글데이터 전체조회(comments) with 상위도메인 id
        final List<CommentDto> commentDtos = commentService.comments(id);

        model.addAttribute("article", articleEntity);
        //22-8. 템플릿 뷰로한꺼번에 넣어주기 위해 하위도메인 dto list도 담아서보낸다.
        model.addAttribute("commentDtos", commentDtos);

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

    @PostMapping("/articles/update")
    public String update(ArticleDto form) {
        log.info(form.toString());

        final Article articleEntity = form.toEntity();
        log.info(articleEntity.toString());

        final Article target = articleRepository.findById(articleEntity.getId()).orElse(null);
        if (target != null) {
            articleRepository.save(articleEntity);
        }

        return "redirect:/articles/" + articleEntity.getId();
    }
    // --------------------------------------------------


    // ---------4. delete-----------------------------------------
    @GetMapping("/articles/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes rttr) {
        log.info("삭제 요청이 들어옴.");

        //14-2 삭제대상을 가져온다 - 있는 것만 삭제해야함
        final Article target = articleRepository.findById(id).orElse(null);
        log.info(target.toString());

        //14-3 그 대상을 삭제한다
        if (target != null) {
            articleRepository.delete(target);
            //14-5 update나 delete처럼, id를 통해 찾고 난 뒤 -> 있으면(notNull시) 수행에 대해
            // -> 수행될수도 있고 안될수도있으니, 수행된 경우, 1회성 flash msg를 redirect페이지로 넘기자.
            // -> addFlashAttribute만 해주면, 알아서 redirec되는 html(index.mustache)로 날려준다.
            // --> 어떤 페이지든지 받을 수 있게, 어떤 페이지든지 공통인 header의 아랫부분에 머스태취 문법으로 사용한다.
            rttr.addFlashAttribute("msg", "삭제가 완료되었습니다.");
        }
        //14-3-2 삭제확인은 실제db에서 한다.

        //14-4 결과페이지로 redirect한다.
        return "redirect:/articles";
    }

    // --------------------------------------------------
}
