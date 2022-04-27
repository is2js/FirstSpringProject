package com.example.firstproject.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor // 파라미터가 없는 default 생성자
@ToString
@Entity // 해당 클래스로 -> DB가 테이블을 만든다.
@Getter
public class Article {

    @Id
    //@GeneratedValue // 1,2,3, ...  자동생성 어노테이션, 외부에서는 null을 주입해주면 됨.
    //15-3. DB에 이미 생성된 데이터를 고려해서 DB가 번호 +1씩 번호부여하도록 전략변경
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String content;

    public void patch(final Article article) {
        //17-9. 수정안되서 requestbody에 안들어간 칼럼은 null로 차있다. -> patch로 붙여준다는 것은
        // -> 각 칼럼들에 대해 != null로 존재하는 칼럼만 붙여준다(갱신)는 뜻이다.
        // -> 같은 entity객체내 비교라서, 필드를 편하게 꺼내서 확인하면된다.
        if (article.title != null) {
            // 데이터가 존재한다면, target Entity에 넣어줘 없음 말고...
            this.title = article.title;
        }
        if (article.content != null) {
            this.content = article.content;
        }
    }
}
