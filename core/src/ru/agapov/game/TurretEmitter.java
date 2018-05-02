package ru.agapov.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TurretEmitter {


    public void upgTurret(Turret turret, int index) {
     turret.activate(templates[index], turret.getCellX(), turret.getCellY(), index);
        if (index==5)
        turret.setMaxUpg(true);

    }

    public class TurretTemplate {
        private int imageIndex;
        private int cost;
        private int damage;
        private int radius;
        private float fireRate;


        public int getImageIndex() {
            return imageIndex;
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

        public TurretTemplate(String line) {
            String[] tokens = line.split("\\s");
            imageIndex = Integer.parseInt(tokens[0]);
            cost = Integer.parseInt(tokens[1]);
            fireRate = Float.parseFloat(tokens[2]);
            damage = Integer.parseInt(tokens[3]);
            radius = Integer.parseInt(tokens[4]);

        }


    }

    private GameScreen gameScreen;
    private TextureAtlas atlas;
    private Map map;
    private Turret[] turrets;
    private TurretTemplate[] templates;

    public TurretEmitter(TextureAtlas atlas, GameScreen gameScreen, Map map) {
        this.loadTurretData();
        this.gameScreen = gameScreen;
        this.map = map;
        this.atlas = atlas;
        this.turrets = new Turret[20];
        TextureRegion[] regions = new TextureRegion(atlas.findRegion("turrets")).split(80, 80)[0];
        for (int i = 0; i < turrets.length; i++) {
            turrets[i] = new Turret(regions, gameScreen, map, 0, 0);
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

    public int getTurretCost(int index) throws IndexOutOfBoundsException {
        if (index>5)  //costyl
        {
            throw new IndexOutOfBoundsException();
        }
        return templates[index].cost;
    }


    public Turret getTurret(int cellX, int cellY) {
        if (map.isCellEmpty(cellX, cellY)){
            for (int i = 0; i < turrets.length; i++) {
            if (turrets[i].isActive() && turrets[i].getCellX() == cellX && turrets[i].getCellY() == cellY) {
            return turrets[i];
        }}}
    return null; }

    public void setTurret(int index, int cellX, int cellY) {
        if (map.isCellEmpty(cellX, cellY)) {
            for (int i = 0; i < turrets.length; i++) {
                if (turrets[i].isActive() && turrets[i].getCellX() == cellX && turrets[i].getCellY() == cellY) {
                    return;
                }
            }
            for (int i = 0; i < turrets.length; i++) {
                if (!turrets[i].isActive()) {

                    turrets[i].activate(templates[index], cellX, cellY, index);
                    break;}
            }
        }
    }


    public void loadTurretData() {
        BufferedReader br = null;
        List<String> lines = new ArrayList<String>();
        try {
            br = Gdx.files.internal("turrets.dat").reader(8192);
            String str;
            while ((str = br.readLine()) != null) {
                lines.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        templates = new TurretTemplate[lines.size() - 1];
        for (int i = 1; i < lines.size(); i++) {
            templates[i - 1] = new TurretTemplate(lines.get(i));
        }
       // System.out.println(templates.length); right
    }
}

