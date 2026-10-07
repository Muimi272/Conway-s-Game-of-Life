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

    public Game() {
        scanner = new Scanner(System.in).useLocale(Locale.ROOT);
        console = new Console();
    }

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

    private void initWithRandom() {
        int width = getDimension("Please input your universe's width: ", 1);
        int height = getDimension("Please input your universe's height: ", width);
        initializeUniverse(Initializer.initialize(width, height), width, height);
    }

    private void initWithDensity() {
        int width = getDimension("Please input your universe's width: ", 1);
        int height = getDimension("Please input your universe's height: ", width);
        double density = getDouble("Please input live-cell density (0-1): ", 0, 1);
        initializeUniverse(Initializer.initialize(width, height, density), width, height);
    }

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

    private void setCell(byte[] state, int width, int height, int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            state[x + y * width] = 1;
        }
    }

    private void initializeUniverse(Universe newUniverse, int width, int height) {
        universe = newUniverse;
        console.initBuffer(width, height);
        console.clear();
    }

    private int getDimension(String prompt, int otherDimension) {
        while (true) {
            int value = getInt(prompt);
            if (value >= MIN_DIMENSION && (long) value * otherDimension <= MAX_CELLS) {
                return value;
            }
            console.print("Dimensions must be at least 20 and contain at most 1,000,000 cells.\n");
        }
    }

    private boolean sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

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

    public void close() {
        running = false;
        console.restoreTerminal();
    }

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
