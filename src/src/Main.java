import javax.swing.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Give map's width: ");
        int width = sc.nextInt();
        System.out.print("Give map's height: ");
        int height = sc.nextInt();

        int maxFighters = (width * height) / 15;
        System.out.print("Number of Knights/Monsters (<= " + maxFighters + "): ");
        int numFighters = sc.nextInt();
        sc.nextLine();

        Map map = new Map(width, height, numFighters);
        SwingUtilities.invokeLater(() -> new GameGUI(map));

        //Από εδώ και κάτω είναι η εκτέλεση χωρίς το GUI!
        /**
        boolean running = true;
        while (running) {
            map.printWorld();
            System.out.println("Move: w/a/s/d, p = pause/stats, q = quit");
            String input = sc.nextLine().trim().toLowerCase();

            switch (input) {
                case "w":
                case "a":
                case "s":
                case "d":
                    if (map.movePlayer(input)) {
                        map.moveFighters();
                        map.resolveInteractions();
                    } else {
                        System.out.println("You can't go there!");
                    }
                    break;
                case "p":
                    map.printStats();
                    break;
                case "q":
                    running = false;
                    System.out.println("Quit game.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
            // Now I check whether a team has been defeated.
            if (map.getKnights().isEmpty()) {
                System.out.println("Monsters have won!");
                break;
            }
            if (map.getMonsters().isEmpty()) {
                System.out.println("Knights have won!");
                break;
            }
        }
         **/
    }

}