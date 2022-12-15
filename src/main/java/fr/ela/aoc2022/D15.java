package fr.ela.aoc2022;


import java.math.BigInteger;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class D15 extends AoC {

    record Position(int x, int y) {

        int distance(Position other) {
            return Math.abs(x - other.x) + Math.abs(y - other.y);
        }

        //x coordinate by 4000000 and then adding its y coordinate.
        public String tunningFrequency() {
            return BigInteger.valueOf(x).multiply(BigInteger.valueOf(4000000L)).add(BigInteger.valueOf(y)).toString();
        }
    }

    record Range(int left, int right) {
        int size() {
            return right - left;
        }
    }

    public List<Range> mergeRanges(List<Range> ranges) {
        if (ranges.isEmpty()) {
            return ranges;
        }
        ranges.sort(Comparator.comparingInt(Range::left));

        List<Range> result = new ArrayList<>();
        Range current = ranges.get(0);

        for (int i = 1; i < ranges.size(); i++) {
            Range range = ranges.get(i);
            if (range.left <= current.right + 1) {
                current = new Range(current.left, Math.max(range.right, current.right));
            } else {
                result.add(current);
                current = range;
            }
        }
        result.add(current);
        return result;
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

        Range getScannedRangeAtLine(int y) {
            Position closestOnLine = new Position(pos.x, y);
            int distance = range - pos.distance(closestOnLine);
            // La ligne est trop loin
            if (distance < 0) {
                return null;
            } else {
                return new Range(pos.x - distance, pos.x + distance);
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
            return getRangesInSightOfBeacons(y)
                    .stream().mapToInt(Range::size)
                    .sum();
        }

        public List<Range> getRangesInSightOfBeacons(int y) {
            List<Range> list = new ArrayList<>(sensors.stream().map(sensor -> sensor.getScannedRangeAtLine(y)).filter(Objects::nonNull).toList());
            return mergeRanges(list);
        }


        public Position findBeacon(int min, int max) {
            for (int y = min; y < max; y++) {
                List<Range> ranges = getRangesInSightOfBeacons(y);
                if (ranges.size() != 1) {
                    // We have a hole here!
                    return new Position(ranges.get(0).right+1, y);
                }
            }
            return null;
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
        System.out.println("Test part two : " + testGrid.findBeacon(0, 20).tunningFrequency());

        Grid grid = readInput(getInputPath());
        System.out.println("Real part one : " + grid.countInRangePositions(2000000));
        System.out.println("Test part two : " + grid.findBeacon(0, 4000000).tunningFrequency());
    }
}
