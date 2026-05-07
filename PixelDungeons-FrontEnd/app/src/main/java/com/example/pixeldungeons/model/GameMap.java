package com.example.pixeldungeons.model;

public class GameMap {

    private String name;
    private boolean visible;

    public GameMap(String name, boolean visible) {
        this.name = name;
        this.visible = visible;
    }

    public String getName() { return name; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
}
