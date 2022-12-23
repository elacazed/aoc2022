package fr.ela.aoc2022;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class D23 extends AoC {

    public record Position(int x, int y) {
        List<Position> adjacent() {
            return List.of(new Position(x - 1, y - 1), new Position(x - 1, y), new Position(x - 1, y + 1),
                    new Position(x, y - 1), new Position(x, y + 1),
                    new Position(x + 1, y - 1), new Position(x + 1, y), new Position(x + 1, y + 1));
        }
    }

    public enum Direction {
        NORTH(p -> new Position(p.x, p.y - 1),
                p -> List.of(new Position(p.x - 1, p.y - 1), new Position(p.x, p.y - 1), new Position(p.x + 1, p.y - 1))),
        SOUTH(p -> new Position(p.x, p.y + 1),
                p -> List.of(new Position(p.x - 1, p.y + 1), new Position(p.x, p.y + 1), new Position(p.x + 1, p.y + 1))),
        WEST(p -> new Position(p.x - 1, p.y),
                p -> List.of(new Position(p.x - 1, p.y), new Position(p.x - 1, p.y - 1), new Position(p.x - 1, p.y + 1))),
        EAST(p -> new Position(p.x + 1, p.y),
                p -> List.of(new Position(p.x + 1, p.y), new Position(p.x + 1, p.y - 1), new Position(p.x + 1, p.y + 1)));

        public final Function<Position, List<Position>> positionsToConsider;
        public final Function<Position, Position> proposition;

        Direction(Function<Position, Position> proposition, Function<Position, List<Position>> positionsToConsider) {
            this.proposition = proposition;
            this.positionsToConsider = positionsToConsider;
        }

        Position propose(Position from) {
            return proposition.apply(from);
        }
    }


    public class Grid {

        LinkedList<Direction> directions;

        Set<Position> positions = new HashSet<>();

        public Grid() {
            directions = new LinkedList<>();
            directions.addAll(List.of(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST));
        }

        public void changeDirection() {
            Direction first = directions.removeFirst();
            directions.addLast(first);
        }

        public Optional<Position> propose(Position from) {
            Set<Position> positions = from.adjacent().stream().filter(this::free).collect(Collectors.toSet());
            if (positions.size() == 8) {
                return Optional.empty();
            }
            return directions.stream().filter(d -> positions.containsAll(d.positionsToConsider.apply(from)))
                    .map(d -> d.propose(from)).findFirst();

        }

        boolean free(Position p) {
            return !positions.contains(p);
        }

        public int round() {
            //Map destination -> list<Position de provenance>
            Map<Position, List<Position>> propositions = new HashMap<>();
            for (Position position : positions) {
                propose(position)
                        .ifPresent(p -> propositions.computeIfAbsent(p, x -> new ArrayList<>()).add(position));
            }
            int movingElves = 0;
            for (Map.Entry<Position, List<Position>> proposition : propositions.entrySet()) {
                if (proposition.getValue().size() == 1) {
                    Position from = proposition.getValue().get(0);
                    Position to = proposition.getKey();
                    positions.remove(from);
                    positions.add(to);
                    movingElves++;
                }
            }
            changeDirection();
            return movingElves;
        }

        public int rectangleSize() {
            IntSummaryStatistics xStats = positions.stream().mapToInt(p -> p.x).summaryStatistics();
            IntSummaryStatistics yStats = positions.stream().mapToInt(p -> p.y).summaryStatistics();
            return (xStats.getMax() - xStats.getMin() + 1) * (yStats.getMax() - yStats.getMin() + 1);
        }

        public String toString() {
            IntSummaryStatistics xStats = positions.stream().mapToInt(p -> p.x).summaryStatistics();
            IntSummaryStatistics yStats = positions.stream().mapToInt(p -> p.y).summaryStatistics();
            StringBuilder sb = new StringBuilder();
            int size = xStats.getMax() - xStats.getMin() + 1;
            for (int y = yStats.getMin(); y <= yStats.getMax(); y++) {
                char[] line = new char[size];
                for (int x = xStats.getMin(); x <= xStats.getMax(); x++) {
                    line[x - xStats.getMin()] = free(new Position(x, y)) ? '.' : '#';
                }
                sb.append(new String(line)).append("\n");
            }
            return sb.toString();
        }

        public void addElf(Position position) {
            positions.add(position);
        }
    }

    public Grid readInput(Path path) {
        Grid grid = new Grid();
        List<String> lines = list(path);
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '#') {
                    grid.addElf(new Position(x, y));
                }
            }
        }
        return grid;
    }

    public Grid partOne(String kind, Path path) {
        Grid grid = readInput(path);
        for (int i = 0; i < 10; i++) {
            grid.round();
        }
        System.out.println(grid);
        System.out.println(kind + " empty tiles in rectangle : " + (grid.rectangleSize() - grid.positions.size()));
        return grid;
    }

    public void partTwo(String kind, Grid grid) {
        int rounds = 10;
        while (grid.round() > 0) {
            rounds++;
        }
        System.out.println(grid);
        System.out.println(kind + " : elves stop moving at round " + (rounds + 1));


    }

    @Override
    public void run() {
        Grid testGrid = partOne("Test", getTestInputPath());// 110
        Grid grid = partOne("Real", getInputPath()); // 3815

        partTwo("Test", testGrid); // 19
        partTwo("Real", grid); // 893

    }

}
