import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameGUI extends JFrame implements KeyListener {
    private Map map;
    private int cellSize = 30; // cell size in pixels

    public GameGUI(Map map) {
        this.map = map;
        setTitle("Knights vs Monsters");
        setSize(map.getWidth()*cellSize+16, map.getHeight()*cellSize+39);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        //addKeyListener(this);


        JPanel panel = new JPanel(){

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Grid
                for (int y = 0; y < map.getHeight(); y++) {
                    for (int x = 0; x < map.getWidth(); x++) {
                        char c = map.getTerrain(x, y);
                        switch (c) {
                            case '.': g.setColor(new Color(170, 255, 170)); break; // earth
                            case '%': g.setColor(new Color(60,120,50)); break; // tree
                            case '~': g.setColor(new Color(50,180,255)); break; // water
                            default: g.setColor(Color.LIGHT_GRAY);
                        }
                        g.fillRect(x*cellSize, y*cellSize, cellSize, cellSize);
                    }
                }
                // Entities

                for (Knight k : map.getKnights()) {
                    g.setColor(Color.BLUE);
                    g.fillOval(k.getX()*cellSize+6, k.getY()*cellSize+6, cellSize-12, cellSize-12);
                }
                for (Monster m : map.getMonsters()) {
                    g.setColor(Color.RED);
                    g.fillOval(m.getX()*cellSize+6, m.getY()*cellSize+6, cellSize-12, cellSize-12);
                }
                Player p = map.getPlayer();
                if (p != null) {
                    g.setColor(Color.BLACK);
                    g.fillRect(p.getX()*cellSize+8, p.getY()*cellSize+8, cellSize-16, cellSize-16);
                }
            }
        };

        setContentPane(panel);

        panel.setFocusable(true);
        panel.requestFocusInWindow();
        panel.addKeyListener(this);


        //this.requestFocusInWindow();
        setVisible(true);
        panel.requestFocus();
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        char key = e.getKeyChar();
        boolean moved = false;
        if ("wasd".indexOf(key) >= 0) {
            moved = map.movePlayer(Character.toString(key));
            if (moved) {
                map.moveFighters();
                map.resolveInteractions();
            }
            repaint();
        } else if (key == 'p') {
            JOptionPane.showMessageDialog(this, map.getStatsString(), "Statistics", JOptionPane.INFORMATION_MESSAGE);
        } else if (key == 'q') {
            JOptionPane.showMessageDialog(this, "Quit game.");
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }
}
