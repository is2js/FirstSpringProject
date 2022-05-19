package com.example.firstproject.entity;

import com.example.firstproject.dto.CommentDto;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

//20-1. Comment Entity class를 만들고,
//      jpa가 자동으로 매핑해서 db 테이블을 생성하도록 @Entity로 명시해준다.
@Entity
//20-2. lombok을 통해 4가지 (getter, tostring, allArgs, NoArgsConstructor)를 만들어준다
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    //20-3. comment와 관련된 칼럼들을, entity 필드로 만들어준다.
    //20-3-1. (PK)댓글id
    //20-4. id는 @Id + auto_increment를 위한 @GeneratedValue+ 전략을 만들어준다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //20-3-2. (FK -> 댓글의 부모 게시글id) aricleId 대신, <미리 만들어진 부모게시글 entity>를 필드(칼럼)로 가진다.
    //20-5. 댓글내부 입장에선, 다대일 관계의 FK(일의 PK -> 일의 entity)에는
    //     @ManyToOne 애너테이션을 달아준다.
    @ManyToOne // 해당 댓글 엔터티 여러개가, 하나의 Article에 연관된다.!
    //20-6. entity를 가졌어도, db에는 id로 관계를 가져야한다.
    //     @JoinColumn(name = )을 통해, 연결된 entity의 id(FK)를 db에 저장할 name을 지정해준다.
    @JoinColumn(name = "article_id")
    private Article article;

    //20-3-3. 댓글은 PK/FK이외에 닉네임+내용을 가진다고 가정한다.
    //20-7. 일반 칼럼들은 @Column만 달아준다.
    @Column
    private String nickname;

    @Column
    private String body;

    // setter형태는 싼것이라도 의존할 수 밖에 없으며, update로직을 entity내부에 생성해도 된다.
    public void patch(final CommentDto dto) {
        //21-35.
        //  예외발생: 수정uri속 id(->조회후 entity속 id) vs json으로 날아온 수정요청 body 속 id 같은지 검증
        if (id != dto.getId()) {
            throw new IllegalArgumentException("댓글 수정 실패! 잘못된 id가 입력되었습니다.");
        }
        //21-36. 수정요청 body(dto)에 [수정을 원하여 보내진 데이터가 존재할때만 = not null일때만] entity필드를 업데이트
        //   - 상위도메인을 빼고
        if (dto.getNickname() != null) {
            this.nickname = dto.getNickname();
        }
        if (dto.getBody() != null) {
            this.body = dto.getBody();
        }
    }

}
