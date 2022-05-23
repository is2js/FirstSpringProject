package com.example.firstproject.objectmapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class BurgerTest {

    @Test
    void 버거_생성() {
        final String name = "찐찌버거";
        final int price = 1000;
        final List<String> ingredients = Arrays.asList("패티", "양배추");

        assertDoesNotThrow(() -> new Burger(name, price, ingredients));
    }

    @Test
    void 자바_객체를_JSON으로_변환() throws JsonProcessingException {
        // 준비
        // 2. objectMapper 생성(스프링 제공)
        final ObjectMapper objectMapper = new ObjectMapper();
        final List<String> ingredients = Arrays.asList("통새우 패티", "순쇠고기 패티", "토마토", "스파이시 어니언 소스");
        final Burger burger = new Burger("맥도날드 슈비버거", 5500, ingredients);

        // 수행
        // 1. obejctMapper에 Burger를 넣어주면 -> 필드들을 String json 만들어줄 것이다.
        final String json = objectMapper.writeValueAsString(burger);

        // 3. 결과값 예상
        final String expected = "{\"name\":\"맥도날드 슈비버거\",\"price\":5500,\"ingredients\":[\"통새우 패티\",\"순쇠고기 패티\",\"토마토\",\"스파이시 어니언 소스\"]}";

        // 4. 검증
        assertThat(json).isEqualTo(expected);

        final JsonNode jsonNode = objectMapper.readTree(json);
        System.out.println("jsonNode.toPrettyString() = " + jsonNode.toPrettyString());
    }

    @Test
    void JSON을_자바_객체로_변환() throws JsonProcessingException {
        /*준비*/
        final ObjectMapper objectMapper = new ObjectMapper();
        final String json = "{\"name\":\"맥도날드 슈비버거\",\"price\":5500,\"ingredients\":[\"통새우 패티\",\"순쇠고기 패티\",\"토마토\",\"스파이시 어니언 소스\"]}";

        /*수행*/
        final Burger burger = objectMapper.readValue(json, Burger.class);

        /*예상*/
        final List<String> ingredients = Arrays.asList("통새우 패티", "순쇠고기 패티", "토마토", "스파이시 어니언 소스");
        final Burger expected = new Burger("맥도날드 슈비버거", 5500, ingredients);

        /*검증*/
        assertThat(burger.toString()).isEqualTo(expected.toString());
        //System.out.println("json = " + json);
        final JsonNode jsonNode = objectMapper.readTree(json);
        System.out.println("jsonNode = " + jsonNode.toPrettyString());
        System.out.println("burger.toString() = " + burger.toString());
    }

    @Test
    void JSON_직접_생성() throws JsonProcessingException {
    /*
        {
            "name":"맥도날드 슈비버거"
            "price": 5000,
            "ingredients" : [ "통새우 패티", "순쇠고기 패티", "토마토", "스파이시 어니언 소스" ]
        }
    */
        //1. objectNode를 먼저 만든다.
        final ObjectMapper objectMapper = new ObjectMapper();
        final ObjectNode objectNode = objectMapper.createObjectNode();
        //2. put으로 key, value를 넣어준다.
        objectNode.put("name", "맥도날드 슈비버거");
        objectNode.put("price", 5500);

        // 3. list -> js 배열은 특이하게 ArrayNode를 만들어서 add로 각 요소를 넣어줘야한다.
        final ArrayNode arrayNode = objectMapper.createArrayNode();
        arrayNode.add("통새우 패티");
        arrayNode.add("순쇠고기 패티");
        arrayNode.add("토마토");
        arrayNode.add("스파이시 어니언 소스");

        // 4. 전체 objectNode에 arrayNode를 value에 put ->set으로 추가해준다.
        //objectNode.put("ingredients", arrayNode);
        objectNode.set("ingredients", arrayNode);

        // 5. objectNode로 toString을 하면 String json이 된다.
        final String json = objectNode.toString();

        // 6. json -> burger 객체로 변환하여, 원래 burger와 비교하자.
        final Burger burger = objectMapper.readValue(json, Burger.class);

        // 예상
        final List<String> ingredients = Arrays.asList("통새우 패티", "순쇠고기 패티", "토마토", "스파이시 어니언 소스");
        final Burger expected = new Burger("맥도날드 슈비버거", 5500, ingredients);

        // 검증
        assertThat(burger.toString()).isEqualTo(expected.toString());
    }
}
