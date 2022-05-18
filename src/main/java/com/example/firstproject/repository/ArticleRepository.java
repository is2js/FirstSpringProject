package com.example.firstproject.repository;

import com.example.firstproject.entity.Article;
import java.util.ArrayList;
import org.springframework.data.repository.CrudRepository;

public interface ArticleRepository extends CrudRepository<Article, Long> {

    //10-2-1-2
    @Override
//    Iterable<Article> findAll();
    ArrayList<Article> findAll(); // 원래 Iterable응답을 List응답으로 응답형을 오버라이딩할 수 있다.
}
