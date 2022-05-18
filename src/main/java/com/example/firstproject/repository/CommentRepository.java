package com.example.firstproject.repository;

import com.example.firstproject.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

//20-10. 스프링의 repository는 interface를 상속한 interface를 만들어서
//      -> class없이도 객체주입만으로 바로 쓸 수 있으며
//      -> interface에서 메서드를 오버라이딩해서 응답형 등 바꿔서 쓸 수 도 있다.
//      -> 스프링의 repository 상속에서는 2개의 제네릭을 지정해주는데
//      -> < 사용할 entity, Id type>을 넣어주면 된다.
// - 여기서는 CrudRepository보다 더 추상화되어 기능이 더 많은 JpaRepository를 상속해서 쓴다.
public interface CommentRepository extends JpaRepository<Comment, Long> {
    //20-11. 다 entity에 대한 repository에서 CRUD이외에 아래와 같은 메서드를 정의해줄 수 있다.
    // (1) 특정 게시글(부모)의 모든 댓글(묶음 자식) 조회
    // (2) 특정 닉네임(자식데이터)의 모든 댓글들(where 조회)

    // 20-12. (1) 특정 게시글(부모)의 모든 댓글(묶음 자식) 조회
    // -> 응답은 comment(entity) list
    // -> 메서드내부에서 sql + jdbcTempalte을 이용하는게 아니라
    // --> @Query( ,nativeQuery = true) 애노테이션에 수행할 sql문만 작성해주면 된다.
    //     namedParameter형식으로 :변수에  파라미터로 넘어오는 id값을 주면 된다.
    @Query(value = ""
        + "SELECT * "
        + "FROM comment "
        + "WHERE article_id = :articleId",
        nativeQuery = true)
    List<Comment> findByArticleId(Long articleId);

    //20-13. 네이티브쿼리를 @Query가 아닌, XML로 작성해보자.
    // (2) 특정 닉네임(자식데이터)의 모든 댓글들(where 조회)
    // -> 폴더가 정해져있다. resources > (new) META-INF > (new) orm.xml
    // -> orm.xml틀은 구글링하면 된다 "orm native query orm.xml example"
    // -> 양식 중 name에는 class.메서드명
    // -> 양식 중 result-class에는 root부터 반환형 class를 지정해주는데, 우클릭>copy path/reference > copy reference를 해서 복붙해주자.
    List<Comment> findByNickname(String nickname);
}
