### 기본
![20220523103347](https://raw.githubusercontent.com/is2js/screenshots/main/20220523103347.png)
- 기존에는 json -> java객체(dto)로 자동 변환되었었다
    - 간접적으로 ObjectMapper를 사용했었다.
- aop를 적용해서 json과 java객체간의 변환을 확인해보자.


### 실습
1. 디버깅aop를 service -> 모든 api의 모든 controller class의 모든 메서드에 적용되도록 변경해보자.
    ```java
    @Pointcut("execution(* com.example.firstproject.api.*.*(..))")
    ```
2. api이므로 생성 테스트를 talendAPI로 테스트한다.
    - 상위도메인 5번글아래 댓글 1개를 생성해보기
        - id빼고 나머지 필드 body에 채워서 생성요청 보내기
        ```json
        {
        "nickname" : "No",
        "body" : "어벤져스",
        "article_id" : 5
        }
        ```
        ![20220523104105](https://raw.githubusercontent.com/is2js/screenshots/main/20220523104105.png)
        ![20220523104143](https://raw.githubusercontent.com/is2js/screenshots/main/20220523104143.png)
        ![20220523104151](https://raw.githubusercontent.com/is2js/screenshots/main/20220523104151.png)
    - json을 보낸 것이 dto로 만들어졌다.
    - response를 보면 dto -> json으로 반환되었다.
        ![20220523104236](https://raw.githubusercontent.com/is2js/screenshots/main/20220523104236.png)
    - return dto(java객체) + ResponseEntity를 했는데, json으로 반환해주는 것일까?
        ![20220523104341](https://raw.githubusercontent.com/is2js/screenshots/main/20220523104341.png)
    - 직접 변환하지 않았지만, 내부 ObjectMapper가 변환해준 것이다.

3. objectmapper학습을 위해 `objectmapper`패키지 및 `Burger`클래스를 만들고 tdd로 실습한다.
    ![20220523104511](https://raw.githubusercontent.com/is2js/screenshots/main/20220523104511.png)


#### ObjectMapper를 학습테스트(java 객체 -> json)
1. 일단시작 class를 만들고 시작한다.
2 Burger에 기본적인 필드를 만들어주자.
    1. name
    2. price
    3. 재료 list(객체 일급컬렉션 만들면 더 좋음)
        ![20220523104656](https://raw.githubusercontent.com/is2js/screenshots/main/20220523104656.png)

    4. 롬복으로 생성자 생성(필드 초기화가 생성자를 통해 되어서 빨간줄 사라짐)
        ![20220523104722](https://raw.githubusercontent.com/is2js/screenshots/main/20220523104722.png)
        
    5. tostring(롬복)
        ```java
        @AllArgsConstructor
        @ToString
        public class Burger {
            private final String name;
            private final int price;
            private final List<String> ingredients;
        }
        ```
3. 기본 시작 주체 class + 필드+ 생성자 + toString이 완성되면, test코드를 만들어서 실습한다.
    - **`학습테스트`로서 `이미 제공되는 메서드`의 expected를 예상할 수 없다면, 비워두고 에러를 내보자**
    ```java
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
        final String expected = "";

        // 4. 검증
        assertThat(json).isEqualTo(expected);
        System.out.println("json = " + json); // 검증과 별개로 마지막에 출력해주는 sense
    }
    ```
4. 학습테스트로서 이미 개발된 메서드의 예상값을 추측하기 위해 에러를 냈지만, `No serializer found`로서 직렬화가 안된다고 에러가 난다. 예상값 추측이전에, 필요코드가 빠졌다는 에러를 해결하자.
    ![20220523105706](https://raw.githubusercontent.com/is2js/screenshots/main/20220523105706.png)
    1. **getter가 없어서 직렬화를 할 수 가 없음.**
        - 직렬화 대상인 Burger객체에 getter를 롬복으로 추가한다.
    2. **expected를 예상불가하므로, type만 맞춰주고 `actual을 통해 반환값을 확인`한다.**
        ![20220523105929](https://raw.githubusercontent.com/is2js/screenshots/main/20220523105929.png)
        ```java
        expected: ""
        but was: "{"name":"맥도날드 슈비버거","price":5500,"ingredients":["통새우 패티","순쇠고기 패티","토마토","스파이시 어니언 소스"]}"
        org.opentest4j.AssertionFailedError: 
        ```
    
5. **학습테스트로서 `actual`값을 복붙하여 `expected`를 채워준다.**
    ![20220523110035](https://raw.githubusercontent.com/is2js/screenshots/main/20220523110035.png)
    ![20220523110118](https://raw.githubusercontent.com/is2js/screenshots/main/20220523110118.png)


#### json node를 활용해 json 이쁘게 출력하기
1. soutv 출력대신 `object.readTree( json )`을 통해, jsonNode를 반환받는다.

2. `jsonNode.toPrettyString()`를 soutv 출력한다.
    ```java
    // 4. 검증
    assertThat(json).isEqualTo(expected);

    final JsonNode jsonNode = objectMapper.readTree(json);
    System.out.println("jsonNode.toPrettyString() = " + jsonNode.toPrettyString());
    ```
    ![20220523110431](https://raw.githubusercontent.com/is2js/screenshots/main/20220523110431.png)


#### ObjectMapper를 학습테스트(json -> java 객체)
1. **json은 내가 직접 치기 힘드니까 `먼저 수행한 java -> json한 결과물`을 복붙해서 준비에 넣어준다.**

2. java객체 to json는 `objectMapper.writeValueAsString(burger);`로 인자가 java객체 1개지만, **json to java객체는 `json + 만들Class.class`을 같이 넣어줘야 반환값이 해당 `클래스객체가 반환`된다.**
    ```java
    objectMapper.readValue(json, Burger.class);
    ```
    ![20220523111535](https://raw.githubusercontent.com/is2js/screenshots/main/20220523111535.png)
    ![20220523111544](https://raw.githubusercontent.com/is2js/screenshots/main/20220523111544.png)

3. 예상결과는 해당 json값들이 field로 들어간 자바 객체다. 이것 역시 java객체 to json에서 썼던 java객체를 그대로 가져오면 된다.

4. **객체 비교는 eq&hash를 정의하지말고 toString()으로 바로 할 수 있다.(리스트도 순서만 맞춘다면 가능하다)**
    ```java
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
    ```
5. **또 또다른 에러가 난다.**
    ![20220523111919](https://raw.githubusercontent.com/is2js/screenshots/main/20220523111919.png)
    - 직렬화(java to json): `getter`로 값들을 가져와서 json을 만들어야하는 과정이 생략되어있다.
    - **역직렬화(json to java): `default 생성자`로 `빈 객체를 만들어서 박아 java객체 생성`이 생략되어있다.**
        - **default 생성자인 `@NoArgsConstructor`를 정의해주자.**
        - 빈 생성자는 field를 바로 초기화하지 않으니 final을 제거해야한다.
            ![20220523112123](https://raw.githubusercontent.com/is2js/screenshots/main/20220523112123.png)
            ![20220523112132](https://raw.githubusercontent.com/is2js/screenshots/main/20220523112132.png)


6. json출력을 이쁘게 하기 위해 readTree(json) -> jsonNode.toPrettyString()를 출력하도록 변경해보자.
    ```java
    /*검증*/
    assertThat(burger.toString()).isEqualTo(expected.toString());
    //System.out.println("json = " + json);
    final JsonNode jsonNode = objectMapper.readTree(json);
    System.out.println("jsonNode = " + jsonNode.toPrettyString());
    System.out.println("burger.toString() = " + burger.toString());
    ```


#### json직접 만들어보기 실습
1. objectNode를 만들어서 put으로 개별 key, value를 담는다.
2. js배열을 만들어 줄 경우, ArrayNode에 개별 add해서 배열value를 만들고
    - 완성된 arrayNode를 objectNode에 ~~put~~ set하여 key, value를 넣는다.
3. 만들어진 json을 readValue(json, .class)를 통해 객체로 만들어서
4. 예상 객체를 만든 뒤, 객체vs객체를 toString해서 비교한다.
    ```java
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
    ```
### 큰틀
1. objectMapper에서 .writeValueAsString( 객체 ) 로 json으로 변환한다
2. objectMapper에서 .realvalue( json, .class ) 로 객체로 변환한다
3. objectMapper에서 .createObjectNode() +  .createArrayNode로 json을 만든다. 비교하려면 realvalue로 객체를 만들어서 객체끼리 비교한다.
