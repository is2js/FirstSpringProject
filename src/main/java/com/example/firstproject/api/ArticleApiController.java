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
        return articleService.index();
    }

    @GetMapping("/api/articles/{id}")
    public Article show(@PathVariable Long id) {
        return articleService.show(id);
    }

    //POST
    @PostMapping("/api/articles")
    public ResponseEntity<Article> create(@RequestBody ArticleDto dto) {
        Article created = articleService.create(dto);

        return (created != null) ?
            ResponseEntity.status(HttpStatus.OK).body(created) :
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    //PATCH
    @PatchMapping("/api/articles/{id}")
    public ResponseEntity<Article> update(@PathVariable Long id,
                                          @RequestBody ArticleDto dto) {
        Article updated = articleService.update(id, dto);

        return (updated != null) ?
            ResponseEntity.status(HttpStatus.OK).body(updated) :
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    //DELETE
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Article> delete(@PathVariable Long id) {
        Article deleted = articleService.delete(id);

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
