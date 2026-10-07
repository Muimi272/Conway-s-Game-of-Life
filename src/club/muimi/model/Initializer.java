package club.muimi.model;

public final class Initializer {
    private Initializer() {
    }

    public static Universe initialize(int width, int height, double density) {
        return new Universe(width, height, density);
    }

    public static Universe initialize(int width, int height) {
        return new Universe(width, height);
    }

    public static Universe initialize(int width, int height, byte[] state) {
        return new Universe(width, height, state);
    }
}
