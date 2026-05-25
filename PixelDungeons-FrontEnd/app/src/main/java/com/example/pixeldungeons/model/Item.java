package com.example.pixeldungeons.model;

public class Item {

    private int id;
    private String name;
    private String type;
    private int quantity;
    private boolean consumable;
    private String description;
    private boolean equipped;
    private String bonusStat;
    private int bonusValue;

    public Item(String name, String type, int quantity, boolean consumable, String description, String bonusStat, int bonusValue) {
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.consumable = consumable;
        this.description = description;
        this.equipped = false;
        this.bonusStat = bonusStat;
        this.bonusValue = bonusValue;
    }

    public Item(String name, String type, int quantity, boolean consumable, String description) {
        this(name, type, quantity, consumable, description, "none", 0);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public int getQuantity() { return quantity; }
    public boolean isConsumable() { return consumable; }
    public String getDescription() { return description; }
    public boolean isEquipped() { return equipped; }
    public void setEquipped(boolean equipped) { this.equipped = equipped; }
    public String getBonusStat() { return bonusStat; }
    public int getBonusValue() { return bonusValue; }

    public void decreaseQuantity() { if (quantity > 0) quantity--; }
    public void increaseQuantity() { quantity++; }
}
