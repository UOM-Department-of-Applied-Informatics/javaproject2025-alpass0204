public abstract class Entity {

    protected int x, y; // Συντεταγμένες στο χάρτη

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }

    public abstract char getSymbol();
}
