package com.ajulay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.List;

public class MonsterEmitter implements Serializable {
    public class Wave implements Serializable {
        private float time;
        private int routeIndex;
        private int monstersCount;

        public Wave(float time, int routeIndex, int monstersCount) {
            this.time = time;
            this.routeIndex = routeIndex;
            this.monstersCount = monstersCount;
        }
    }

    private Map map;
    private Monster[] monsters;
    private Wave[] waves;
    private float spawnTimer;
    private int currentWave;

    public Monster[] getMonsters() {
        return monsters;
    }

    public MonsterEmitter(Map map, int maxSize) {
        this.map = map;
        this.monsters = new Monster[maxSize];
        for (int i = 0; i < monsters.length; i++) {
            this.monsters[i] = new Monster(map, 0);
        }
        loadScenario("scenario1");
    }

    public void reload() {
        for (int i = 0; i < monsters.length; i++) {
            monsters[i].reload();
        }
    }

    private void loadScenario(String scenario) {
        BufferedReader br = null;
        List<String> lines = Utilities.readAllLinesFromFile(Gdx.files.internal(scenario + ".dat"));
        lines.remove(0);
        waves = new Wave[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String[] arr = lines.get(i).split("\\s");
            waves[i] = new Wave(Float.parseFloat(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
        }
    }

    public void createMonster(int routeIndex) {
        for (int i = 0; i < monsters.length; i++) {
            if (!monsters[i].isActive()) {
                monsters[i].activate(routeIndex);
                break;
            }
        }
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        for (int i = 0; i < monsters.length; i++) {
            if (monsters[i].isActive()) {
                monsters[i].render(batch);
                monsters[i].renderHUD(batch, font);
            }
        }
    }

    public void update(float dt) {
        if (currentWave < waves.length) {
            spawnTimer += dt;
            Wave w = waves[currentWave];
            if (spawnTimer >= w.time) {
                for (int j = 0; j < w.monstersCount; j++) {
                    createMonster(w.routeIndex);
                }
                currentWave++;
            }
        }

        for (int i = 0; i < monsters.length; i++) {
            if (monsters[i].isActive()) {
                monsters[i].update(dt);
            }
        }
    }
}
