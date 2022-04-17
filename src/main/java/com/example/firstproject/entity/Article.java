package com.example.firstproject.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

//8-2. 롬복 애노테이션으로 필드받는 생성자/toString 코드 대체
@AllArgsConstructor
@NoArgsConstructor // 파라미터가 없는 default 생성자
@ToString
@Entity
public class Article {

    @Id
    @GeneratedValue // 1,2,3, ...  자동생성 어노테이션, 외부에서는 null을 주입해주면 됨.
    private Long id;

    @Column
    private String title;

    @Column
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }
}
