package com.example.firstproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.firstproject.dto.ArticleDto;
import com.example.firstproject.entity.Article;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest // 해당 클래스는 스프링부트와 연동되서 통합테스트를 수행한다.
class ArticleServiceTest {

    @Autowired
    ArticleService articleService;

    @DisplayName("")
    @Test
    void index() {
        //2. 예상given
        // -> 실제데이터와 예상되는 데이터를 비교한다. articleService.index(); 후 예상되는 데이터는 무엇일까? (없으면 에상데이터를 만들어줘야한다)
        // -> data.sql에 만들어둔 초기데이터 -> db속 데이터 -> 비교가능한 것으로 꺼내면 Entity로 만들어져있을 것
        final Article a = new Article(1L, "가가가가", "1111");
        final Article b = new Article(2L, "나나나나", "2222");
        final Article c = new Article(3L, "다다다다", "3333");
        // -> 같은 카테고리는 list로 묶어야 비교가 편하다
        // --> (1) 묶을 땐, 연산(add/remove)가능한 Arrays.asList()로 묶고  <-> 불변List.of()
        // --> (2) 연결을 끊기 위해선 new ArrayList<>() 로 한번더 씌워주자.
        // ---> cf) 연산불가능+끊기는 List.copyOf() ?
        // --> 연산(add/remove)가능한 list는 new로 만들자. 그외에는 List.of() ?
        //new ArrayList<Article>(Arrays.asList(a,b,c));
        final List<Article> expected = new ArrayList<>(Arrays.asList(a, b, c));

        //1. 실제when
        final List<Article> articles = articleService.index();

        //비교then
        // -> 2 list가 같은 것을 toString()으로???
        assertEquals(expected.toString(), articles.toString());
    }

//    @DisplayName("")
//    @Test
//    void index_fail() {
//        //2. 예상given
//        final Article a = new Article(1L, "가가가가", "1111");
//        final Article b = new Article(2L, "나나나나", "2222");
//        // 일부러 틀리게 id를 4번으로 주지
////        final Article c = new Article(3L, "다다다다", "3333");
//        final Article c = new Article(4L, "다다다다", "3333");
//        final List<Article> expected = new ArrayList<>(Arrays.asList(a, b, c));
//
//        //1. 실제when
//        final List<Article> articles = articleService.index();
//
//        //3. 비교then
//        assertEquals(expected.toString(), articles.toString());
//
//        //4. list vs list를 .toString()으로 비교시 실패하면?
//        // -> 대문자 Expected(expected와 동일한 값, 만들어준 정답 값) vs Actual(실제 값)
////        expected: <[Article(id=1, title=가가가가, content=1111), Article(id=2, title=나나나나, content=2222), Article(id=4, title=다다다다, content=3333)]> but was: <[Article(id=1, title=가가가가, content=1111), Article(id=2, title=나나나나, content=2222), Article(id=3, title=다다다다, content=3333)]>
////        Expected :[Article(id=1, title=가가가가, content=1111), Article(id=2, title=나나나나, content=2222), Article(id=4, title=다다다다, content=3333)]
////        Actual   :[Article(id=1, title=가가가가, content=1111), Article(id=2, title=나나나나, content=2222), Article(id=3, title=다다다다, content=3333)]
//    }


    @DisplayName("")
    @Test
    void show_성공___존재하는_id_입력() {
        //2.(실제를)예상
        // 1) 실제에 쓰일 인자를, 예상에서 사용자가 입력했다고 가정하고 만들어준다.
        final Long id = 1L;
        // 2) 실제에 주어진 인자를 바탕으로 -> 실제 값을 예상해서 예상값을 만들어 준다
        final Article expected = new Article(id, "가가가가", "1111");

        //1.실제 - 인자가 필요하면 변수로 둔다.
        final Article article = articleService.show(id);

        //3.비교
        assertEquals(expected.toString(), article.toString());
    }

    @DisplayName("")
    @Test
    void show_실패___존재하지않는_id_입력() {
        //2.(실제를)예상값
        // 1) 실제에 쓰일 인자를, 예상에서 사용자가 입력했다고 가정하고 만들어준다.
//        final Long id = 1L;
        final Long id = -1L;
        // 2) 실제에 주어진 인자를 바탕으로 -> 실제 값을 예상해서 예상값을 만들어 준다
        // -> id가 -1이면.. 예상하는 정답은 Entity에 service가 null로 응답하는 것이다!
        // -> service는 잘못된요청을 null로 응답하고, controller가 null가지고 비교해서 응답한다.
        // -> 만약
//        final Article expected = new Article(id, "가가가가", "1111");
        final Article expected = null;

        //1.실제 - 인자가 필요하면 변수로 둔다.
        final Article article = articleService.show(id);

        //3.비교
        // - null에 .toStirng() 등 메서드호출하면 NPE이 뜨니.. -> NPE : null이 넘어와서 메서드 호출됬다!고 생각하자.
//        assertEquals(expected.toString(), article.toString());
        assertEquals(expected, article);
    }


    @DisplayName("")
    @Test
    @Transactional
    void create_성공____title과_content만_있는_dto_입력() {
        //2. 예상
        // -> (1) dto를 일단, 현재 케이스에 맞게 [title+content만 있는 dto] 만들어줘야한다
        final String title = "제목";
        final String content = "내용";
        // dto는 없는 데이터는 그냥 null을 넣어서 만들어주자.
        final ArticleDto dto = new ArticleDto(null, title, content);
        // -> (2) 이제 실제값을 예상한 값인, [[[[id = 4]]]]가 박힌 entity를 만들어줘야한다.
        final Article expected = new Article(4L, title, content);

        //1. 실제
        final Article article = articleService.create(dto);

        //3. 비교를 toString()을 찍어 내용으로 비교해보자.
        // - comparable 안해줄때 비교가능한 방법.
        assertEquals(expected.toString(), article.toString());
    }

    @DisplayName("")
    @Test
    void create_실패____id가_포함된_dto_입력() {
        //2. 예상
        final String title = "제목";
        final String content = "내용";
        final ArticleDto dto = new ArticleDto(4L, title, content);
        //-> 실패하면 null을 응답하도록 service를 만들어뒀으니, null을 실제를 예상한 정답값으로 변수에 받아준다.
        final Article expected = null;

        //1. 실제
        final Article article = articleService.create(dto);

        //3. 비교
        assertEquals(expected, article);
    }

    //과제
    //@Transactional
    //update_성공____존재하는_id와_title_content가_있는_dto_입력()
    //update_성공____존재하는_id와_title만_있는_dto_입력()
    //update_실패____존재하지_않는_id의_dto_입력()
    //update_실패____id만_있는_dto_입력()

    //@Transactional
    //delete_성공____존재하는_id_입력()
    //delete_실패____존재하지_않는_id_입력()
}
