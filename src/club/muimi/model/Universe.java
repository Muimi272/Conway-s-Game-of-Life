package club.muimi.model;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Universe {
    private final int width;
    private final int height;
    private byte[] currentState;

    public Universe(int width, int height) {
        this(width, height, 0.5);
    }

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

    private static void validateDimensions(int width, int height) {
        if (width < 1 || height < 1 || (long) width * height > 1_000_000) {
            throw new IllegalArgumentException("Dimensions must be positive and contain at most 1,000,000 cells.");
        }
    }

    public byte[] getCurrentState() {
        return Arrays.copyOf(currentState, currentState.length);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public byte getState(int x, int y) {
        return currentState[getIndex(x, y)];
    }

    public int getIndex(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("Cell coordinates are outside the universe.");
        }
        return x + y * width;
    }

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
