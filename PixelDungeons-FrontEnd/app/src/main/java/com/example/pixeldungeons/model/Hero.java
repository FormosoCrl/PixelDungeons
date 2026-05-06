package com.example.pixeldungeons.model;

public class Hero {

    private String name;
    private String race;
    private String heroClass;
    private int hp;
    private int maxHp;
    private int str;
    private int dex;
    private int defence;
    private int mana;

    public Hero(String name, String race, String heroClass,
                int hp, int maxHp, int str, int dex, int defence, int mana) {
        this.name = name;
        this.race = race;
        this.heroClass = heroClass;
        this.hp = hp;
        this.maxHp = maxHp;
        this.str = str;
        this.dex = dex;
        this.defence = defence;
        this.mana = mana;
    }

    public String getName() { return name; }
    public String getRace() { return race; }
    public String getHeroClass() { return heroClass; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getStr() { return str; }
    public int getDex() { return dex; }
    public int getDefence() { return defence; }
    public int getMana() { return mana; }
}
