import java.util.Random;

public abstract class Fighter extends Entity {

    protected int health;
    protected int power;
    protected int defense;
    protected int potion;
    protected Random random = new Random();

    public Fighter(int x, int y) {
        super(x, y);
        this.health = 3;
        this.power = random.nextInt(3) + 1;   // [1-3]
        this.defense = random.nextInt(2) + 1; // [1-2]
        this.potion = random.nextInt(3);      // [0-2]
    }

    public int getHealth() { return health; }
    public void changeHealth(int delta) { health += delta; }
    public int getPower() { return power; }
    public int getDefense() { return defense; }
    public int getPotion() { return potion; }
    public void usePotion() { if(potion > 0) potion--; }
    public void addPotion() { potion++; }

    //TODO: extras για μαχη, θεραπεια κτλ
}
