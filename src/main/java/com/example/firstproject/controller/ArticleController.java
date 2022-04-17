package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
//8-3. controller속 log.info()사용을 위한 애노테이션
@Slf4j
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    // ---------create-----------------------------------------
    @GetMapping("/articles/new")
    public String newArticleForm() {
        return "articles/new";
    }

    @PostMapping("/articles/create")
    public String createArticleForm(ArticleForm form) {
        log.info(form.toString());

        Article article = form.toEntity();
        log.info("article.toString() = " + article.toString());

        Article saved = articleRepository.save(article);
        log.info("saved.toString() = " + saved.toString());

        return "";
    }
    // ---------create-----------------------------------------

    // ---------read-----------------------------------------
    //9-1. 조회는 view없이 url로 조회요청이오며 get으로 받아준다. 가변은 {}중괄호로 받아준다.
    //9-2. form으로 오는 데이터는 파라미터에서 dto(xxxForm)클래스로 받아주는 반면
    //    -> url속 가변 파라미터는 id라면 Long타입으로 @PathVariable 어노테이션을 때려줘야한다.
    @GetMapping("/articles/{id}")
//    public String show(@PathVariable Long id) {
    //9-3-2-1 그냥 view템플릿만 뿌리는 getter가 아니라, 데이터를 가진체 뿌리려면
    //        -> 파라미터에서 Model model을 받아와서, model에 entity를 attribute로 넣어서 뿌려줘야한다.
    public String show(@PathVariable Long id, Model model) {
        // 제대로 url속가변id를 잘 받아오는지 찍자 -> string이 아닌 변수를 찍을 때는, 설명없이 "" + 를 활용해도 된다.
        log.info("id = " + id);

        //9-3. url조회요청 속 id를 받아온 뒤, 해야할 작업 3개를 확인한다.
        //9-3-1) id로 데이터를 가져오기
        // -> db에서 데이터를 가져오는 주체는 Repository다
        // -> db에서 가져온 데이터 응답은 entity이다.
        // -> 없을 수 도 있으니 Optional<Entity>로 가져온다.
        // -> java8아니라면, optioncal을 지우고, .orElse(null);를 너허주면 된다.
//        final Optional<Article> article = articleRepository.findById(id);
        final Article articleEntity = articleRepository.findById(id).orElse(null);

        //9-3-2) 가져온 데이터를 Model에 등록하기
        // -> view 템플릿에 응답해주기 위해선 Model에 담아서 보내야하며,
        // -> 파라미터에서 Model model로 받아서 가져오면 된다.
        // -> article이름으로 article Entity를 뷰템플릿으로 넘겨줄 예정이다.
        // -> 따로 직접 안넘겨줘도 model은 알아서 view 템플릿으로 데이터를 넘겨준다.
        model.addAttribute("article", articleEntity);

        //9-3-3) 보여줄 view템플릿 controller return  설정해놓고 -> 생성하러 가기
        return "articles/show";
    }
}
