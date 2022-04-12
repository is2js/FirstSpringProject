package com.example.firstproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

//2-1. view의 동료임을 선언한다.
@Controller
public class FirstController {

    //2-3. 동료내부에서 해당 페이지로 @접속할 url도 선언해줘야한다.
    // 서버 재시작후 http://localhost:8080/hi
    @GetMapping("/hi")

    //2-5. view의 또다른 동료는 controller의 파라미터로 받아야한다.
//    public String niceToMeetYou() {
    // -> import class시 org.spring~을 찾아서 해주면 된다
    public String niceToMeetYou(final Model model) {
        //2-6. 이제 view로 보내줄 변수를 model에 등록해줄 수 있다.
        model.addAttribute("username", "hongpark");

        //return "";
        //2-2. controller 동료는 메소드를 통해 -> view 템플릿 페이지를 반환할 수 있게 해줘야한다.
        //    확장자만 빼고, 페이지명만 string으로 넘겨준다.
        return "greetings"; // templates/greetings.mustache를 알아서 찾아서 브라우저로 전송해준다.
        // -> 엥? http://localhost:8080/greetings.mustache로 들어가도 안보인다. -> 빠진게 있다.
    }
}
