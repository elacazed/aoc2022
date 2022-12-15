package fr.ela.aoc2022;


import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class D15 extends AoC {

    record Position(int x, int y) {

        int distance(Position other) {
            return Math.abs(x - other.x) + Math.abs(y - other.y);
        }
    }

    record Range(int left, int right) {

    }

    public class Sensor {
        private final Position pos;
        private final Position beacon;

        private final int range;

        public Sensor(Position pos, Position beacon) {
            this.pos = pos;
            this.beacon = beacon;
            range = pos.distance(beacon);
        }

        boolean outOfRange(Position p) {
            return pos.distance(p) > range;
        }

        Set<Position> getInRangePosition(int y) {
            Position closestOnLine = new Position(pos.x, y);
            int distance = range - pos.distance(closestOnLine);
            // La ligne est trop loin
            if (distance < 0) {
                return Set.of();
            } else {
                return IntStream.range(pos.x - distance, pos.x + distance +1)
                        .mapToObj(x -> new Position(x, y))
                        .collect(Collectors.toSet());
            }
        }

    }


    public class Grid {
        Set<Sensor> sensors = new HashSet<>();
        Set<Position> positions = new HashSet<>();

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        public void add(Sensor sensor) {
            sensors.add(sensor);
            positions.add(sensor.beacon);
            positions.add(sensor.pos);
            minX = Math.min(Math.min(sensor.beacon.x, sensor.pos.x), minX);
            minY = Math.min(Math.min(sensor.beacon.y, sensor.pos.y), minY);
            maxX = Math.max(Math.max(sensor.beacon.x, sensor.pos.x), maxX);
            maxY = Math.max(Math.max(sensor.beacon.y, sensor.pos.y), maxY);
        }

        public boolean isOutOfRange(Position pos) {
            return sensors.stream().allMatch(b -> b.outOfRange(pos));
        }

        public boolean isInRange(Position pos) {
            return sensors.stream().anyMatch(b -> !b.outOfRange(pos));
        }

        public long countInRangePositions(int y) {
            Set<Position> set = sensors.stream().map(sensor -> sensor.getInRangePosition(y))
                    .flatMap(Set::stream)
                    .collect(Collectors.toCollection(HashSet::new));
            set.removeAll(positions);
            return set.size();
        }

    }

    private static final Pattern pattern = Pattern.compile("Sensor at x=(-?[0-9]+), y=(-?[0-9]+): closest beacon is at x=(-?[0-9]+), y=(-?[0-9]+)");

    public Sensor parse(String line) {
        Matcher m = pattern.matcher(line);
        if (m.matches()) {
            return new Sensor(new Position(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))),
                    new Position(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))));
        } else {
            throw new IllegalArgumentException(line);
        }
    }

    public Grid readInput(Path path) {
        Grid grid = new Grid();
        stream(path, this::parse).forEach(grid::add);
        return grid;
    }

    //Sensor at x=2, y=18: closest beacon is at x=-2, y=15
    @Override
    public void run() {
        Grid testGrid = readInput(getTestInputPath());
        System.out.println("Test part one : " + testGrid.countInRangePositions(10));

        Grid grid = readInput(getInputPath());
        System.out.println("Real part one : " + grid.countInRangePositions(2000000));
    }
}
