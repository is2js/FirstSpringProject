package com.example.firstproject.dto;

import com.example.firstproject.entity.Article;

//5-5
public class ArticleForm {

    private String title;
    private String content;

    public ArticleForm(final String title, final String content) {
        this.title = title;
        this.content = content;
    }

    @Override
    public String toString() {
        return "ArticleForm{" +
            "title='" + title + '\'' +
            ", content='" + content + '\'' +
            '}';
    }

    //6-8. toEntity메서드는 테이블과 1:1매칭되는 Entityclass 객체를 생성자를 통해 만들어서 return
    // -> id는 초기화안해줘도 자동으로 1부터 올라가니, null을 대입해주면 된다.
    public Article toEntity() {
        return new Article(null, title, content);
    }
}
