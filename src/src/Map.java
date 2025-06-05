import java.util.ArrayList;
import java.util.Random;

public class Map {

    private int width, height;
    private char[][] grid;
    private Player player;
    private ArrayList<Knight> knights;
    private ArrayList<Monster> monsters;
    private Random random = new Random();

    private static final int[][] NEIGHBORS = {
            {0,1}, {1,0}, {0,-1}, {-1,0}, // σταυρός
            {1,1}, {-1,1}, {1,-1}, {-1,-1} // διαγώνια
    };
    private static final int[][] CROSS = {
            {0,1}, {1,0}, {0,-1}, {-1,0}
    };


    public Map(int width, int height, int numFighters) {
        this.width = width;
        this.height = height;
        grid = new char[height][width];
        knights = new ArrayList<>();
        monsters = new ArrayList<>();
        generateTerrain();
        placeEntities(numFighters);
    }

    private void generateTerrain(){
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double r = random.nextDouble();
                if (r < 0.10) grid[i][j] = '~';      //I put 10% water
                else if (r < 0.22) grid[i][j] = '%'; //22% trees
                else grid[i][j] = '.';               //rest earth
            }
        }

    }

    private boolean isEmpty(int x, int y) {
        // Δεν πρέπει να έχει οντότητα ή εμπόδιο
        if (grid[y][x] != '.') return false;
        if (player != null && player.getX() == x && player.getY() == y) return false;
        for (Fighter f : knights) if (f.getX() == x && f.getY() == y) return false;
        for (Fighter f : monsters) if (f.getX() == x && f.getY() == y) return false;
        return true;
    }

    private void placeEntities(int numFighters) {

        int x, y;
        do {
            x = random.nextInt(width);
            y = random.nextInt(height);
        } while (!isEmpty(x, y));
        player = new Player(x, y);

        // Knights
        for (int i = 0; i < numFighters; i++) {
            do {
                x = random.nextInt(width);
                y = random.nextInt(height);
            } while (!isEmpty(x, y));
            knights.add(new Knight(x, y));
        }
        // Monsters
        for (int i = 0; i < numFighters; i++) {
            do {
                x = random.nextInt(width);
                y = random.nextInt(height);
            } while (!isEmpty(x, y));
            monsters.add(new Monster(x, y));
        }
    }

    public void printWorld() {

        char[][] display = new char[height][width];
        for (int i = 0; i < height; i++)
            System.arraycopy(grid[i], 0, display[i], 0, width);

        for (Knight k : knights)
            display[k.getY()][k.getX()] = k.getSymbol();
        for (Monster m : monsters)
            display[m.getY()][m.getX()] = m.getSymbol();
        if (player != null)
            display[player.getY()][player.getX()] = player.getSymbol();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++)
                System.out.print(display[i][j]);
            System.out.println();
        }
    }

    // Getters

    public Player getPlayer() { return player; }
    public ArrayList<Knight> getKnights() { return knights; }
    public ArrayList<Monster> getMonsters() { return monsters; }
    public boolean isPassable(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return false;
        return grid[y][x] == '.'; // μόνο η γη περνιέται
    }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public boolean movePlayer(String dir) {
        int x = player.getX();
        int y = player.getY();
        int nx = x, ny = y;

        switch (dir) {
            case "w": ny--; break;
            case "s": ny++; break;
            case "a": nx--; break;
            case "d": nx++; break;
        }
        if (nx < 0 || nx >= width || ny < 0 || ny >= height) return false;
        if (!isEmpty(nx, ny)) return false;
        if (!isPassable(nx, ny)) return false;

        player.setPosition(nx, ny);
        return true;
    }

    public void moveFighters() {
        moveKnights();
        moveMonsters();
    }

    private void moveKnights() {
        for (Knight k : knights) {
            int[][] moves = {{0,1},{0,-1},{1,0},{-1,0},{0,0}}; // κάτω, πάνω, δεξιά, αριστερά, ή να μείνει
            int[] move = moves[random.nextInt(moves.length)];
            int nx = k.getX() + move[0];
            int ny = k.getY() + move[1];
            if (nx >= 0 && nx < width && ny >= 0 && ny < height &&
                    isEmpty(nx, ny) && isPassable(nx, ny)) {
                k.setPosition(nx, ny);
            }
        }
    }

    private void moveMonsters() {
        for (Monster m : monsters) {
            int[][] moves = {
                    {0,1},{0,-1},{1,0},{-1,0}, // σταυρός
                    {1,1},{-1,1},{1,-1},{-1,-1}, // διαγώνια
                    {0,0} // να μείνει
            };
            int[] move = moves[random.nextInt(moves.length)];
            int nx = m.getX() + move[0];
            int ny = m.getY() + move[1];
            if (nx >= 0 && nx < width && ny >= 0 && ny < height &&
                    isEmpty(nx, ny) && isPassable(nx, ny)) {
                m.setPosition(nx, ny);
            }
        }
    }

    public void printStats() {
        int sumKnights = knights.stream().mapToInt(Knight::getHealth).sum();
        int sumMonsters = monsters.stream().mapToInt(Monster::getHealth).sum();
        System.out.println("Available Knights: " + knights.size());
        System.out.println("Available Monsters: " + monsters.size());
        System.out.println("Total health Knights: " + sumKnights);
        System.out.println("Total health Monsters: " + sumMonsters);
    }

    public void resolveInteractions(){
        // 1. Healing between Knights
        for (int i = 0; i < knights.size(); i++) {
            Knight k1 = knights.get(i);
            for (int[] d : CROSS) {
                int nx = k1.getX() + d[0];
                int ny = k1.getY() + d[1];
                if (!isInside(nx, ny)) continue;

                for (int j = 0; j < knights.size(); j++) {
                    if (i == j) continue;
                    Knight k2 = knights.get(j);
                    if (k2.getX() == nx && k2.getY() == ny) {
                        // If someone needs heal
                        if (k1.getHealth() < 3 && k2.getPotion() > 0) {
                            k1.changeHealth(1);
                            k2.usePotion();
                            System.out.println("Knight in ("+k1.getX()+","+k1.getY()+") was healed by Knight in ("+k2.getX()+","+k2.getY()+")");
                        }
                    }
                }
            }
        }
        // 2. Healing between Monsters
        for (int i = 0; i < monsters.size(); i++) {
            Monster m1 = monsters.get(i);
            for (int[] d : NEIGHBORS) {
                int nx = m1.getX() + d[0];
                int ny = m1.getY() + d[1];
                if (!isInside(nx, ny)) continue;
                for (int j = 0; j < monsters.size(); j++) {
                    if (i == j) continue;
                    Monster m2 = monsters.get(j);
                    if (m2.getX() == nx && m2.getY() == ny) {
                        if (m1.getHealth() < 3 && m2.getPotion() > 0) {
                            m1.changeHealth(1);
                            m2.usePotion();
                            System.out.println("Monster in ("+m1.getX()+","+m1.getY()+") healed by Monster in ("+m2.getX()+","+m2.getY()+")");
                        }
                    }
                }
            }
        }

        // 3. Fight — Knights VS Monsters
        // First Knights vs Monsters
        ArrayList<Monster> deadMonsters = new ArrayList<>();
        for (Knight k : knights) {
            for (int[] d : CROSS) { // μόνο σταυρός
                int nx = k.getX() + d[0];
                int ny = k.getY() + d[1];
                if (!isInside(nx, ny)) continue;
                for (Monster m : monsters) {
                    if (m.getX() == nx && m.getY() == ny) {
                        // Knight attacks only if power >= defense Monster
                        if (k.getPower() >= m.getDefense()) {
                            int damage = k.getPower() - m.getDefense();
                            if (damage > 0) {
                                m.changeHealth(-damage);
                                System.out.println("Knight in ("+k.getX()+","+k.getY()+") attacked Monster in ("+m.getX()+","+m.getY()+")");
                            }
                            if (m.getHealth() <= 0 && !deadMonsters.contains(m)) {
                                System.out.println("Monster in ("+m.getX()+","+m.getY()+") is dead!");
                                deadMonsters.add(m);
                            }
                        }
                    }
                }
            }
        }
        // Monsters vs Knights
        ArrayList<Knight> deadKnights = new ArrayList<>();
        for (Monster m : monsters) {
            for (int[] d : NEIGHBORS) {
                int nx = m.getX() + d[0];
                int ny = m.getY() + d[1];
                if (!isInside(nx, ny)) continue;
                for (Knight k : knights) {
                    if (k.getX() == nx && k.getY() == ny) {
                        if (m.getPower() >= k.getDefense()) {
                            int damage = m.getPower() - k.getDefense();
                            if (damage > 0) {
                                k.changeHealth(-damage);
                                System.out.println("Monster in ("+m.getX()+","+m.getY()+") attacked Knight in ("+k.getX()+","+k.getY()+")");
                            }
                            if (k.getHealth() <= 0 && !deadKnights.contains(k)) {
                                System.out.println("Knight in ("+k.getX()+","+k.getY()+") is dead!");
                                deadKnights.add(k);
                            }
                        }
                    }
                }
            }
        }

        knights.removeAll(deadKnights);
        monsters.removeAll(deadMonsters);
    }


    private boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;

    }


}
