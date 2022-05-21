package com.example.firstproject.ioc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChefTest {

    @Autowired
    IngredientFactory ingredientFactory;
    
    @Autowired
    private Chef chef;


    @Test
    void 돈가스_요리하기() {
        //2. 준비
        //5. 재료공장 만들기
        //  5-1. 생성자 -> 껍데기 class만 일단 만든다.
//        final IngredientFactory ingredientFactory = new IngredientFactory();
        //  5-2. method에 인자로 재료주입하는 것과 달리,
        //       재료공장은 주체인 객체Chef에 생성자 주입한다.
        //final Chef chef = new Chef();
//        final Chef chef = new Chef(ingredientFactory);
        final String menu = "돈가스";

        //1.수행
        // - chef가 menu를 받아 요리를 해서 -> food를 응답하고 -> 그게 돈가스였으면 좋겠다.
        // - my) 생성자없이 객체 바로사용 -> 인자도 값x변수 -> 응답 또한 값이 아니라 변수로 사용해서 개별적으로 다 만들어준다.
        // - my) 응답변수의 값은 메서드내부에서 반환해줄 것이고, 예상은 응답값을 적고 -> 변수를 expected로 이름 짓는다.
        String food = chef.cook(menu);

        //3. 예상
        // - food의 값을 예상해서 값을 우항에, 변수명은 expected로 준다.
        final String expected = "한돈 등심으로 만든 돈가스"; // 메서드가 반환할 food 예상 값

        //4. 검증
        assertThat(food).isEqualTo(expected);
        System.out.println("food = " + food); // 검증에 추가확인용으로 sout를 적어줄 수 있다.
    }

    @Test
    void 스테이크_요리하기() {
        //추가 case는 method인자 + 예상값이 달라지며, 기존반환유지 + 예상값이 반환되도록 method를 작성한다.

        //2. 준비
//        final IngredientFactory ingredientFactory = new IngredientFactory();
//        final Chef chef = new Chef(ingredientFactory);
//        final String menu = "돈가스";
        final String menu = "스테이크";

        //1.수행
        String food = chef.cook(menu);

        //3. 예상
        //final String expected = "한돈 등심으로 만든 돈가스";
        final String expected = "한우 꽃등심으로 만든 스테이크";

        //4. 검증
        assertThat(food).isEqualTo(expected);
        System.out.println("food = " + food);
    }

    @Test
    void 크리스피_치킨_요리하기() {
        //2. 준비
//        final IngredientFactory ingredientFactory = new IngredientFactory(); // 재료공장 생성
//        final Chef chef = new Chef(ingredientFactory); // Chef에 재료공장 DI
        final String menu = "크리스피 치킨"; // 변화1: 인자

        //1.수행
        String food = chef.cook(menu);

        //3. 예상
        final String expected = "국내산 10호 닭으로 만든 크리스피 치킨"; // 변화2: 예상값

        //4. 검증
        assertThat(food).isEqualTo(expected);
        System.out.println("food = " + food);
    }
}
