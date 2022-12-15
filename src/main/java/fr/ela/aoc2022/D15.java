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
        if (ranges == null || ranges.isEmpty() || ranges.size() == 1) {
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

    public record Sensor(Position pos, int range) {
    }


    public class Grid {
        Map<Integer, List<Range>> ranges = new HashMap<>();

        public void add(Sensor sensor) {
            for (int y = sensor.pos.y - sensor.range; y < sensor.pos.y + sensor.range; y++) {
                ranges.computeIfAbsent(y, i -> new ArrayList<>()).add(getScannedRangeAtLine(sensor.pos, sensor.range, y));
            }
        }

        Range getScannedRangeAtLine(Position pos, int range, int y) {
            Position closestOnLine = new Position(pos.x, y);
            int distance = range - pos.distance(closestOnLine);
            // La ligne est trop loin
            if (distance < 0) {
                return null;
            } else {
                return new Range(pos.x - distance, pos.x + distance);
            }
        }

        public long countInRangePositions(int y) {
            return mergeRanges(ranges.get(y))
                    .stream().mapToInt(Range::size)
                    .sum();
        }

        public Position findBeacon(int min, int max) {
            for (int y = max-1; y >= min; y--) {
                List<Range> ranges = mergeRanges(this.ranges.get(y));
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
            Position s = new Position(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
            Position b = new Position(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)));
            return new Sensor(s, s.distance(b));
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
