package com.example.pixeldungeons.data;

import com.example.pixeldungeons.model.GameMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameMapRepository {

    private static final List<GameMap> maps = new ArrayList<>(Arrays.asList(
            new GameMap("Cripta nivel 1", true, ""),
            new GameMap("Bosque Oscuro", false, ""),
            new GameMap("Torre del Mago", false, ""),
            new GameMap("Caverna del Dragón", false, "")
    ));

    public static List<GameMap> getMaps() {
        return maps;
    }

    public static void addMap(GameMap map) {
        maps.add(map);
    }

    public static void setVisible(int position) {
        for (int i = 0; i < maps.size(); i++) {
            maps.get(i).setVisible(i == position);
        }
    }
}
