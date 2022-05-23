package com.example.firstproject.service;

import com.example.firstproject.dto.CommentDto;
import com.example.firstproject.entity.Article;
import com.example.firstproject.entity.Comment;
import com.example.firstproject.repository.ArticleRepository;
import com.example.firstproject.repository.CommentRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//21-2. 
@Service
@Slf4j
public class CommentService {
    //21-3.
    @Autowired
    private CommentRepository commentRepository;

    //21-4. Jpa Entity에서는 하위entity(다, comment)에서는 자동join에 의해 상위entity(일, article)를 쉽게 얻어오므로
    //     바로 얻어낸 상위entity로 데이터를 갖다쓰기 위해 repository도 주입받는다.
    @Autowired
    private ArticleRepository articleRepository;


    public List<CommentDto> comments(final Long articleId) {
        //21-12. service가 할일을 적어보면
        // (1) 조회: 댓글 목록
        // (2) 변환: db에서 받아온 entity가 아닌 dto로 반환
        // (3) 반환

        //21-13. 댓글들을 db에서 가져오는 것은 일꾼repository에게 시킨다. comment~
        // (1) 조회: 댓글 목록
        final List<Comment> comments = commentRepository.findByArticleId(articleId);

        // (2) 변환: db에서 받아온 entity가 아닌 dto로 반환
        //21-14. entito to Dto는 어떻게 변환할까?
        // -> stream을 안쓴다면, 빈 commentDto 리스트에 돌면서 개별 변환후 add해준다.
//        final ArrayList<CommentDto> dtos = new ArrayList<>();
//        for (final Comment comment : comments) {
//            // 싼것(dto).toEntity()는 가능하나 비싼것(entity, domain).toDto()는 안되므로
//            // -> **비싼것.toDto()를 배제한다면(무조건), `싼것.정펙매(비싼것)`으로 가져가자**
//            // -> my) 싼것으로 변환할 땐, 비싼것. 시작이 아니라 [비싼것을 (인자)로 넣어서 비싼것을 의존하는 싼Class.정팩매]로 만들자.
//            final CommentDto dto = CommentDto.createCommentDto(comment);
//            dtos.add(dto);
//        }
//        return dtos;
        //21-16. list를 다른 list로 변환 -> 빈list +for문 -> stream으로 해결
        return comments.stream()
            .map(CommentDto::createCommentDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto create(final Long articleId, final CommentDto commentDto) {
//        // AOP 도입 전 lombok의 @Slf4j 로그찍기
//        // - lombok의 log.info는 콤마로 formatting 출력 가능
//        log.info("입력값 => {}", articleId);
//        log.info("입력값 => {}", commentDto);

        final Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패! 대상 게시글이 없습니다."));

        final Comment comment = commentDto.toEntity(article);

        final Comment created = commentRepository.save(comment);

        final CommentDto createDto = CommentDto.createCommentDto(created);
        // AOP 도입 전 lombok의 @Slf4j 로그찍기
//        log.info("반환값 => {}", createDto);
        return createDto;
    }

    @Transactional
    public CommentDto update(final Long id, final CommentDto dto) {
        //21-31. 할 일
        // (1) 댓글 조회(존재 검증) 및 예외 발생
        // (2) 댓글 수정
        // (3) DB로 갱신
        // (4) entity to dto로 변환 및 반환

        //21-32
        // (1) 댓글 조회(존재 검증) 및 예외 발생 -> 조회후 쓰는 것은 target으로 네이밍
        //     cf) 생생시 조회한 것은 상위entity이니, target은 아님. 그래서 네이밍 그대로 article(상위객체)
        final Comment target = commentRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("댓글 수정 실패! 대상 댓글이 없습니다."));
        // (2) 댓글 수정 -> 수정내용이 완료된 entity를 만들어야한다. by .patch()
        //    여기서는 비싼것.메서드( 싼 것)의 로직처럼 보이지만, setter의 역할일 뿐이다.
        //    싼것이든 뭐든.. 들어오는 데이터?? -> 나는 싼것Class.정팩매_(비싼것) 할 것 같긴하다.
        //    - 강의에선 target(하위entity)를 그냥 dto내용을 setter해서 수정완료된 row entity를 만든다.
//        dto.toEntity(target); // 도전 결과 setter는 같은 객체연산 아닌 이상, setter형태는 무조건 발생하므로
        // -> 싼것.이(비싼것 의존) 형태를 꼭 안만들어도 된다. (똑같으며, 더 불편하다)
        target.patch(dto); // setter는 비싼것 상관없이 [update될객체].setter([데이터보유객체])형태로 넣어준다.

        // (3) DB로 갱신 -> update도 수정완료된 entity를 save로 한다?!
        final Comment updated = commentRepository.save(target);
        // (4) entity to dto로 변환 및 반환
        return CommentDto.createCommentDto(target);
    }

    //21-36. 데이터를 건들이면 transactional
    @Transactional
    public CommentDto delete(final Long id) {
        //21-37. 할일
        // (1) 댓글 조회 및 예외 발생(존재검증 및 entity반환)
        // (2) 댓글삭제
        // (3) dto변환후 반환

        //21-38.
        // (1) 댓글 조회 및 예외 발생(존재검증 및 entity반환)
        final Comment target = commentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("댓글 삭제 실패! 대상이 없습니다."));

        // (2) 댓글삭제
        commentRepository.delete(target);

        // (3) entity -> dto변환후 반환 ( 싼것Class.정팩매( 비싼것 ))
        return CommentDto.createCommentDto(target);
    }
}
