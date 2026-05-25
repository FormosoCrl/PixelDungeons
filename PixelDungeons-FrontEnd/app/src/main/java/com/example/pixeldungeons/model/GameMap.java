package com.example.pixeldungeons.model;

public class GameMap {

    private int id;
    private String name;
    private boolean visible;
    private String image;

    public GameMap(String name, boolean visible, String image) {
        this.name = name;
        this.visible = visible;
        this.image = image != null ? image : "";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image != null ? image : ""; }
}
