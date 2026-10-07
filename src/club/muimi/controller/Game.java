package club.muimi.controller;

import club.muimi.model.*;
import club.muimi.view.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Game {
    private final Scanner scanner;
    private final Console console;
    private Universe universe;
    private boolean running;
    private final BlockingQueue<String> commands = new LinkedBlockingQueue<>();
    private final Deque<String> inputTokens = new ArrayDeque<>();

    private static final int UPDATE_INTERVAL_MS = 100;
    private static final int MIN_DIMENSION = 20;
    private static final int MAX_CELLS = 1_000_000;

    /** Creates the controller, input scanner, and console view. */
    public Game() {
        scanner = new Scanner(System.in).useLocale(Locale.ROOT);
        console = new Console();
    }

    /**
     * Initializes the terminal and displays the setup menu until the user
     * selects a valid mode and the universe has been created.
     *
     * @throws IllegalStateException if the input stream ends during initialization
     */
    public void init() {
        console.initTerminal();
        printLogo();
        console.print("Initializing...\n");
        sleep(1000);
        console.print("Universe initialized.\n");
        while (true) {
            int choice = getInt("How would you like to initialize the universe?\n1. Random\n2. Density\n3. Specific\n");
            if (choice == 1) {
                initWithRandom();
            } else if (choice == 2) {
                initWithDensity();
            } else if (choice == 3) {
                initWithSpecific();
            } else {
                console.print("Invalid choice. Please try again.\n");
                sleep(1000);
                continue;
            }
            break;
        }
    }

    /** Reads the universe dimensions and initializes it with the default 0.5 live-cell density. */
    private void initWithRandom() {
        int width = getDimension("Please input your universe's width: ", 1);
        int height = getDimension("Please input your universe's height: ", width);
        initializeUniverse(Initializer.initialize(width, height), width, height);
    }

    /** Reads the dimensions and live-cell density, then initializes the universe randomly. */
    private void initWithDensity() {
        int width = getDimension("Please input your universe's width: ", 1);
        int height = getDimension("Please input your universe's height: ", width);
        double density = getDouble("Please input live-cell density (0-1): ", 0, 1);
        initializeUniverse(Initializer.initialize(width, height, density), width, height);
    }

    /** Reads the dimensions and loads either a glider or blinker pattern. */
    private void initWithSpecific() {
        int width = getDimension("Please input your universe's width: ", 1);
        int height = getDimension("Please input your universe's height: ", width);
        int pattern = getInt("Choose a pattern: 1. Glider  2. Blinker\n");
        while (pattern < 1 || pattern > 2) {
            console.print("Please choose 1 or 2.\n");
            pattern = getInt("Choose a pattern: 1. Glider  2. Blinker\n");
        }
        byte[] state = new byte[width * height];
        if (pattern == 1) {
            setCell(state, width, height, 1, 0);
            setCell(state, width, height, 2, 1);
            setCell(state, width, height, 0, 2);
            setCell(state, width, height, 1, 2);
            setCell(state, width, height, 2, 2);
        } else {
            setCell(state, width, height, width / 2 - 1, height / 2);
            setCell(state, width, height, width / 2, height / 2);
            setCell(state, width, height, width / 2 + 1, height / 2);
        }
        initializeUniverse(Initializer.initialize(width, height, state), width, height);
    }

    /**
     * Sets the specified position to a live cell; out-of-range coordinates are ignored.
     *
     * @param state row-major cell-state array
     * @param width grid width
     * @param height grid height
     * @param x zero-based horizontal coordinate
     * @param y zero-based vertical coordinate
     */
    private void setCell(byte[] state, int width, int height, int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            state[x + y * width] = 1;
        }
    }

    /**
     * Stores the created universe and prepares the render buffer and screen.
     *
     * @param newUniverse newly created universe
     * @param width universe width; must match newUniverse
     * @param height universe height; must match newUniverse
     */
    private void initializeUniverse(Universe newUniverse, int width, int height) {
        universe = newUniverse;
        console.initBuffer(width, height);
        console.clear();
    }

    /**
     * Repeatedly reads a dimension and rejects values below the minimum
     * or values that exceed the maximum total cell count.
     *
     * @param prompt input prompt
     * @param otherDimension other dimension used for the cell-count check; use 1 for width
     * @return a validated dimension
     * @throws IllegalStateException if the input stream ends
     */
    private int getDimension(String prompt, int otherDimension) {
        while (true) {
            int value = getInt(prompt);
            if (value >= MIN_DIMENSION && (long) value * otherDimension <= MAX_CELLS) {
                return value;
            }
            console.print("Dimensions must be at least 20 and contain at most 1,000,000 cells.\n");
        }
    }

    /**
     * Pauses the current thread and restores the interrupt flag if interrupted.
     *
     * @param milliseconds pause duration in milliseconds
     * @return true when the pause completes normally, otherwise false
     */
    private boolean sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /** Prints the game title banner. */
    private void printLogo() {
        console.print("""

       ██████╗ ██████╗ ███╗   ██╗██╗    ██╗ █████╗ ██╗   ██╗ ██╗ ███████╗
      ██╔════╝██╔═══██╗████╗  ██║██║    ██║██╔══██╗╚██╗ ██╔╝ █╔╝ ██╔════╝
      ██║     ██║   ██║██╔██╗ ██║██║ █╗ ██║███████║ ╚████╔╝  ╚╝  ███████╗
      ██║     ██║   ██║██║╚██╗██║██║███╗██║██╔══██║  ╚██╔╝       ╚════██║
      ╚██████╗╚██████╔╝██║ ╚████║╚███╔███╔╝██║  ██║   ██║        ███████║
       ╚═════╝ ╚═════╝ ╚═╝  ╚═══╝ ╚══╝╚══╝ ╚═╝  ╚═╝   ╚═╝        ╚══════╝

       ██████╗  █████╗ ███╗   ███╗███████╗     ██████╗ ███████╗    ██╗     ██╗███████╗███████╗
      ██╔════╝ ██╔══██╗████╗ ████║██╔════╝    ██╔═══██╗██╔════╝    ██║     ██║██╔════╝██╔════╝
      ██║  ███╗███████║██╔████╔██║█████╗      ██║   ██║█████╗      ██║     ██║█████╗  █████╗
      ██║   ██║██╔══██║██║╚██╔╝██║██╔══╝      ██║   ██║██╔══╝      ██║     ██║██╔══╝  ██╔══╝
      ╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗    ╚██████╔╝██║         ███████╗██║██╗     ███████╗
       ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝     ╚═════╝ ╚═╝         ╚═════╝╚═╝╚═╝      ╚══════╝


       """
       );
    }

    /**
     * Reads an integer and asks the user to retry invalid or out-of-range input.
     *
     * @param prompt input prompt
     * @return the parsed integer
     * @throws IllegalStateException if the input stream ends
     */
    private int getInt(String prompt) {
        while (true) {
            console.print(prompt);
            String token = nextInputToken();
            try {
                return Integer.parseInt(token);
            } catch (NumberFormatException e) {
                console.print("Invalid input. Please enter an integer.\n");
            }
        }
    }

    /**
     * Reads a finite double in the specified inclusive range and asks the user
     * to retry malformed or out-of-range input.
     *
     * @param prompt input prompt
     * @param minimum inclusive lower bound
     * @param maximum inclusive upper bound
     * @return the validated double
     * @throws IllegalStateException if the input stream ends
     */
    private double getDouble(String prompt, double minimum, double maximum) {
        while (true) {
            console.print(prompt);
            String token = nextInputToken();
            try {
                double value = Double.parseDouble(token);
                if (Double.isFinite(value) && value >= minimum && value <= maximum) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // The common validation message below also covers malformed numbers.
            }
            console.print("Enter a number between " + minimum + " and " + maximum + ".\n");
        }
    }

    /**
     * Returns the next input token, reading and splitting a line when the cache is empty.
     * Empty lines prompt a retry, while multiple tokens on one line are retained in order.
     *
     * @return the next token to parse
     * @throws IllegalStateException if the input stream ends while the cache is empty
     */
    private String nextInputToken() {
        while (inputTokens.isEmpty()) {
            if (!scanner.hasNextLine()) {
                throw new IllegalStateException("Input ended.");
            }
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                console.print("Input cannot be empty. Please try again.\n");
                continue;
            }
            for (String token : line.split("\\s+")) {
                inputTokens.addLast(token);
            }
        }
        return inputTokens.removeFirst();
    }

    /** Sets the stop flag and restores the terminal during shutdown. */
    public void close() {
        running = false;
        console.restoreTerminal();
    }

    /**
     * Renders and advances the universe at the configured interval until q is received,
     * the input stream ends, or the thread is interrupted. The terminal is restored on exit.
     *
     * @throws IllegalStateException if the universe has not been initialized
     */
    public void run() {
        if (universe == null) {
            throw new IllegalStateException("Universe has not been initialized.");
        }
        running = true;
        startCommandReader();
        try {
            while (running) {
                console.render(universe);
                universe.next();
                if (!sleep(UPDATE_INTERVAL_MS)) {
                    running = false;
                    break;
                }
                String command = commands.poll();
                if (command != null && command.trim().equalsIgnoreCase("q")) {
                    running = false;
                }
            }
        } finally {
            running = false;
            console.restoreTerminal();
        }
    }

    /**
     * Starts a daemon thread that reads runtime commands line by line
     * and passes them to the main loop. End of input queues q to stop the loop.
     */
    private void startCommandReader() {
        Thread reader = new Thread(() -> {
            while (running && scanner.hasNextLine()) {
                commands.offer(scanner.nextLine());
            }
            if (running) {
                commands.offer("q");
            }
        }, "life-command-reader");
        reader.setDaemon(true);
        reader.start();
    }
}
