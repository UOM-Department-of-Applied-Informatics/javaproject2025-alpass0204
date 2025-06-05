public class Monster extends Fighter {

    public Monster(int x, int y) {
        super(x, y);
    }

    @Override
    public char getSymbol() {
        return 'M';
    }
}
