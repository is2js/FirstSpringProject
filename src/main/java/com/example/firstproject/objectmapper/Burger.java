package com.example.firstproject.objectmapper;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Burger {
    private String name;
    private int price;
    private List<String> ingredients;

    @Override
    public String toString() {
        return "Burger{" +
            "name='" + name + '\'' +
            ", price=" + price +
            ", ingredients=" + ingredients +
            '}';
    }
}
