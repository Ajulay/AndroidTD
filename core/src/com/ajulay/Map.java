package com.ajulay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Map implements Serializable {
    public class Route {
        private int startX, startY;
        private Vector2[] directions;

        public int getStartX() {
            return startX;
        }

        public int getStartY() {
            return startY;
        }

        public Vector2[] getDirections() {
            return directions;
        }

        public Route(String str) {
            String[] data = str.split(",");
            startX = Integer.parseInt(data[0]);
            startY = Integer.parseInt(data[1]);
            directions = new Vector2[data[2].length()];
            for (int i = 0; i < data[2].length(); i++) {
                if (data[2].charAt(i) == 'L') {
                    directions[i] = new Vector2(-1, 0);
                }
                if (data[2].charAt(i) == 'R') {
                    directions[i] = new Vector2(1, 0);
                }
                if (data[2].charAt(i) == 'U') {
                    directions[i] = new Vector2(0, 1);
                }
                if (data[2].charAt(i) == 'D') {
                    directions[i] = new Vector2(0, -1);
                }
            }
        }
    }

    public enum CellType {
        GRASS("0"), ROAD("1"), CROSSROAD("2"), HOME("5");

        private String symb;

        CellType(String symb) {
            this.symb = symb;
        }

        public static CellType getTypeFromString(String str) {
            for (int i = 0; i < CellType.values().length; i++) {
                if (CellType.values()[i].symb.equals(str)) {
                    return CellType.values()[i];
                }
            }
            return null;
        }
    }

    private String name;

    private transient int width;
    private transient int height;

    private transient CellType[][] data;
    private transient List<Route> routes;
    private transient TextureRegion textureGrass;
    private transient TextureRegion textureRoad;

    public List<Route> getRoutes() {
        return routes;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Map() {
        textureGrass = Assets.getInstance().getAtlas().findRegion("grass");
        textureRoad = Assets.getInstance().getAtlas().findRegion("road");
        name = "map1";
        loadMap(name);
    }

    public void reload() {
        textureGrass = Assets.getInstance().getAtlas().findRegion("grass");
        textureRoad = Assets.getInstance().getAtlas().findRegion("road");
        loadMap(name);
    }

    public void loadMap(String mapName) {
        List<String> lines = Utilities.readAllLinesFromFile(Gdx.files.internal(mapName + ".dat"));
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).equals("routes")) {
                height = i;
                break;
            }
        }
        width = lines.get(0).split(",").length;
        data = new CellType[width][height];
        for (int i = 0; i < height; i++) {
            String[] arr = lines.get(i).split(",");
            for (int j = 0; j < width; j++) {
                data[j][height - i - 1] = CellType.getTypeFromString(arr[j]);
            }
        }
        routes = new ArrayList<Route>();
        for (int i = height + 1; i < lines.size(); i++) {
            routes.add(new Route(lines.get(i)));
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (data[i][j] == CellType.GRASS) {
                    batch.draw(textureGrass, i * 80, j * 80);
                }
                if (data[i][j] == CellType.ROAD || data[i][j] == CellType.CROSSROAD) {
                    batch.draw(textureRoad, i * 80, j * 80);
                }
            }
        }
    }

    public boolean isCrossroad(int cx, int cy) {
        return data[cx][cy] == CellType.CROSSROAD;
    }

    public boolean isHome(int cx, int cy) {
        return data[cx][cy] == CellType.HOME;
    }

    public boolean isCellEmpty(int cx, int cy) {
        return data[cx][cy] == CellType.GRASS;
    }
}
