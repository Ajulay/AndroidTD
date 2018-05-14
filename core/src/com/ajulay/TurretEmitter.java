package com.ajulay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.Serializable;
import java.util.List;

public class TurretEmitter implements Serializable {
    public class TurretTemplate {
        private int turretId;
        private int level;
        private int imageIndexX;
        private int imageIndexY;
        private int cost;
        private int damage;
        private int radius;
        private float fireRate;

        public int getTurretId() {
            return turretId;
        }

        public int getLevel() {
            return level;
        }

        public int getImageIndexX() {
            return imageIndexX;
        }

        public int getImageIndexY() {
            return imageIndexY;
        }

        public int getCost() {
            return cost;
        }

        public int getDamage() {
            return damage;
        }

        public int getRadius() {
            return radius;
        }

        public float getFireRate() {
            return fireRate;
        }

        // turret_id level image_x image_y cost fire_rate damage radius
        public TurretTemplate(String line) {
            String[] tokens = line.split("\\s");
            turretId = Integer.parseInt(tokens[0]);
            level = Integer.parseInt(tokens[1]);
            imageIndexX = Integer.parseInt(tokens[2]);
            imageIndexY = Integer.parseInt(tokens[3]);
            cost = Integer.parseInt(tokens[4]);
            fireRate = Float.parseFloat(tokens[5]);
            damage = Integer.parseInt(tokens[6]);
            radius = Integer.parseInt(tokens[7]);
        }
    }

    private transient TurretTemplate[][] templates;
    private Map map;
    private Turret[] turrets;

    public TurretEmitter(GameScreen gameScreen, Map map) {
        this.loadTurretData();
        this.map = map;
        this.turrets = new Turret[20];
        TextureRegion[][] regions = new TextureRegion(Assets.getInstance().getAtlas().findRegion("turrets")).split(80, 80);
        for (int i = 0; i < turrets.length; i++) {
            turrets[i] = new Turret(regions, gameScreen, map, 0, 0);
        }
    }

    public void reload(GameScreen gameScreen) {
        loadTurretData();
        TextureRegion[][] regions = new TextureRegion(Assets.getInstance().getAtlas().findRegion("turrets")).split(80, 80);
        for (int i = 0; i < turrets.length; i++) {
            turrets[i].reload(regions, gameScreen);
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < turrets.length; i++) {
            if (turrets[i].isActive()) {
                turrets[i].render(batch);
            }
        }
    }

    public void update(float dt) {
        for (int i = 0; i < turrets.length; i++) {
            if (turrets[i].isActive()) {
                turrets[i].update(dt);
            }
        }
    }

    public int getTurretCost(int turretId, int level) {
        return templates[turretId][level].cost;
    }

    public void setTurret(int index, int cellX, int cellY) {
        if (map.isCellEmpty(cellX, cellY)) {
            for (int i = 0; i < turrets.length; i++) {
                if (turrets[i].isActive() && turrets[i].getCellX() == cellX && turrets[i].getCellY() == cellY) {
                    return;
                }
            }
            for (int i = 0; i < turrets.length; i++) {
                if (!turrets[i].isActive()) {
                    turrets[i].activate(templates[index][1], cellX, cellY);
                    break;
                }
            }
        }
    }

    public void destroyTurret(int cellX, int cellY) {
        for (int i = 0; i < turrets.length; i++) {
            if (turrets[i].isActive() && turrets[i].getCellX() == cellX && turrets[i].getCellY() == cellY) {
                turrets[i].deactivate();
            }
        }
    }

    public boolean upgradeTurret(PlayerInfo player, int cellX, int cellY) {
        for (int i = 0; i < turrets.length; i++) {
            if (turrets[i].isActive() && turrets[i].getCellX() == cellX && turrets[i].getCellY() == cellY) {
                TurretTemplate nextLevelTemplate = templates[turrets[i].getTurretId()][turrets[i].getLevel() + 1];
                if (nextLevelTemplate != null) {
                    if(player.isMoneyEnough(nextLevelTemplate.cost))
                    {
                        player.decreaseMoney(nextLevelTemplate.cost);
                        turrets[i].activate(nextLevelTemplate, cellX, cellY);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void loadTurretData() {
        List<String> lines = Utilities.readAllLinesFromFile(Gdx.files.internal("turrets.dat"));
        templates = new TurretTemplate[5][5];
        // turret_id level image_x image_y cost fire_rate damage radius
        for (int i = 1; i < lines.size(); i++) {
            int turretId = Integer.parseInt(lines.get(i).split("\\s")[0]);
            int turretLevel = Integer.parseInt(lines.get(i).split("\\s")[1]);
            templates[turretId][turretLevel] = new TurretTemplate(lines.get(i));
        }
    }
}

