package com.example.pixeldungeons.data;

import com.example.pixeldungeons.model.Item;

import java.util.ArrayList;
import java.util.List;

public class PlayerInventoryRepository {

    private static final List<Item> inventory = new ArrayList<>();

    public static List<Item> getInventory() {
        return inventory;
    }

    public static void giveItem(Item catalogItem) {
        for (Item i : inventory) {
            if (i.getName().equals(catalogItem.getName())) {
                i.increaseQuantity();
                return;
            }
        }
        inventory.add(new Item(
                catalogItem.getName(),
                catalogItem.getType(),
                1,
                catalogItem.isConsumable(),
                catalogItem.getDescription()
        ));
    }

    public static void equipItem(int position) {
        Item newItem = inventory.get(position);
        String type = newItem.getType();
        if (type.equals("Arma") || type.equals("Armadura")) {
            for (Item i : inventory) {
                if (i != newItem && i.isEquipped() && i.getType().equals(type)) {
                    i.setEquipped(false);
                }
            }
        }
        newItem.setEquipped(true);
    }

    public static void useItem(int position) {
        Item item = inventory.get(position);
        if (item.isConsumable() && item.getQuantity() > 0) {
            item.decreaseQuantity();
        }
    }

    public static void removeOne(int position) {
        Item item = inventory.get(position);
        if (item.getQuantity() > 0) {
            item.decreaseQuantity();
            if (item.getQuantity() == 0) {
                item.setEquipped(false);
            }
        }
    }

    public static int getEquippedBonus(String stat) {
        int bonus = 0;
        for (Item item : inventory) {
            if (item.isEquipped() && stat.equals(item.getBonusStat())) {
                bonus += item.getBonusValue();
            }
        }
        return bonus;
    }
}
