package com.example.firstproject.ioc;

import java.util.Objects;
import org.springframework.stereotype.Component;

@Component // 해당 클래스를 spring bean(객체)로 만들고, 이것을 IoC 컨테이너에 등록함
public class IngredientFactory {
    public Ingredient get(final String menu) {
        if (Objects.equals(menu, "돈가스")) {
            return new Pork("한돈 등심");
        }
        if (Objects.equals(menu, "스테이크")) {
            return new Beef("한우 꽃등심");
        }
        // 확장가능성이 있는 것으로서 코드가 변하는 것은
        // -> domain(Chef)의 외부에 위치하여  DI되는 재료공장 밖이다.
        if (Objects.equals(menu, "크리스피 치킨")) {
            return new Chicken("국내산 10호 닭");
        }
        return null;
    }
}
