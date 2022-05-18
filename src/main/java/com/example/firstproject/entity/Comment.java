package com.example.firstproject.entity;

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

    //20-8. 여기까지 댓글(many) entity를 완성하고, 웹을 실행후,
    // 1) DDL log에 댓글 테이블이 만들어지는지 확인한다.
    // 2) h2콘솔에서도 생성되는지 확인한다.
    // 3) @JoinColumn에서 부모(일)entity의 id가 저장될 칼럼명을 변경해보기도 한다.

    //20-9. 테이블이 완성되었으면 -> 더미데이터를 data.sql을 통해 추가한다.
}
