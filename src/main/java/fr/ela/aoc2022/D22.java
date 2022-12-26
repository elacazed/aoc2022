package fr.ela.aoc2022;

import java.util.*;
import java.util.function.Function;

public class D22 extends AoC {

    /**
     * Instructions
     *
     * @param steps
     * @param turn
     */
    record Instruction(int steps, Direction turn) {
        public String toString() {
            if (turn == null) {
                return "Step " + steps;
            } else {
                return "turn " + turn;
            }
        }
    }

    public List<Instruction> readInstructions(String string) {
        List<Instruction> instructions = new ArrayList<>();
        int amount = -1;
        Direction d = Direction.UP;

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (Character.isDigit(c)) {
                amount = amount == -1 ? c - '0' : amount * 10 + (c - '0');
            } else {
                instructions.add(new Instruction(amount, null));
                amount = -1;
                instructions.add(new Instruction(-1, c == 'R' ? Direction.RIGHT : Direction.LEFT));
            }
        }
        if (amount != 0) {
            instructions.add(new Instruction(amount, null));
        }
        return instructions;
    }

    /**
     * Directions
     */
    enum Direction {
        RIGHT, DOWN, LEFT, UP;

        Direction turn(Direction d) {
            int index = (ordinal() + (d == RIGHT ? 1 : -1)) % 4;
            if (index == -1) {
                index = 3;
            }
            return Direction.values()[index];
        }
    }

    /** Classes for Part 1 */
    /**
     * Range : represents a line or a column limits in input, used to wrap around the map for part 1.
     *
     * @param min
     * @param max
     */
    record Range(int min, int max) {

        int wrapDown(int x) {
            return x == min ? max - 1 : x;
        }

        int wrapUp(int x) {
            return x == max ? min + 1 : x;
        }
    }

    /**
     * Position on the grid
     *
     * @param x
     * @param y
     */
    record Position(int x, int y) {

        public String toString() {
            return "["+x+","+y+"]";
        }
    }

    /**
     * The grid.
     */
    public class Grid {
        Set<Position> walls;

        Map<Integer, Range> lines;
        Map<Integer, Range> columns;

        public Grid(List<String> data) {
            walls = new HashSet<>();
            lines = new HashMap<>();
            columns = new HashMap<>();

            int width = data.stream().mapToInt(String::length).max().orElseThrow();

            int[] minY = new int[width];
            int[] maxY = new int[width];
            Arrays.fill(maxY, -1);
            Arrays.fill(minY, data.size() + 1);


            int y = 1;
            for (String line : data) {
                int minX = line.length() + 1;
                int maxX = -1;
                for (int x = 0; x < line.length(); x++) {
                    switch (line.charAt(x)) {
                        case '#':
                            walls.add(new Position(x + 1, y));
                        case '.':
                            minY[x] = Math.min(y, minY[x]);
                            maxY[x] = Math.max(maxY[x], y);
                            minX = Math.min(minX, x);
                            maxX = Math.max(maxX, x);
                            break;
                        default:
                            break;
                    }
                }
                lines.put(y, new Range(minX, maxX + 2));
                y++;
            }
            for (int x = 0; x < width; x++) {
                columns.put(x + 1, new Range(minY[x] - 1, maxY[x] + 1));
            }
        }

        Position move(Position from, Direction direction) {
            Position to = switch (direction) {
                case UP -> moveUp(from);
                case DOWN -> moveDown(from);
                case LEFT -> moveLeft(from);
                case RIGHT -> moveRight(from);
            };
            if (walls.contains(to)) {
                return from;
            } else {
                return to;
            }
        }

        private Position moveUp(Position from) {
            int y = from.y - 1;
            Range r = columns.get(from.x);
            return new Position(from.x, r.wrapDown(y));
        }

        private Position moveDown(Position from) {
            int y = from.y + 1;
            Range r = columns.get(from.x);
            return new Position(from.x, r.wrapUp(y));
        }

        private Position moveLeft(Position from) {
            int x = from.x - 1;
            Range r = lines.get(from.y);
            return new Position(r.wrapDown(x), from.y);
        }

        private Position moveRight(Position from) {
            int x = from.x + 1;
            Range r = lines.get(from.y);
            return new Position(r.wrapUp(x), from.y);
        }

    }


    public record Face(String name, int size, Position topLeft) {
        boolean isOnFace(Position position) {
            return position.x < topLeft.x + size &&
                    position.y < topLeft.y + size &&
                    position.x >= topLeft.x &&
                    position.y >= topLeft.y;
        }
    }

    public record Transition(Function<Position, Position> positionFunction,
                             Function<Direction, Direction> directionFunction) {

        public Position translate(Position pos) {
            return positionFunction.apply(pos);
        }

        public Direction translate(Direction direction) {
            return directionFunction.apply(direction);
        }

    }

    static Transition NOOP = new Transition(Function.identity(), Function.identity());


    public record Cube(Grid grid, List<Face> faces, Map<String, Transition> transitions) {

        public Face getFace(Position position) {
            Face face = faces.stream().filter(f -> f.isOnFace(position)).findFirst().orElse(null);
            if (face == null) {
                System.out.println("Oups, no face for position " + position);
            }
            return face;
        }

        public Transition getTransition(Face from, Direction d) {
            String key = from.name + d.name();
            if (transitions.containsKey(key)) {
                return transitions.get(key);
            } else {
                return NOOP;
            }
        }

    }

    public class Walker {
        Position position;
        Direction direction;

        Walker(Position start) {
            position = start;
            direction = Direction.RIGHT;
        }

        public String toString() {
            return direction.name() + " [" + position.x + ", " + position.y + "]";
        }

        public void follow(Instruction instruction, Cube cube) {
            if (instruction.turn == null) {
                walk(cube, instruction.steps);
            } else {
                this.direction = direction.turn(instruction.turn);
            }
        }

        public void follow(Instruction instruction, Grid grid) {
            if (instruction.turn == null) {
                walk(grid, instruction.steps);
            } else {
                this.direction = direction.turn(instruction.turn);
            }
        }

        public void walk(Cube cube, int steps) {
            for (int i = 0; i < steps; i++) {
                Face fromFace = cube.getFace(position);
                Position pos = cube.grid.move(position, direction);
                if (pos.equals(position)) {
                    return;
                }
                if (fromFace.isOnFace(pos)) {
                    this.position = pos;
                } else {
                    Transition transition = cube.getTransition(fromFace, direction);
                    if (transition.equals(NOOP)) {
                        this.position = pos;
                    } else {
                        Position portal = transition.translate(position);
                        if (cube.grid.walls.contains(portal)) {
                            return;
                        } else {
                            pos = position;
                            Direction d = direction;
                            this.position = portal;
                            this.direction = transition.translate(direction);
                            System.out.println("Transition " + fromFace.name + d.name() + " Pos = " + pos + " -> " + portal + ", " + direction);
                        }
                    }
                }
            }
        }

        public void walk(Grid grid, int steps) {
            for (int i = 0; i < steps; i++) {
                Position pos = grid.move(position, direction);
                if (pos.equals(position)) {
                    return;
                } else {
                    this.position = pos;
                }
            }
        }

        /**
         * Facing is 0 for right (>), 1 for down (v), 2 for left (<), and 3 for up (^).
         * The final password is the sum of 1000 times the row, 4 times the column,
         * and the facing
         */
        public int password() {
            return position.y * 1000 + position.x * 4 + direction.ordinal();
        }
    }


    public void partOne(String kind, Grid grid, List<Instruction> instructions) {
        Position start = new Position(grid.lines.get(1).min + 1, 1);
        Walker w = new Walker(start);
        instructions.forEach(i -> w.follow(i, grid));
        System.out.println(kind + " password = " + w.password());
    }

    public void partTwo(String kind, Grid grid, Cube cube, List<Instruction> instructions) {
        Position start = new Position(grid.lines.get(1).min + 1, 1);
        Walker w = new Walker(start);
        instructions.forEach(i -> w.follow(i, cube));
        System.out.println(kind + " part two password = " + w.password());
    }

    @Override
    public void run() {
        List<List<String>> testInput = splitOnEmptyLines(getTestInputPath());
        List<Instruction> testInstructions = readInstructions(testInput.get(1).get(0));
        Grid testGrid = new Grid(testInput.get(0));
        //partOne("Test", testGrid, testInstructions); // 6032
        Map<String, Transition> testTransitions = new HashMap<>();
        testTransitions.put("topLEFT", new Transition(p -> new Position(p.y + 4, 5), d -> Direction.DOWN));
        testTransitions.put("topRIGHT", new Transition(p -> new Position(16, 13 - p.y), d -> Direction.LEFT));
        testTransitions.put("topUP", new Transition(p -> new Position(13 - p.x, 5), d -> Direction.DOWN));

        testTransitions.put("leftUP", new Transition(p -> new Position(9, p.x - 4), d -> Direction.RIGHT));
        testTransitions.put("leftDOWN", new Transition(p -> new Position(9, p.x + 4), d -> Direction.RIGHT));

        testTransitions.put("frontRIGHT", new Transition(p -> new Position(21 - p.y, 9), d -> Direction.DOWN));

        testTransitions.put("backUP", new Transition(p -> new Position(13 - p.x, 1), d -> Direction.DOWN));
        testTransitions.put("backDOWN", new Transition(p -> new Position(9, p.x + 8), d -> Direction.UP));
        testTransitions.put("backLEFT", new Transition(p -> new Position(p.y + 8, 12), d -> Direction.UP));

        testTransitions.put("bottomLEFT", new Transition(p -> new Position(p.y - 4, 8), d -> Direction.UP));
        testTransitions.put("bottomDOWN", new Transition(p -> new Position(13 - p.x, 8), d -> Direction.UP));

        testTransitions.put("rightUP", new Transition(p -> new Position(12, 21 - p.x), d -> Direction.LEFT));
        testTransitions.put("rightRIGHT", new Transition(p -> new Position(12, 13 - p.y), d -> Direction.LEFT));
        testTransitions.put("rightDOWN", new Transition(p -> new Position(1, p.x - 8), d -> Direction.RIGHT));

        Cube testCube = new Cube(testGrid,
                List.of(new Face("top", 4, new Position(9, 1)),
                        new Face("front", 4, new Position(9, 5)),
                        new Face("left", 4, new Position(5, 5)),
                        new Face("right", 4, new Position(13, 9)),
                        new Face("back", 4, new Position(1, 5)),
                        new Face("bottom", 4, new Position(9, 9))),
                testTransitions);


        //partTwo("Test", testGrid, testCube, testInstructions); // 5031

        List<List<String>> input = splitOnEmptyLines(getInputPath());
        List<Instruction> instructions = readInstructions(input.get(1).get(0));
        Grid grid = new Grid(input.get(0));
        //partOne("Real", grid, instructions); // 162186
        Map<String, Transition> transitions = new HashMap<>();
        transitions.put("topLEFT", new Transition(p -> new Position(1, 151 - p.y), d -> Direction.RIGHT));
        transitions.put("leftLEFT", new Transition(p -> new Position(51, 151 - p.y), d -> Direction.RIGHT));

        transitions.put("topUP", new Transition(p -> new Position(1, 100 + p.x), d -> Direction.RIGHT));
        transitions.put("backLEFT", new Transition(p -> new Position(p.y - 100, 1), d -> Direction.DOWN));

        transitions.put("rightUP", new Transition(p -> new Position(p.x - 100, 200), d -> Direction.UP));
        transitions.put("backDOWN", new Transition(p -> new Position(p.x + 100, 1), d -> Direction.DOWN));

        transitions.put("rightRIGHT", new Transition(p -> new Position(100, 151 - p.y), d -> Direction.LEFT));
        transitions.put("bottomRIGHT", new Transition(p -> new Position(150, 151 - p.y), d -> Direction.LEFT));

        transitions.put("rightDOWN", new Transition(p -> new Position(100, p.x - 50), d -> Direction.LEFT));
        transitions.put("frontRIGHT", new Transition(p -> new Position(p.y + 50, 50), d -> Direction.UP));

        transitions.put("frontLEFT", new Transition(p -> new Position(p.y - 50, 101), d -> Direction.DOWN));
        transitions.put("leftUP", new Transition(p -> new Position(51, p.x + 50), d -> Direction.RIGHT));

        transitions.put("bottomDOWN", new Transition(p -> new Position(50, 100 + p.x), d -> Direction.LEFT));
        transitions.put("backRIGHT", new Transition(p -> new Position(p.y - 100, 150), d -> Direction.UP));


        Cube cube = new Cube(grid,
                List.of(new Face("top", 50, new Position(51, 1)),
                        new Face("front", 50, new Position(51, 51)),
                        new Face("left", 50, new Position(1, 101)),
                        new Face("right", 50, new Position(101, 1)),
                        new Face("back", 50, new Position(1, 151)),
                        new Face("bottom", 50, new Position(51, 101))),
                transitions);


        partTwo("Real", grid, cube, instructions);

    }

}
