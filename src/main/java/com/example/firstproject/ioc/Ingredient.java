package com.example.firstproject.ioc;

public abstract class Ingredient {

    protected final String name;

    public Ingredient(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
