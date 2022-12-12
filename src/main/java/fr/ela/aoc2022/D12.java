package fr.ela.aoc2022;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class D12 extends AoC {

    record Position(int x, int y) {

        Stream<Position> neighbors() {
            return Stream.of(new Position(x - 1, y), new Position(x + 1, y), new Position(x, y - 1), new Position(x, y + 1));
        }
    }

    public class Grid {

        private final int hSize;
        private final int vSize;
        public Position arrival;
        public Position start;
        Map<Position, Integer> map = new HashMap<>();


        public Grid(List<String> lines) {
            this.vSize = lines.size();
            this.hSize = lines.get(0).length();
            for (int y = 0; y < vSize; y++) {
                String line = lines.get(y);
                for (int x = 0; x < hSize; x++) {
                    Position p = new Position(x, y);
                    char height = line.charAt(x);
                    if (height == 'S') {
                        start = p;
                        height = 'a';
                    }
                    if (height == 'E') {
                        arrival = p;
                        height = 'z';
                    }
                    put(p, height - 'a');
                }
            }
        }

        boolean isIn(Position p) {
            return map.containsKey(p);
        }

        int getHeight(Position p) {
            return map.getOrDefault(p, -1);
        }

        public void put(Position p, int i) {
            map.put(p, i);
        }

        public int findShortestPath(Position start, Position end) {
            Map<Position, Integer> pathLengths = new HashMap<>();
            Map<Position, Position> exploredFrom = new HashMap<>();
            LinkedList<Position> queue = new LinkedList<>();
            pathLengths.put(start, 0);
            queue.add(start);
            while (queue.size() > 0) {
                Position pos = queue.poll();
                if (pos.equals(end)) {
                    ArrayList<Position> path = new ArrayList<>();
                    while (exploredFrom.containsKey(pos)) {
                        path.add(pos);
                        pos = exploredFrom.get(pos);
                    }
                    return path.size();
                }
                int curHeight = getHeight(pos);
                for (Position next : pos.neighbors().filter(this::isIn).toList()) {
                    if (getHeight(next) > curHeight + 1) {
                        continue;
                    }
                    int pathLength = pathLengths.get(pos) + 1;
                    if (pathLength < pathLengths.getOrDefault(next, Integer.MAX_VALUE)) {
                        pathLengths.put(next, pathLength);
                        exploredFrom.put(next, pos);
                        queue.add(next);
                    }
                }
            }
            return Integer.MAX_VALUE;
        }

        public int findShortestPathFromAltitude(int alt, Position end) {
            return map.entrySet().stream()
                    .filter(e -> e.getValue().equals(alt))
                    .mapToInt(e -> findShortestPath(e.getKey(), end))
                    .min()
                    .orElseThrow();
        }

    }

    @Override
    public void run() {
        Grid testGrid = new Grid(list(getTestInputPath()));
        Grid realGrid = new Grid(list(getInputPath()));
        System.out.println("Test part one : " + testGrid.findShortestPath(testGrid.start, testGrid.arrival));
        System.out.println("Real part one : " + realGrid.findShortestPath(realGrid.start, realGrid.arrival));

        System.out.println("Test part two : "+ testGrid.findShortestPathFromAltitude(0, testGrid.arrival));
        System.out.println("Real part one : "+ realGrid.findShortestPathFromAltitude(0, realGrid.arrival));

    }
}
