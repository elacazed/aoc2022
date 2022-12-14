package fr.ela.aoc2022;


import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class D14 extends AoC {


    public record Position(int x, int y) {
        public static Position parse(String element) {
            String[] coords = element.split(",");
            return new Position(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
        }
    }

    public class Grid {
        Set<Position> grid = new HashSet<>();

        private final int maxY;

        public Grid(Set<Position> positions) {
            grid.addAll(positions);
            maxY = grid.stream().mapToInt(p -> p.y).max().orElseThrow();
        }

        public boolean addSand() {
            int x = 500;
            int y = 0;
            while (y <= maxY) {
                if (!grid.contains(new Position(x, y + 1))) {
                    y++;
                } else if (!grid.contains(new Position(x - 1, y + 1))) {
                    y++;
                    x--;
                } else if (!grid.contains(new Position(x + 1, y + 1))) {
                    y++;
                    x++;
                } else {
                    grid.add(new Position(x, y));
                    return true;
                }
            }
            return false;
        }
    }

    //498,4 -> 498,6 -> 496,6
    //503,4 -> 502,4 -> 502,9 -> 494,9
    public Set<Position> parse(String line) {
        Set<Position> positions = new HashSet<>();
        String[] elements = line.split(" -> ");
        Position start = Position.parse(elements[0]);
        positions.add(start);
        for (int i = 1; i < elements.length; i++) {
            Position pos = Position.parse(elements[i]);
            if (pos.x == start.x) {
                for (int y = Math.min(pos.y, start.y); y < Math.max(pos.y, start.y); y++) {
                    positions.add(new Position(pos.x, y));
                }
            }
            if (pos.y == start.y) {
                for (int x = Math.min(pos.x, start.x); x < Math.max(pos.x, start.x); x++) {
                    positions.add(new Position(x, pos.y));
                }
            }
            positions.add(pos);
            start = pos;
        }
        return positions;
    }

    public int partOne(Grid grid) {
        boolean cont = false;
        int count = 0;
        do {
            cont = grid.addSand();
            if (cont) {
                count++;
            }
        } while (cont);
        return count;
    }

    @Override
    public void run() {
        Grid testGrid = new Grid(stream(getTestInputPath()).map(this::parse).flatMap(Set::stream).collect(Collectors.toSet()));
        System.out.println("Test part one : " + partOne(testGrid));
        Grid grid = new Grid(stream(getInputPath()).map(this::parse).flatMap(Set::stream).collect(Collectors.toSet()));
        System.out.println("Real part one : " + partOne(grid));
    }
}
