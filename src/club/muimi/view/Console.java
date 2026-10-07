package club.muimi.view;

import club.muimi.model.Universe;

public class Console {

    private static final String CURSOR_HOME = "\033[H";
    private static final String HIDE_CURSOR = "\033[?25l";
    private static final String SHOW_CURSOR = "\033[?25h";
    private static final String ENTER_ALT_SCREEN = "\033[?1049h";
    private static final String EXIT_ALT_SCREEN = "\033[?1049l";

    private final java.io.PrintStream out;
    private StringBuilder buffer;
    private boolean isInitialized = false;
    private boolean terminalInitialized = false;

    public Console() {
        this.out = System.out;
    }

    public void initBuffer(int width, int height) {
        int capacity = 32 + (width * 2 + 1) * height;
        this.buffer = new StringBuilder(capacity);
        isInitialized = true;
    }

    public void initTerminal() {
        if (!terminalInitialized) {
            out.print(ENTER_ALT_SCREEN + HIDE_CURSOR);
            out.flush();
            terminalInitialized = true;
        }
    }

    public void restoreTerminal() {
        if (terminalInitialized) {
            out.print(SHOW_CURSOR + EXIT_ALT_SCREEN);
            out.flush();
            terminalInitialized = false;
        }
    }

    public void print(String message) {
        out.print(message);
        out.flush();
    }

    public void render(Universe universe) {
        if (!isInitialized) {
            return;
        }
        buffer.setLength(0);
        buffer.append(CURSOR_HOME);
        int height = universe.getHeight();
        int width = universe.getWidth();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (universe.getState(x, y) == 1) {
                    buffer.append("██");
                } else {
                    buffer.append("  ");
                }
            }
            buffer.append('\n');
        }
        out.print(buffer.toString());
        out.flush();
    }

    public void clear() {
        if (!isInitialized) {
            return;
        }
        buffer.setLength(0);
        out.print("\033[2J" + CURSOR_HOME);
        out.flush();
    }
}
