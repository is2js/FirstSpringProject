package com.example.firstproject.ioc;

import org.springframework.stereotype.Component;

@Component
public class Chef {

    // 세프는 식재료 공장을 알고 있음.
    private final IngredientFactory ingredientFactory;

    // 세프가 식재료 공장과 협업하기 위한 DI
    public Chef(final IngredientFactory ingredientFactory) {
        this.ingredientFactory = ingredientFactory;
    }

    public String cook(final String menu) {
        // 1. 재료 준비
        // 재료는 주체가 주입받은 재료공장으로부터 받되,
        // 종류가 여러개이므로 -> method가 받아온 인자(menu)에 따라 다르게 조달받는다.
        final Ingredient ingredient = ingredientFactory.get(menu);

        // 2. food 반환
        return ingredient.getName() + "으로 만든 " + menu;
    }
}


