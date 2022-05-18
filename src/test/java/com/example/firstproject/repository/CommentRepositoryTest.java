package com.example.firstproject.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.firstproject.entity.Article;
import com.example.firstproject.entity.Comment;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest // 20-13. 스프링부터까지 할 필요없이 Jpa와 연동된 테스트
class CommentRepositoryTest {

    //빈 객체는 주입받아 사용한다.
    @Autowired
    private CommentRepository commentRepository;


    @DisplayName("특정 게시글의 모든 댓글 조회")
    @Test
    void findByArticleId() {
        // 20-14. servicetest와 달리, 4단계로 테스트를 수행해보자.
        // + 중괄호//**로 묶어서 Case를 나눠서도 시행해보자.
        /* Case 1: 4번 게시글의 모든 댓글 조회 */
        {
            // 입력 데이터준비
            // 실제 수행
            // 예상하기
            // 검증
        }

        /* Case 1: 4번 게시글의 모든 댓글 조회 */
        {
            // 2. 입력 데이터준비
            final Long articleId = 4L;

            // 1. 실제 수행
            // -> 메서드의 인자로 들어가는 것은 given(입력데이터 준비)에 변수로 만들어준다.
            final List<Comment> comments = commentRepository.findByArticleId(articleId);

            // 4. 예상하기
            // -> expected된 comments를 확인하기 위해서는 db를 먼저 확인해야한다.
            // -> db를 확인해야만 예상데이터를 뽑을 수 있는 경우, 4단계로 준비한다?!
            // -> 콘솔을 열고, comment table로 가서, 4번 게시글의 코멘트들을 확인한 뒤, 걔네들을 예상데이터로 만들어주자.
            // --> 1개만 확인해서 comment를 만들고 복붙해서 나머지들을 완성하자. / 내용물은 db조회된 내용을 복붙하자.
            //final Comment a = new Comment(1L, article, "Park", "굳 월 헌팅");
            //final Comment b = new Comment(2L, article, "Kim", "아이 엠 샘");
            //final Comment c = new Comment(3L, article, "Choi", "쇼생크 탈출");
            // ---> db에는 fk(article_id)인데, Comment객체는 article entity를 들고 있다.
            // ----> 부모 entity이므로, 더 위에 해당 부모 1개 entity를 만들어주고,
            // ----> db 속 부모 table에서 해당 부모데이터를 확인해서 복붙하여 완성하면 된다.
            final Article article = new Article(4L, "당신의 인생 영화는?", "댓글ㄱ");
            final Comment a = new Comment(1L, article, "Park", "굳 월 헌팅");
            final Comment b = new Comment(2L, article, "Kim", "아이 엠 샘");
            final Comment c = new Comment(3L, article, "Choi", "쇼생크 탈출");
            // 응답형을 맞추려고 list로 만들어주면 된다. 가변이면 Arrays.asList() 아니면 List.of()인데, 아무거나 써도 될 듯.
            final List<Comment> expected = Arrays.asList(a, b, c);

            // 3. 검증
            // -> expected도 만들어줘야한다. (원래느 데이터준비given에서 다해줬었음)
//            assertThat(expected.toString()).isEqualsTo(comments.toString())
            assertEquals(expected.toString(), comments.toString(), "4번 글의 모든 댓글들을 출력");
        }

        //20-15. 한 메서드에 대해 case를 중괄호로 추가해서 테스트한다?!
        // -> 틀을 복사한 뒤, 값만 바꿔주면 된다. 해당 테스트의 정보는 assertEquals안에 3번째 인자에 문자열로 설명되어있다.
        // -> comment 테이블을 봤더니, 1번 부모 게시글을 가리키는 댓글은 없다. -> 비어있게 나온다.
        /* Case 2: 1번 게시글의 모든 댓글 조회 */
        {
            // 2. 입력 데이터준비
            final Long articleId = 1L;

            // 1. 실제 수행
            // -> 메서드의 인자로 들어가는 것은 given(입력데이터 준비)에 변수로 만들어준다.
            // -> 내용물없이 Arrays.asList()를 통해 빈 리스트를 만들면 된다.
            final List<Comment> comments = commentRepository.findByArticleId(articleId);

            // 4. 예상하기
            final Article article = new Article(1L, "가가가가", "1111");
            final List<Comment> expected = Arrays.asList();

            // 3. 검증
            assertEquals(expected.toString(), comments.toString(), "1번 글은 댓글이 없음");
        }

        //과제
        /* Case 3: 9번 게시글의 모든 댓글 조회 */
        {
        }
        /* Case 4: 9999번 게시글의 모든 댓글 조회 */
        {
        }
        /* Case 5: -1번 게시글의 모든 댓글 조회 */
        {
        }
    }

    @DisplayName("특정 닉네임의 모든 댓글 조회")
    @Test
    void findByNickname() {
        /* Case 1: "Park"의 모든 댓글 조회 */
        {
            // 2. 입력 데이터 준비
            final String nickname = "Park";

            // 1. 실제 수행
            final List<Comment> comments = commentRepository.findByNickname(nickname);

            // 3. 예상하기 -> db를 보고 "Park"의 모든 댓글 조회해서 보면서, entity를 만들어 예상해야한다.
            // -> **부모entity를 가졌다면, 부모가 필요없는 상황(부모1개에 대한 자식 모음)이라도 의미없이, 대응하는 부모entity들을 만들어줘야 자식 entity 생성이 가능하다.**
            // --> **db 조회 확인하여 fk에 대응하는 부모를 개별로 다 만들어줘야한다.**
//            ID  	BODY  	NICKNAME  	ARTICLE_ID
//            1	굳 월 헌팅	Park	4
//            4	치킨	Park	5
//            7	조깅	Park	6
            // ---> 예상 자식부터 db조회만들고 -> 그에 따른 대응 부모들을 하나씩 만들어준다.
            // 1) Park 댓글은 PK 1,4,7번이다.
            //final Comment a = new Comment(1L, article, "Park", "굳 월 헌팅");
            //final Comment b = new Comment(4L, article, "Park", "치킨");
            //final Comment c = new Comment(7L, article, "Park", "조깅");
            // 2) Park 댓글들에 대응하는 부모들은 4,5,6 번 aritcle이다.
            // --> 공통이 아니므로 맨 뒤에 변수로 빼는 것이 아니라, 각 필요한 자리에 생성자를 통해 만들어주자.
            // --> 부모의 내용도 알아야해서 부모 tablke도 조회해서 그 article의 내용까지 알아야한다.
            // select * from article where id in (select article_id from comment where nickname = 'Park');
//            final Comment a = new Comment(1L, new Article(4L, "당신의 인생 영화는?", "댓글ㄱ"), "Park", "굳 월 헌팅");
//            final Comment b = new Comment(4L, new Article(5L, "당신의 인생 푸드는?", "댓글ㄱㄱ"), "Park", "치킨");
//            final Comment c = new Comment(7L, new Article(6L, "당신의 취미는?", "댓글ㄱㄱㄱ"), "Park", "조깅");
            // ----> nickname 동일한 것으로 조회했으니, 변수로 뽑은 nickname "Park"자리에 재활용해준다.
            final Comment a = new Comment(1L, new Article(4L, "당신의 인생 영화는?", "댓글ㄱ"), nickname, "굳 월 헌팅");
            final Comment b = new Comment(4L, new Article(5L, "당신의 인생 푸드는?", "댓글ㄱㄱ"), nickname, "치킨");
            final Comment c = new Comment(7L, new Article(6L, "당신의 취미는?", "댓글ㄱㄱㄱ"), nickname, "조깅");
            // 3) asList로 묶어서 응답형과 맞춰준다.
            final List<Comment> expected = Arrays.asList(a, b, c);

            // 검증
            assertEquals(expected.toString(), comments.toString(), "Park의 모든 댓글들 출력");
        }

        //과제
        /* Case 2: "Kim"의 모든 댓글 조회 */
        {
        }
        /* Case 3: null의 모든 댓글 조회 */
        {
        }
        /* Case 4: ""의 모든 댓글 조회 */
        {
        }
        /* Case 5: i가 들어간 모든 댓글 조회 */
        {
        }
    }
}
