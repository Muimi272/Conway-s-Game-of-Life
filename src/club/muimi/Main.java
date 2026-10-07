package club.muimi;

import club.muimi.controller.Game;

// Conway's Life Game
public class Main {
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
