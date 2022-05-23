package com.example.firstproject.api;

import com.example.firstproject.annotation.RunningTime;
import com.example.firstproject.dto.CommentDto;
import com.example.firstproject.service.CommentService;
import java.util.List;
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

//21-1.
@RestController
public class CommentApiController {
    //21-2.
    @Autowired
    private CommentService commentService;

    //21-5. controller에서 main로직을 먼저 작성한다.
    //      (1) 댓글 목록 조회(전체->개별)
    //      (2) 댓글 생성
    //      (3) 댓글 수정
    //      (4) 댓글 삭제

    //21-6. 요청 uri부터 talendAPI에서 정해놓고, controller에서 생성한다.
    // - 하위도메인은 항상 상위도메인의 id를 <hierarchy용>PathVariable로 물고 있는다 cf) RequestParam = QueryString
    @GetMapping("api/articles/{articleId}/comments")
    //21-7. controller는 list Dto를 반환하도록 해주고, 응답으로서 responseEntity에 넣어서 보낸다.
    // **uri상의 {id} pathvariable과 메서드인자에서 쓰는 Long articleId가 서로 다른 경우, @PathVariable(value = "id")처럼 [uri상의 변수값]을 value=""에 넣어주면, 원하는 파라미터 이름 articelId으로 사용가능해진다.**
    public ResponseEntity<List<CommentDto>> comments(@PathVariable Long articleId) {
        //21-6-2. controller내부 역할을 생각해보면 아래 2가지와 같다.
        // (1) 서비스에게 위임해서 list를 받아온 뒤
        // (2) 결과로서 응답

        //21-8.
        // (1) 서비스에게 위임해서 list를 받아온다.
        // -> 반환은 일단 dtos로 받아오도록 할 예정이다(List<CommentDto> = )
        final List<CommentDto> dtos = commentService.comments(articleId);

        //21-9.
        // (2) 결과로서 응답
        // -> articleController에서는 매번  service응답값 != null ? good : bad로 나눠서 응답해줬지만
        // -> controller는 **무조건 good으로 응답하도록 짠다.** -> service에서 받는 값들을 body에 받아 보낸다.
        // -> ResponseEntity로 generic없이 응답해준다.
        // --> 빨간줄이 뜨는 것은 아직 CommentDto가 완성안됬다는 의미이다. -> 만들어주기만 하면 사라진다.
        //   my) ResponseEntity<>에 제네릭을 안써줘도 응답형만 완성되면 사라진다.
        return ResponseEntity.status(HttpStatus.OK)
            .body(dtos);
    }

    //(2) 댓글 생성
    @PostMapping("api/articles/{articleId}/comments")
    public ResponseEntity<CommentDto> create(@PathVariable Long articleId,
                                             @RequestBody CommentDto dto) {
        // 서비스에게 생성 로직 위임
        final CommentDto createdDto = commentService.create(articleId, dto);

        // 결과 응답
        return ResponseEntity.status(HttpStatus.OK)
            .body(createdDto);
    }

    @PatchMapping("api/comments/{id}")
    public ResponseEntity<CommentDto> update(@PathVariable Long id,
                                             @RequestBody CommentDto dto) {
        final CommentDto updatedDto = commentService.update(id, dto);

        return ResponseEntity.status(HttpStatus.OK)
            .body(updatedDto);
    }

    //21-33. 삭제는 path속 자신id외에 json이 없으므로 @RequestBody 및 dto 삭제
    @DeleteMapping("api/comments/{id}")
    //28-16. 메서드는 다 작동하니 @RunningTime을 붙여 시간 측정 aop를 사용한다.
    @RunningTime
    public ResponseEntity<CommentDto> delete(@PathVariable Long id) {
        final CommentDto updatedDto = commentService.delete(id);

        return ResponseEntity.status(HttpStatus.OK)
            .body(updatedDto);
    }
}
