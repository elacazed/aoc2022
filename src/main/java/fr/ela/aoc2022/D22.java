package fr.ela.aoc2022;

import java.nio.file.Path;
import java.util.*;

public class D22 extends AoC {

    record Instruction(int steps, Direction turn) {
        public String toString() {
            if (turn == null) {
                return "Step " + steps;
            } else {
                return "turn " + turn;
            }
        }
    }

    record Range(int min, int max) {

        int wrapDown(int x) {
            return x == min ? max - 1 : x;
        }

        int wrapUp(int x) {
            return x == max ? min + 1 : x;
        }
    }

    record Position(int x, int y) {
    }

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

        public void follow(Instruction instruction, Grid grid) {
            String before = toString();
            if (instruction.turn == null) {
                walk(grid, instruction.steps);
            } else {
                turn(instruction.turn);
            }
            //System.out.println(before + " -> " + instruction + " : " + toString());
        }

        public void turn(Direction d) {
            this.direction = direction.turn(d);
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


    public void partOne(String kind, Path path) {
        List<List<String>> input = splitOnEmptyLines(path);
        Grid grid = new Grid(input.get(0));
        Position start = new Position(grid.lines.get(1).min + 1, 1);
        Walker w = new Walker(start);
        List<Instruction> instructions = readInstructions(input.get(1).get(0));
        instructions.forEach(i -> w.follow(i, grid));
        System.out.println(kind + " password = " + w.password());
    }

    public void partTwo(String kind, Path path) {

    }

    @Override
    public void run() {
        partOne("Test", getTestInputPath());
        partOne("Real", getInputPath());

        //partTwo("Test", getTestInputPath());
        //partTwo("Real", getInputPath());

    }

}
