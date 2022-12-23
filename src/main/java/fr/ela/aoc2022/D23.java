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

    /**
     * During the first half of each round, each Elf considers the eight positions adjacent to themself.
     * If no other Elves are in one of those eight positions, the Elf does not do anything during this round.
     * Otherwise, the Elf looks in each of four directions in the following order and proposes moving one step in the first valid direction:
     * <p>
     * If there is no Elf in the N, NE, or NW adjacent positions, the Elf proposes moving north one step.
     * If there is no Elf in the S, SE, or SW adjacent positions, the Elf proposes moving south one step.
     * If there is no Elf in the W, NW, or SW adjacent positions, the Elf proposes moving west one step.
     * If there is no Elf in the E, NE, or SE adjacent positions, the Elf proposes moving east one step.
     * <p>
     * After each Elf has had a chance to propose a move, the second half of the round can begin.
     * Simultaneously, each Elf moves to their proposed destination tile if they were the only Elf to propose moving to that position.
     * If two or more Elves propose moving to the same position, none of those Elves move.
     * <p>
     * Finally, at the end of the round, the first direction the Elves considered is moved to the end of the list of directions.
     * For example, during the second round, the Elves would try proposing a move to the south first, then west, then east, then north. On the third round, the Elves would first consider west, then east, then north, then south.
     */

    public class Elf {
        private final LinkedList<Direction> directions = new LinkedList<>();

        public Elf() {
            directions.addAll(List.of(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST));
        }

        public Optional<Position> propose(Grid grid, Position from) {
            Set<Position> positions = from.adjacent().stream().filter(grid::free).collect(Collectors.toSet());
            if (positions.size() == 8) {
                return Optional.empty();
            }
            return directions.stream().filter(d -> positions.containsAll(d.positionsToConsider.apply(from)))
                    .map(d -> d.propose(from)).findFirst();

        }


        public void changeDirection() {
            Direction first = directions.removeFirst();
            directions.addLast(first);
        }
    }

    public class Grid {

        Map<Position, Elf> positions = new HashMap<>();

        boolean free(Position p) {
            return !positions.containsKey(p);
        }

        public int round() {
            //Map destination -> list<Position de provenance>
            Map<Position, List<Position>> propositions = new HashMap<>();
            for (Map.Entry<Position, Elf> e : positions.entrySet()) {
                e.getValue().propose(this, e.getKey())
                        .ifPresent(p -> propositions.computeIfAbsent(p, x -> new ArrayList<>()).add(e.getKey()));
            }
            int movingElves = 0;
            for (Map.Entry<Position, List<Position>> proposition : propositions.entrySet()) {
                if (proposition.getValue().size() == 1) {
                    Position from = proposition.getValue().get(0);
                    Position to = proposition.getKey();
                    Elf elf = positions.remove(from);
                    positions.put(to, elf);
                    movingElves++;
                }
            }
            positions.values().forEach(Elf::changeDirection);
            return movingElves;
        }

        public int rectangleSize() {
            IntSummaryStatistics xStats = positions.keySet().stream().mapToInt(p -> p.x).summaryStatistics();
            IntSummaryStatistics yStats = positions.keySet().stream().mapToInt(p -> p.y).summaryStatistics();
            return (xStats.getMax() - xStats.getMin() + 1) * (yStats.getMax() - yStats.getMin() + 1);
        }

        public String toString() {
            IntSummaryStatistics xStats = positions.keySet().stream().mapToInt(p -> p.x).summaryStatistics();
            IntSummaryStatistics yStats = positions.keySet().stream().mapToInt(p -> p.y).summaryStatistics();
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

        public void addElf(Elf elf, Position position) {
            positions.put(position, elf);
        }
    }

    public Grid readInput(Path path) {
        Grid grid = new Grid();
        List<String> lines = list(path);
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '#') {
                    grid.addElf(new Elf(), new Position(x, y));
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
        System.out.println(kind + " : elves stop moving at round " + (rounds+1));


    }

    @Override
    public void run() {
        Grid testGrid = partOne("Test", getTestInputPath());// 25
        Grid grid = partOne("Real", getInputPath());

        partTwo("Test", testGrid);
        partTwo("Real", grid);

    }

}
