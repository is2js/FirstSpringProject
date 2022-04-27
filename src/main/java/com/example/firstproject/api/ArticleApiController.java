package com.example.firstproject.api;

import com.example.firstproject.dto.ArticleDto;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController // RestAPI 용 컨트롤러 데이터(주로 JSON) 반환
public class ArticleApiController {

    @Autowired // DI 외부에서 가져오는 것
    private ArticleRepository articleRepository;

    //GET
    //17-1. get(전체 조회) 부터 시작  -> 응답은 List< Entity >형태로 받환할 것이다.
    @GetMapping("/api/articles")
    public List<Article> index() {
        // -> reposiitory - findAll()을 이용해서 전체를 조회한다.
        // --> repository는 Controller속 field로 선언해놓고, spring boot로 부터 땡겨오는 것이다.
        return articleRepository.findAll();
    }

    //17-2. get(전체 개별 조회) route -> 응답은 단일 Entity?! + 조회시 없을 수도 있음 null가능
    // -> 개별조회는 id PathValiable 때문에 파라미터가 달라져 index로 그대로 메서드명 유지한다?!
    @GetMapping("/api/articles/{id}")
    public Article index(@PathVariable Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    //POST
    //17-3. post(생성) with [id없는 데이터] -> Dto with @RequestBody-> toEntity -> save
    @PostMapping("/api/articles/")
    public Article create(@RequestBody ArticleDto dto) {
        final Article article = dto.toEntity();
        return articleRepository.save(article);
    }

    //PATCH
    //17-4. 수정은 개별id 상태에서 수정이므로 path에서 현재id가 반영되어있다.
    // -> 수정은 id를 포함한 수정완료된 row 1줄의 full데이터가 온다
    // --> 수정은 @PathVariable + @RequestBody가  둘다 들어온다.
    @PatchMapping("/api/articles/{id}")
    public ResponseEntity<Article> update(@PathVariable Long id,
                                          @RequestBody ArticleDto dto) {
        //(1) 수정용 엔터티 생성
        final Article article = dto.toEntity();
        // - log를 2가지변수를 찍을땐, 중괄호{}로 남긴 포매터 + 콤마로 연결해주면 된다.
        log.info("id: {}, article: {}", id, article.toString());

        //(2) 대상 엔터티 조회
        final Article target = articleRepository.findById(id).orElse(null);

        //(3) 잘못된 요청 처리(update, delete는 (1)개별id 조회 -> ? -> (2) 처리 이므로)
        // - [대상id가 DB에 없거나] or [Path 속 id <-> 수정완료된 json속 id]가 다른 경우
        // -> 잘못된 경우를 잡아서 early return 400을 해준다. -> 그 이후로 올바른 요청이므로 update나 delete해주면 된다.
        // (1) 템플릿엔진에서는 [올바른경우만 처리+메세지로 잘못된요청처리X]하도록 하여 db에 있는 경우(target!=null)만 처리후 -> rttr에 flash msg 담아서 -> redirect로 알려줬지만
        // (2) restAPI에서는 [잘못된 경우 2가지를 먼저처리 early 400 return] -> 그뒤는 무조건 올바른 경우

        //1) db에 없는 경우(null) OR 요청URL 속 id와 데이터속id가 다른 경우
        if (target == null || id != article.getId()) {
            // 잘못된요청은 400 early return
            // 잘못된요청으로 빠질 경우, log를 찍어주자.
            log.info("잘못된 요청입니다. id: {}, article: {}", id, article.toString());
            // 17-5. 이제 400번 응답을 해주려면, entity가 아닌, ResponEntity로 감싸서 응답해줘야한다.
            // -> 제네릭자리에 응답 Entity가 들어간다. ResponEntity로 상태코드를 담아서 보낼 수 있따.
//            return ResponseEntity.status(400) // 숫자로도 지정가능하고
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400을 의미하는 BAD_REQUEST를 줘도된다.
                .body(null); // body의 경우, null로 아무것도 안 싣어보낼 수 있다.
            //제대로 된 요청의 경우, ResponseEntity<Article>로 내려가니 그걸로 return값을 맞춰주면 된다.
        }

        //(4) 업데이트 처리 및 정상 응답(200)

        //17-8. 존재하는 데이터 하면서 && 요청id와 데이터id가 일치하는 정상 수정요청이지만,
        //      [id + 수정하고 싶은 칼럼의 데이터만] 보내줘도 -> [ 나머지 칼럼들은 알아서 기존데이터를 채운 Entity]로 만들고 싶다
        //      -> 그것을 db_target_entity .patch ( 들어온dto_to_entity ) 의 비교를 통해 처리한다.
        //      -> 서로 같은 객체라 편하게 메세지 날려서 비교하면 된다.
        //      수정전인 targetEntity에다가 . patch붙여준다(  받아온 새로운 수정 데이터만 가진 entity )를
        //     my) target Entity <- 처리전 db에서 조회해온 entity(처리가 들어갈 entity)
        target.patch(article);
        //target에 새로운 데이터가 잘 반영되었다면, -> 수정완료된 통 데이터entity를 저장해서 업데이트하는게 아니라
        //처리완료된 target을 저장해야한다.
        // -> 요청에서 들어온 수정완료된 entity를 저장하면, 그게수정이다.
//        final Article updated = articleRepository.save(article);
        final Article updated = articleRepository.save(target);

        //17-6. 정상응답도 status를 담아서 보내준다.
//        return ResponseEntity.status(200)
        // body에는 updatede된 응답entity를 싣어버 보내주면 된다.
        return ResponseEntity.status(HttpStatus.OK).body(updated);

        //17-7.
        // (1) 없는 ID를 수정요청해서 400(BAD_REQUEST)뜨는지 확인한다
        // (2) Talend API에서  요청url속 id <-> body StringJson 쪽 id를 다르게 주면
        // -> bad_requuest인 400이 뜨는지확인한다.

    }

    //DELETE
    //17-9. delete를 만든다. 삭제 성공시 응답은 body에 데이터 없이줘도 ok만 주면 된다(ok  .body(null) or .build() );
    //      - 기본적으로 Entity를 응답하게 첨에 작성한 뒤, 나중에 ResponseEntity로 바꿔보자.
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Article> delete(@PathVariable Long id) {
        //17-10. update에 비해 toEntity의 과정이 없이, id만 넘어와 바로 조회->처리된다.
        //(1) 대상 조회
        final Article target = articleRepository.findById(id).orElse(null);
        //(2) 조회후 처리전, 잘못된요청 검증
        // - 여기선 update처럼, id <-> json내부 id 다른 것은 고려안한다.
        // - 없는 데이터 조회만, 처리하여 early 400 return
        if (target == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            //ResponseEntity<Object>가 return되어도 ResponseEntity<Entity>로 바꿔주면 된다
        }
        //(3) 대상 삭제(처리)
        articleRepository.delete(target);
        //(4) 데이터 반환 -> 삭제는 수정과 달리 데이터없이 ok만 응답하면 된다.
        //삭제 성공시 응답은 body에 데이터 없이줘도 ok만 주면 된다(ok  .body(null) or .build() );
//        return ResponseEntity.status(HttpStatus.OK).body(null);
        return ResponseEntity.status(HttpStatus.OK).build();

        // apitest 확인후 -> 실제db에서도 삭제되었는지 조회해보자.
    }
}
