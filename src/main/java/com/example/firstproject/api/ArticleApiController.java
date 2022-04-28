package com.example.firstproject.api;

import com.example.firstproject.dto.ArticleDto;
import com.example.firstproject.entity.Article;
import com.example.firstproject.service.ArticleService;
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

    @Autowired // DI, @Service로 미리 만들어진 객체를 가져와 주입
    private ArticleService articleService;

    //GET
    @GetMapping("/api/articles")
    public List<Article> index() {
//        return articleRepository.findAll();
        return articleService.index();
    }

    @GetMapping("/api/articles/{id}")
    public Article show(@PathVariable Long id) {
//        return articleRepository.findById(id).orElse(null);
        return articleService.show(id);
    }

    //POST
    @PostMapping("/api/articles")
    public ResponseEntity<Article> create(@RequestBody ArticleDto dto) {
        Article created = articleService.create(dto);
        //18-3?. service가 내부 업무들을 트랜잭션단위로 순서총괄한 뒤 응답하는 값은
        // (1) 값이 단일 entity -> null가능성이 있다.
        // (2) 값이 복수 list -> size = 0일 가능성이 있다.
        //-> 거기에 따라 다르게 응답해준다. 생성처리후 생성된 데이터가 왔다? good : bad
        //return (create != null) ? good :bad;
        return (created != null) ?
            ResponseEntity.status(HttpStatus.OK).body(created) :
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    //PATCH
    @PatchMapping("/api/articles/{id}")
    public ResponseEntity<Article> update(@PathVariable Long id,
                                          @RequestBody ArticleDto dto) {

        // 웨이터controller는 는 service(쉐프)에게 파라미터로 받은 것들을(`id`, `dto`)를 그대로 건네준다.
        // -> 주방장이 updated된 entity를 받아와준다고 믿는다.
        Article updated = articleService.update(id, dto);
        // -> 조회(read) or 받아온 데이터(update/ )를 바탕으로 good/bad 응답을 판단해서 보내준다...
        // controller에서 그정도 판단은 한다.. 널이나 아니냐
        // -> 원래는 예외처리로 해야지만, 지금은 null로 판단한다.
        // --> null이 아니면 좋은 요청으로 return with data / null이면 나쁜요청을 ...
        return (updated != null) ?
            ResponseEntity.status(HttpStatus.OK).body(updated) :
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    //DELETE
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Article> delete(@PathVariable Long id) {
//        //(1) 대상 조회
//        final Article target = articleRepository.findById(id).orElse(null);
//        //(2) 조회후 처리전, 잘못된요청 검증
//        if (target == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//        }
//        //(3) 대상 삭제(처리)
//        articleRepository.delete(target);
        //return ResponseEntity.status(HttpStatus.OK).build();

        Article deleted = articleService.delete(id);
        // 웨이터는 일단, 쉐프가 준 자료를 null기준으로 good/bad 응답을 갈라서 응답해준다.
        // -> 삭제성공시는, 삭제된 데이터가 아니라 200번대의 NO_CONTENT(204)를 응답해주면 된다.
        return (deleted != null) ?
            ResponseEntity.status(HttpStatus.NO_CONTENT).build() :
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    //18-5. transaction test (일련의 과정 -> post로 수행)
    @PostMapping("api/transacton-test")
    //18-5-1. post를 여러개할 것이니 -> 응답은 ResponseEntity + List<Entity>로 Entity list로 응답해주자.
    //18-5-2. 여러개의 StringJsonList가 들어오는 것을 @RequestBody로 받아야한다.
    // -> jsonList는 **List<Dto> dtos**로 받으면 된다.!!
    public ResponseEntity<List<Article>> transactionTest(@RequestBody List<ArticleDto> dtos) {
        //18-5-3. 웨이터는 요청을 뭘 받았고, 응답으로는 무러 던져줄 것인지만 적어주면된다.
        // -> 자세한 순서+내용은 서비스(쉐프)에게 일을 시킨다.
        // -> 여러개 생성요청 -> dtos -> service의 응답은 createdList(List<Entity>)로 미리 작성한다.
        List<Article> createdList = articleService.createArticles(dtos);
        //18-6. 응답entity든 응답listENtity든, controller는 null인지 아닌지에 따라 good:bad로 응답한다.
        // - 여러개라서 사이즈 0으로 검사할 것 같지만. 서비스에서 null를 응답하나보다
        return (createdList != null) ?
            ResponseEntity.status(HttpStatus.OK).body(createdList) :
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
