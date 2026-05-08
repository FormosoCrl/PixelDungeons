package com.example.pixeldungeons.model;

public class Item {

    private String name;
    private String type;
    private int quantity;

    public Item(String name, String type, int quantity) {
        this.name = name;
        this.type = type;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public int getQuantity() { return quantity; }
}
