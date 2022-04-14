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

    //6-11. repository는 controller에서 상태값으로 가지고 있는다. (모든 route에서 쓰기 때문?)
    //6-12. repository패키지를 만들고,  ArticleRepository를 (구현된 클래스)가 아닌 인터페이스로 만든다.!
    // --> 스프링부트가 기능이 완성된 <class가 아닌 Repository interface>를 제공해주며, 이것을 구현없이 그대로 활용하려면,
    // --> Class가 아닌 인터페이스로 extends해야 구현없이 인티페이스를 활용할 수 있다.
    // ---> 6-13
    // -> springboot가 base가불변하는 CrudRepository를 제공해서, 인터페이스가 extends해서 base기능을 그대로 갖다쓴다.
    // -> repository 구현체들을 만들어서 주입시킬 필요도 없이, @어노테이션만 달아주면 알아서 초기화해준다.

    // 6-15. copntroller에서는 인터페이스=추상체 repository를 필드값 변수로 가지되,
    // -> private Repository인터페이스 repository인터페이스; (추상체변수)
    // = new 구상체 초기화는 생략하고 @애노테이션으로 자동초기화시켜 springboot제공 기능을 가진 구상체로 자동 초기화시킨다.
    // -> 필드값으로 가지니, create route에서 crud인페상속repository객체. save()의 빨간줄이 사라졌다. 기능을 물려받기 때문이다.
    // --> 하지만, 아직 인터페이스로서 틀만 제공하는 중이니, 구체적인 구현체를 넣어주는 작업을 @애노테이션으로 대신한다.
//    private ArticleRepository articleRepository;

    // 6-16. 원래 java에서는 추상체필드값에 = new 구현체();객체를 생성해줘야한다.
//    private ArticleRepository articleRepository = new articleRepositoryImpl();
    // -> 하지만, 스프링부트가 자체적으로 구현체를 주입해준다.
    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping("/articles/new")
    public String newArticleForm() {
        return "articles/new";
    }

    @PostMapping("/articles/create")

    public String createArticleForm(ArticleForm form) {
        // dto로 받아온 데이터를 저장하려면??? dto -> entity + entity -> repository -> db
        //6-1. dto를 toEntity로 변환한다 ( toXXXX -> return XXXX -> XXXX를 알고 사용한다 -> toDomain도 가능.. toDto가 불가능)
        // -> entity class가 존재한다고 가정하고 빨간색으로 작성한다.
        //form빼고 다 빨간색: Article ariticle = form.toEntity();
        // Article부터 entity패키지안에서 만든다.
        Article article = form.toEntity();

        //6-17. 이제 2가지 작업의 확인을 위해 각각 아래에 dto -> entity 와, db저장후 받아온 entity를  .toSring()하여 출력해보자.
        // dto, entity등은 찍어봐야하니 항상 toString정의해주기
        System.out.println("article.toString() = " + article.toString());

        //6-9 repository에게 entity를 db안에 저장하게 한다.
        // -> repository가 있다고 가정하고 빨간색으로 작성한다.
        // -> entity를 가지고 db에 하게 한다 save한다.
        //    articleRepository.save(article);
        // -> 최종적으로 save된 이후 save된 데이터를 반환받도록 한다.
        // -> db에 저장후 return되는 데이터는 똑같은 entity class로 return된다.
        //Article saved = articleRepository.save(article);
        // 6-10 articleRepository 객체가 존재안한다. -> repository는 dao처럼 (나중엔 외부에서 주입받아) controller의 필드로 가지고 있는다?!
        Article saved = articleRepository.save(article);

        System.out.println("saved.toString() = " + saved.toString());

        return "articles/new";
    }
}
