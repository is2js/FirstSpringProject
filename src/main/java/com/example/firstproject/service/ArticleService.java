package com.example.firstproject.service;

import com.example.firstproject.dto.ArticleDto;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service // 서비스 선언! (서비스 객체를 스프링부트에 생성)
public class ArticleService {

    // my) 스프링부트에서는 알아서 객체를 만들어주므로, interface 자체를 주입받아 쓸 수 있다.
    @Autowired //DI
    private ArticleRepository articleRepository;

    public List<Article> index() {
        return articleRepository.findAll();
    }

    public Article show(final Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    public Article create(final ArticleDto dto) {
        final Article article = dto.toEntity();
        if (article.getId() != null) {
            return null;
        }
        return articleRepository.save(article);
    }

    public Article update(final Long id, final ArticleDto dto) {
        final Article article = dto.toEntity();
        log.info("id: {}, article: {}", id, article.toString());

        final Article target = articleRepository.findById(id).orElse(null);

        if (target == null || id != article.getId()) {
            log.info("잘못된 요청입니다. id: {}, article: {}", id, article.toString());
            return null;
        }

        target.patch(article);
        final Article updated = articleRepository.save(target);
        return updated;
    }

    public Article delete(final Long id) {
        final Article target = articleRepository.findById(id).orElse(null);
        if (target == null) {
            return null;
        }
        articleRepository.delete(target);

        return target;
    }

    @Transactional // 해당 service메소드를 트랜잭션으로 묶는다.
    public List<Article> createArticles(final List<ArticleDto> dtos) {
        // (1) dto묶음을 -> entity묶음으로 변환
        final List<Article> articleList = dtos.stream()
            .map(dto -> dto.toEntity())
            .collect(Collectors.toList());

        // (2) entity묶음을 -> db에 저장
        articleList.stream()
            .forEach(article -> articleRepository.save(article));

        // (3) 강제로 예외 만들어보기 for transaction
        // - repository를 통해서 예외 발생하기 -> findById에서 id를 음수로
        // - 조회메서드에선 stream처럼 .orElse(null) 혹은 .orElseThrow()처리가 가능하다. 
        articleRepository.findById(-1L).orElseThrow(
            () -> new IllegalArgumentException("결재 실패!")
        );

        // (4) 결과값반환
        // - 이미 예외이 고정이지만, 형식상 articlesList를 반환
        return articleList;
    }
}
