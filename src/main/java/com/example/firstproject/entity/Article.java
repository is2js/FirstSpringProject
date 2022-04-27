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
}
