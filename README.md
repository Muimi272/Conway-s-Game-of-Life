# Conway's Game of Life

English | [中文](README-ZH.md)

This project is a Java-based implementation of the classic console version of **Conway's Game of Life**. The program supports custom grid dimensions and random initialization of live cell density, featuring built-in classic patterns such as the Glider and Blinker. The world space utilizes a **toroidal boundary** design; it evolves by one generation every 500ms by default and supports interactive exit.

It aims to provide Java beginners with an engaging, low-dependency, and well-structured example of object-oriented programming and console-based interaction.

---

## Project Architecture

The project adheres to a clear separation of concerns (MVC pattern):

| Class Path | Responsibility |
| --- | --- |
| `club.muimi.Main` | Program entry point; manages lifecycle scheduling and exit hook binding |
| `club.muimi.controller.Game` | Core controller; handles user input, initialization flow, and the evolution loop |
| `club.muimi.model.Universe` | Domain model; maintains grid data, neighbor statistics, and core evolution rules (B3/S23) |
| `club.muimi.model.Initializer` | State initialization factory; provides preset pattern loading and random distribution generation |
| `club.muimi.view.Console` | Console view layer; handles ANSI terminal redrawing, double-buffered output, and terminal state restoration |

---

## Key Implementation Features

1. **Double-Buffered, Clean Computation**: Each generation is calculated based on a read-only snapshot of the current state to produce an independent array for the next generation. Atomic replacement occurs only after calculation is complete, preventing "dirty data" contamination—where in-place updates might otherwise corrupt neighbor state assessments.
2. **Toroidal Topology (Torus World)**: Coordinates use modulo mapping to handle boundary wrapping, eliminating edge effects and allowing cells to transition smoothly across grid boundaries. 3. **Terminal State Protection**: Implements in-place refreshing and cursor hiding using ANSI control sequences; restores cursor visibility and the original terminal screen state upon program exit to avoid polluting the user's terminal environment.
4. **Robust Input Validation**: Enforces strict boundary checks on grid dimensions and density probability to ensure program stability.

---

## Compilation and Execution

### Requirements

* JDK 17 or higher

### Compilation and Startup

#### Windows (PowerShell)

```powershell
# Compile all source code to the bin directory
javac -encoding UTF-8 -d bin (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })

# Start the program
java -cp bin club.muimi.Main

```

#### Linux / macOS (Bash)

```bash
# Compile all source code to the bin directory
javac -encoding UTF-8 -d bin $(find src -name "*.java")

# Start the program
java -cp bin club.muimi.Main

```

---

## Configuration and Parameters

* **Size Limits**: Minimum grid width and height are `20`; maximum total cell count is `1,000,000`.
* **Parameter Input Example**: After selecting "Density Initialization" mode, enter the **mode number**, **width**, **height**, and **survival density** (between `0.0` and `1.0`) in sequence.
```text
2 30 20 0.3

```


*(Meaning: Mode 2, width 30, height 20, initial live cell ratio 30%)*
* **Exit Method**: During the simulation, enter `q` and press Enter to gracefully terminate the program and restore the terminal environment.
