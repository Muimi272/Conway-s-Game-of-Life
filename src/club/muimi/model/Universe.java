package club.muimi.model;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Universe {
    private final int width;
    private final int height;
    private byte[] currentState;

    /**
     * Creates a universe with the default random live-cell density.
     *
     * @param width universe width
     * @param height universe height
     * @throws IllegalArgumentException if dimensions are invalid
     */
    public Universe(int width, int height) {
        this(width, height, 0.5);
    }

    /**
     * Creates a universe from a copied row-major cell-state snapshot.
     *
     * @param width universe width
     * @param height universe height
     * @param state initial cell states, each of which must be 0 or 1
     * @throws IllegalArgumentException if dimensions or state are invalid
     */
    public Universe(int width, int height, byte[] state) {
        validateDimensions(width, height);
        if (state == null || state.length != width * height) {
            throw new IllegalArgumentException("State length must match the universe dimensions.");
        }
        for (byte cell : state) {
            if (cell != 0 && cell != 1) {
                throw new IllegalArgumentException("Cell state must be 0 or 1.");
            }
        }
        this.width = width;
        this.height = height;
        this.currentState = Arrays.copyOf(state, state.length);
    }

    /**
     * Creates a universe by independently sampling each cell at the given density.
     *
     * @param width universe width
     * @param height universe height
     * @param density live-cell probability in the inclusive range [0, 1]
     * @throws IllegalArgumentException if dimensions or density are invalid
     */
    public Universe(int width, int height, double density) {
        validateDimensions(width, height);
        if (!Double.isFinite(density) || density < 0 || density > 1) {
            throw new IllegalArgumentException("Density must be between 0 and 1.");
        }
        this.width = width;
        this.height = height;
        this.currentState = new byte[width * height];
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < currentState.length; i++) {
            currentState[i] = (byte) (random.nextDouble() < density ? 1 : 0);
        }
    }

    /** Validates positive dimensions and the maximum supported cell count. */
    private static void validateDimensions(int width, int height) {
        if (width < 1 || height < 1 || (long) width * height > 1_000_000) {
            throw new IllegalArgumentException("Dimensions must be positive and contain at most 1,000,000 cells.");
        }
    }

    /** Returns a defensive copy of the current row-major cell-state snapshot. */
    public byte[] getCurrentState() {
        return Arrays.copyOf(currentState, currentState.length);
    }

    /** Returns the universe width. */
    public int getWidth() {
        return width;
    }

    /** Returns the universe height. */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the state of the cell at the given coordinates.
     *
     * @param x horizontal coordinate, starting at zero
     * @param y vertical coordinate, starting at zero
     * @return 1 if alive, otherwise 0
     * @throws IndexOutOfBoundsException if the coordinates are outside the universe
     */
    public byte getState(int x, int y) {
        return currentState[getIndex(x, y)];
    }

    /**
     * Converts valid coordinates to their row-major array index.
     *
     * @param x horizontal coordinate, starting at zero
     * @param y vertical coordinate, starting at zero
     * @return the corresponding cell-state array index
     * @throws IndexOutOfBoundsException if the coordinates are outside the universe
     */
    public int getIndex(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("Cell coordinates are outside the universe.");
        }
        return x + y * width;
    }

    /** Counts the eight neighbors, wrapping coordinates across the universe edges. */
    private int getNeighbors(int x, int y) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int nx = Math.floorMod(x + dx, width);
                int ny = Math.floorMod(y + dy, height);
                count += currentState[getIndex(nx, ny)];
            }
        }
        return count;
    }

    /**
     * Advances one generation using the current state as an immutable snapshot.
     * The computed state replaces the current state only after every cell is processed.
     */
    public void next() {
        byte[] nextState = new byte[currentState.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = getIndex(x, y);
                int neighbors = getNeighbors(x, y);
                byte cell = currentState[index];
                nextState[index] = (byte) (cell == 1
                        ? (neighbors == 2 || neighbors == 3 ? 1 : 0)
                        : (neighbors == 3 ? 1 : 0));
            }
        }
        currentState = nextState;
    }
}
