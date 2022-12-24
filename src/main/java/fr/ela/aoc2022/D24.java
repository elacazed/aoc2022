package fr.ela.aoc2022;

import java.nio.file.Path;
import java.sql.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class D24 extends AoC {

    public record Position(int x, int y) {
    }

    public enum Direction {

        NORTH(p -> new Position(p.x, p.y - 1)),
        WEST(p -> new Position(p.x - 1, p.y)),
        SOUTH(p -> new Position(p.x, p.y + 1)),
        EAST(p -> new Position(p.x + 1, p.y)),
        STEADY(p -> p);

        private final Function<Position, Position> moveFunction;

        Direction(Function<Position, Position> moveFunction) {
            this.moveFunction = moveFunction;
        }

        public Position move(Position from) {
            return moveFunction.apply(from);
        }
    }

    public record Blizzard(Position position, Direction direction) {
        public Blizzard move(int maxX, int maxY) {
            Position to = direction.move(position);
            if (direction == Direction.NORTH && to.y == 0) {
                to = new Position(to.x, maxY);
            }
            if (direction == Direction.WEST && to.x == 0) {
                to = new Position(maxX, to.y);
            }
            if (direction == Direction.SOUTH && to.y == maxY + 1) {
                to = new Position(to.x, 1);
            }
            if (direction == Direction.EAST && to.x == maxX + 1) {
                to = new Position(1, to.y);
            }
            return new Blizzard(to, direction);
        }
    }


    public List<Blizzard> readBlizzards(List<String> lines) {
        List<Blizzard> blizzards = new ArrayList<>();
        int maxY = lines.size() - 2;
        int maxX = lines.get(0).length() - 2;
        for (int y = 1; y <= maxY; y++) {
            String line = lines.get(y);
            for (int x = 1; x <= maxX; x++) {
                Direction direction = switch (line.charAt(x)) {
                    case '>' -> Direction.EAST;
                    case '<' -> Direction.WEST;
                    case '^' -> Direction.NORTH;
                    case 'v' -> Direction.SOUTH;
                    default -> null;
                };
                if (direction != null) {
                    Position pos = new Position(x, y);
                    blizzards.add(new Blizzard(pos, direction));
                }
            }
        }
        return blizzards;
    }

    record Grid(int maxX, int maxY, Position start, Position end, Set<Position> positions) {

        public boolean reachable(Position p) {
            if (p.equals(start) || p.equals(end)) {
                return true;
            }
            if (positions.contains(p)) {
                return false;
            }
            return p.x > 0 && p.x <= maxX && p.y > 0 && p.y <= maxY;
        }

        void add(Position p) {
            this.positions.add(p);
        }
    }


    public void partOne(String kind, Path path) {
        List<String> lines = list(path);
        final int maxY = lines.size() - 2;
        final int maxX = lines.get(0).length() - 2;

        List<Blizzard> blizzards = readBlizzards(lines);

        long result = solve(maxY, maxX, blizzards);
        System.out.println(kind+" part One : "+result);
    }

    private long solve(int maxY, int maxX, List<Blizzard> blizzards) {
        Set<Position> states = new HashSet<>();
        Position start = new Position(1, 0);
        states.add(new Position(1, 0));
        Position destination = new Position(maxX, maxY + 1);
        boolean realEnd = false;
        for (long i = 1; true; i++) {
            Grid grid = new Grid(maxX, maxY, start, destination, new HashSet<>());
            blizzards = blizzards.stream()
                    .map(b -> b.move(maxX, maxY))
                    .toList();
            blizzards.forEach(b -> grid.add(b.position));
            states = states.stream()
                    .flatMap(s -> Arrays.stream(Direction.values()).map(d -> d.move(s)).filter(grid::reachable))
                    .collect(Collectors.toSet());
            if (states.contains(destination)) {
                return i;
            }
        }
    }


    public void partTwo(String kind, Path path) {


    }

    @Override
    public void run() {
        partOne("Test", getTestInputPath());
        partOne("Real", getInputPath());

        //partTwo("Test", ); // 19
        //partTwo("Real", ); // 893

    }

}
