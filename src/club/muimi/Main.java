package club.muimi;

import club.muimi.controller.Game;

// Conway's Game of Life
// BY Muimi
public class Main {
    /**
     * Creates the game controller and starts the initialization and evolution loop.
     *
     * @param args command-line arguments; this application does not use them
     */
    public static void main(String[] args) {
        Game game = new Game();
        try {
            game.init();
            game.run();
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        } finally {
            game.close();
        }
    }
}
