package com.example.pixeldungeons.data;

import com.example.pixeldungeons.model.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemRepository {

    private static final List<Item> items = new ArrayList<>(Arrays.asList(
            new Item("Espada larga", "Arma", 1, false, "Daño: 1d8 cortante"),
            new Item("Poción de vida", "Consumible", 3, true, "Recupera 2d4+2 PV"),
            new Item("Escudo de roble", "Armadura", 1, false, "+2 a Defensa"),
            new Item("Llave oxidada", "Llave", 2, true, "Abre puertas oxidadas"),
            new Item("Pergamino arcano", "Hechizo", 5, true, "Lanza Bola de Fuego")
    ));

    public static List<Item> getItems() {
        return items;
    }

    public static void addItem(Item item) {
        items.add(item);
    }

    public static boolean useItem(int position) {
        Item item = items.get(position);
        if (item.isConsumable() && item.getQuantity() > 0) {
            item.decreaseQuantity();
            return true;
        }
        return false;
    }

    public static void removeOne(int position) {
        Item item = items.get(position);
        if (item.getQuantity() > 0) {
            item.decreaseQuantity();
        }
    }

}
