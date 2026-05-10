package com.example.pixeldungeons.data;

import com.example.pixeldungeons.model.Hero;

import java.util.ArrayList;
import java.util.List;

public class HeroRepository {

    private static final List<Hero> heroes = new ArrayList<>();

    public static List<Hero> getHeroes() {
        return heroes;
    }

    public static void addHero(Hero hero) {
        heroes.add(hero);
    }
}
