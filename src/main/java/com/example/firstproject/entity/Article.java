package com.example.firstproject.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

//6-3. entity는 dto와 필드는 같으나, db가 인식해야한다는 점에서 class에서부터 @Entity 애노테이션을 달아준다
@Entity
public class Article {

    //6-5. 빨간줄 자동완성으로서 테이블과 일치하도록 id도 만들어줘야한다. 
    // 대표값이라고 하며, jpa의 repository사용시 대표값의 Type도 명시해줘야한다.
    @Id
    //6-6. id를 autoincrement시키려면 @GeneratedValue -> 외부에서는 null값으로 줄 예정임.
    @GeneratedValue // 1,2,3, ...  자동생성 어노테이션
    private Long id;

    //6-2. dto와 field를 거의 동일하게 가지고 간다.
    //6-4. class에는 @Entity를, 필드에는 @Column을 달아줘야한다.
    @Column
    private String title;

    @Column
    private String content;

    //6-7. 칼럼들을 다 정의해줬다면, 칼럼(필드)를 채워 entity를 생성시켜주는 생성자도 정의해줘야한다.
    // -> 지금은 호출안하는 것 같지만, toEntity()의 결과가 Article(Entity)이며, 내부에서 return하려면 생성자로 new Aritcle을 만들어야하므로
    // my) to객체 변환메서드는, 내부에서 return new 변환할객체생성자( ) 를 호출해서 바뀌는 것.
    // + toString까지 정의해주기
    public Article(final Long id, final String title, final String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    //
    @Override
    public String toString() {
        return "Article{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", content='" + content + '\'' +
            '}';
    }
}
