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
        // create의 잘못된 요청 -> created에 null로 반환
        // create시 들어오는 dto -> toEntity했는데 그 때 Entity안에 id가 있는 경우(!=null)
        if (article.getId() != null) {
            return null;
        }
        return articleRepository.save(article);
    }

    public Article update(final Long id, final ArticleDto dto) {
        //(1) 수정용 엔터티 생성
        final Article article = dto.toEntity();
        log.info("id: {}, article: {}", id, article.toString());

        //(2) 대상 엔터티 조회
        final Article target = articleRepository.findById(id).orElse(null);

        //(3) 잘못된 요청 처리(update, delete는 (1)개별id 조회 -> ? -> (2) 처리 이므로)
        if (target == null || id != article.getId()) {
            log.info("잘못된 요청입니다. id: {}, article: {}", id, article.toString());
            //service에서는 `잘못된 요청은 null반환`만 하면, controller가 null비교로 good/bad를 가른다.
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            return null;
        }

        //(4) 업데이트 처리 및 정상 응답(200)
        target.patch(article);
        final Article updated = articleRepository.save(target);
        return updated;
    }

    public Article delete(final Long id) {
        //(1) 대상 조회
        final Article target = articleRepository.findById(id).orElse(null);
        //(2) 조회후 처리전, 잘못된요청 검증
        if (target == null) {
            //서비스는 잘못된요청의 경우 일단 null
            return null;
        }
        //(3) 대상 삭제(처리)
        // repository.delete(target)는 사실 응답entity가 없다 -> 삭제된 데이터를 응답해줘야한다면, 조회entity target을 응답해주자.
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
