package com.example.firstproject.dto;

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
}
