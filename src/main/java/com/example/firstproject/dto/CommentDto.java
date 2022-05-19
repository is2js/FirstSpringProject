package com.example.firstproject.dto;

import com.example.firstproject.entity.Article;
import com.example.firstproject.entity.Comment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

//21-10. dto 필수 코드들을 롬복으로 4가지 만들어준다.
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class CommentDto {
    //21-11. dto에서는 상위도메인 entity가 아닌 fk(Id)를 Long으로 가지게 하면 된다.
    //  id외에는 String으로 작성해야 json으로 바뀐다.
    private Long id;
    //21-30. json에서 날라오는 db형태의 snake_case형태의 fkid를
    @JsonProperty(value = "article_id")
    private Long articleId;
    private String nickname;
    private String body;

    public static CommentDto createCommentDto(final Comment comment) {
        //21-15. 상위entity를 가진 하위entity는 id가 아니라 articel객체를 가지고 있어서, 한번더 getId()를 호출해서 id를 dto로 넘겨줘야한다.
        return new CommentDto(
            comment.getId(),
            comment.getArticle().getId(),
            comment.getNickname(),
            comment.getBody()
        );
    }

    // 생성용 dto -> 빈id Entity 변환
    public Comment toEntity(final Article article) {
        //21-25. 예외발생지역인 service내에서 호출되는,  [빈id requestDto -> 빈id Entity]를 만드는 toEntity()에서 [생성 requestDto의 검증]을 해준다.
        // - service 메서드내에서 해도될 것 같지만, 그것을 사용하는 도메인-메서드 내부에서 하면 감추고 더 좋을 듯?!!
        // - service내에서라면 dto.getter로 꺼내서 검사해야할 듯 / 마침 메세지 보내서 쓰는 경우가 생겨서, 여기서 쓰는 순간에 검사한다.
        // - uri속 pathVariable의 상위도메인id -> 조회후 상위entity가 필드에 넣으려고 들어온 상태임을 인지한다.
        //(1) 예외1: requestDto내 id(pk)가 존재하면 안된다 (생성후 db에서 배정받아야함)
        // - my) 빈id Dto에 id [존재하면] == [not null이면] == [!= null 이면] -> 예외발생
        if (id != null) {
            throw new IllegalArgumentException("댓글 생성 실패! 댓글의 id가 없어야 합니다.");
        }
        //(2) 예외2: pathVarible 속 상위도메인id in 조회후 상위entity <-> requestDto 속 상위도메인id(fk) 다르면 예외
        if (articleId != article.getId()) {
            throw new IllegalArgumentException("댓글 생성 실패! 게시글의 id가 잘못되었습니다.");
        }

        // 2가지 예외(빈id + path속 상위id == requestBody속 상위id) 통과시
        // 빈idRequestDto -> 빈id entity를 생성해서 반환한다.
        // - id는 null상태이므로 그냥 id를 대입해준다.
        return new Comment(
            id,
            article,
            nickname,
            body
        );

    }

//    //21-33.
//    // 수정용 dto + 수정전target entity -> 수정완료된 Entity 변환
//    public void toEntity(final Comment comment) {
//        //21-34.dto 검증 in toEntity 등 service내 사용 메서드 내부에서 검증
//        //  uri속 id(->조회후 entity속id) vs json으로 날아온 수정요청body속 id 같은지 검증
//        if (id != comment.getId()) {
//            throw new IllegalArgumentException("댓글 수정 실패! 잘못된 id가 입력되었습니다.");
//        }
//
//        //21-35. patch는 setter로서 각 필드마다 바꿔서 변환
//        // -> json의 수정 요청body에  모든 필드가 입력안되고 수정만 원하는 필드만 입력하는 경우가 있다.
//        // -> 존재하면 수정하는 것이니 존재 equals  != null (not null)으로 검사해서 바꿔준다.
//        if (nickname != null) {
//            // comment 뭐야.. 싼것.toEntity( 비싼것 의존 )의 형태를 잡아 return new 비싼것( )로 변환하더라도
//            // 내부에서 setter 동작은 무조건 발생한다...(같은 type 객체끼리의 update연산이 아닌 이상)
//            // -> update만큼은... setter당하는 비싼것에서 메서드를 만들어주자.
//        }
//
////        return new Comment(comment.getId(), )
//    }
}
