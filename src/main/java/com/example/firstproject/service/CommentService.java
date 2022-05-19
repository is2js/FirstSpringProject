package com.example.firstproject.service;

import com.example.firstproject.dto.CommentDto;
import com.example.firstproject.entity.Article;
import com.example.firstproject.entity.Comment;
import com.example.firstproject.repository.ArticleRepository;
import com.example.firstproject.repository.CommentRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//21-2. 
@Service
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

    //21-22. 여러단계에서 db를 건들이고 있다면, 트랜잭셔널을 달아놔야 중간에 에러나도 롤백된다.
    @Transactional
    public CommentDto create(final Long articleId, final CommentDto commentDto) {
        //21-21. service 생성시 할 일
        // (1) [생성 요청시 넘어오는 상위도메인의 fk] -> 상위도메인 db조회후 entity응답 + 없으면 예외 발생
        // (2) 댓글 엔터티 생성
        // (3) 댓글 엔터티를 db로 저장
        // (4) dto로 변경하여 반환

        //21-23.
        // (1) [생성시 넘어오는 상위도메인의 fk] -> 상위도메인 db 조회 및 예외발생
        // -> 상위entity 객체가 있어야 -> 하위entity가 fk대신 가진 상위entity객체 필드를 채워 생성이 가능해진다...
        // --> 의미상으로는 하위entity가 어디에 속해있는지를 알 수 있다.
        // --> my) 하위도메인 생성시, 상위도메인객체 필드 때문이라도, 하위 service는 상위repository를 알고 있어야한다.
        final Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패! 대상 게시글이 없습니다."));
        // 있으면 받아오고, 없으면 아래로 내려가지 않고 예외가 발생한다.
        // 예외 메세지는  도메인 [생성] 실패로 현재 로직인 create(생성)이 실패했음을 메세지로 넣어준다.

        //21-24.
        // (2) 댓글 엔터티 생성
        // -> 조회된 상위entity를 인자로 받아가야 하위entity를 생성할 수 있다.
        // --> 강의에서는  [비싼것]EntityClass.정펙매( commentDto[싼것에 의존], 상위entity) 해서 만들었다.. 이러면 안됨..
        // --> 나는 commentDto.toEntity()로 만들 것이다.  싼것.to비싼것() (O) / 싼것Class.정팩매( 비싼것 의존 ) (O)
        // --> my) 생성이므로 id는 건네주지 않아도 된다. / entity생성이니 좌항을 미리 만들어놓는다. final Comment comment =
        // - 아직 toEntity() 생성은 있다하고, 좌항을 잡아줘서 -> 아래까지 구조를 잡아 줄 수 있게 한다.
        final Comment comment = commentDto.toEntity(article);

        //21-26
        // (3) 댓글 엔터티를 db로 저장
        final Comment created = commentRepository.save(comment);
        // (4) dto로 변경하여 반환 by dtoClass.정팩매( 비싼것 )
        return CommentDto.createCommentDto(created);
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
