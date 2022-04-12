package com.example.firstproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FirstController {

    @GetMapping("/hi")
    public String niceToMeetYou(final Model model) {
        model.addAttribute("username", "hongpark");

        return "greetings"; // templates/greetings.mustache -> 브라우저로 전송
    }

    //3-1. 새로운 요청을 받을 수 있도록 라우트를 작성해준다.
    // - 요청을 받는 것은 controller니, 새로운 컨트롤러를 받아도 좋다.
    // - 뷰템플릿을 사용한다면, 파일명을 응답값으로 적어줘야하므로 public String은 고정이다.
    @GetMapping("/bye")
    //3-4.
//    public String seeYouNext() {
    public String seeYouNext(final Model model) {
        model.addAttribute("nickname", "홍길동");

        //3-2. 템플릿의 파일명만 적어주고, 만들어주러 가자.
        return "goodbye";
    }
}
