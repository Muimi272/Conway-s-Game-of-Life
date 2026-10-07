package club.muimi.model;

public final class Initializer {
    /** Prevents instantiation of this utility class. */
    private Initializer() {
    }

    /**
     * Creates a universe with randomly generated cells at the requested density.
     *
     * @param width universe width
     * @param height universe height
     * @param density probability that each cell starts alive
     * @return a newly initialized universe
     */
    public static Universe initialize(int width, int height, double density) {
        return new Universe(width, height, density);
    }

    /**
     * Creates a universe with the default random live-cell density.
     *
     * @param width universe width
     * @param height universe height
     * @return a newly initialized universe
     */
    public static Universe initialize(int width, int height) {
        return new Universe(width, height);
    }

    /**
     * Creates a universe from an explicit cell-state snapshot.
     *
     * @param width universe width
     * @param height universe height
     * @param state initial state in row-major order
     * @return a newly initialized universe
     */
    public static Universe initialize(int width, int height, byte[] state) {
        return new Universe(width, height, state);
    }
}
