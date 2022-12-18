package fr.ela.aoc2022;

import java.nio.file.Path;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class D18 extends AoC {

    public record Position(int x, int y, int z) {

        List<Position> adjacent() {
            return List.of(
                    new Position(x - 1, y, z),
                    new Position(x + 1, y, z),
                    new Position(x, y - 1, z),
                    new Position(x, y + 1, z),
                    new Position(x, y, z - 1),
                    new Position(x, y, z + 1)
            );
        }

    }

    public record Droplet(Set<Position> touching) {
        boolean contains(Position p) {
            return touching.contains(p);
        }
    }

    Position readLine(String s) {
        String[] coords = s.split(",");
        return new Position(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
    }

    public class ProjectedSpace {

        int maxX;
        int maxY;
        int maxZ;
        int minX;
        int minY;
        int minZ;

        Set<Position> positions = new HashSet<>();
        Map<Position, List<Integer>> px = new HashMap<>();
        Map<Position, List<Integer>> py = new HashMap<>();
        Map<Position, List<Integer>> pz = new HashMap();

        //List<Droplet> droplets = new ArrayList<>();

        public void add(Position pos) {
            positions.add(pos);
            px.computeIfAbsent(new Position(pos.y, pos.z, 0), i -> new LinkedList<>()).add(pos.x);
            py.computeIfAbsent(new Position(pos.x, pos.z, 0), i -> new LinkedList<>()).add(pos.y);
            pz.computeIfAbsent(new Position(pos.x, pos.y, 0), i -> new LinkedList<>()).add(pos.z);

            maxX = Math.max(maxX, pos.x);
            maxY = Math.max(maxY, pos.y);
            maxZ = Math.max(maxZ, pos.z);
            minX = Math.min(minX, pos.x);
            minY = Math.min(minY, pos.y);
            minZ = Math.min(minZ, pos.z);
        }

        static boolean isRange(int i, int min, int max) {
            return min <= i && i <= max;
        }

        public boolean inGrid(Position pos) {
            return inRange(pos.x, minX, maxX) && inRange(pos.y, minY, maxY) && inRange(pos.z, minZ, maxZ);
        }


        private int countVisible(List<Integer> list) {
            list.sort(Comparator.naturalOrder());
            if (list.isEmpty()) {
                return 0;
            }
            Iterator<Integer> it = list.iterator();
            int cur = it.next();
            int visible = 1;
            while (it.hasNext()) {
                int next = it.next();
                if (next - cur > 1) {
                    visible += 2;
                }
                cur = next;
            }
            visible += 1;
            return visible;
        }

        public int countVisible() {
            return Stream.of(px, py, pz).mapToInt(m ->
                    m.values().stream().mapToInt(this::countVisible).sum()).sum();
        }

        public Set<Position> getInHolesPositions() {
            Set<Position> allPositions = new HashSet<>();
            for (int x = 0; x < maxX; x++) {
                for (int y = 0; y < maxY; y++) {
                    for (int z = 0; z < maxZ; z++) {
                        allPositions.add(new Position(x, y, z));
                    }
                }
            }
            Set<Position> visitables = fill(findStart());

            allPositions.removeAll(positions);
            allPositions.removeAll(visitables);

            return allPositions;
        }

        public Set<Position> fill(Position start) {
            Set<Position> filled = new HashSet<>();
            fill(start, filled);
            return filled;
        }

        public Set<Position> fill(Position start, Set<Position> filled) {
            Set<Position> newVisited = new HashSet<>();
            filled.add(start);
            for (Position next : start.adjacent()) {
                if (! newVisited.contains(next) && ! filled.contains(next) && !positions.contains(next) && inGrid(next)) {
                    newVisited.addAll(fill(next, filled));
                }
            }
            return newVisited;
        }


        private Position findStart() {
            int x = 0;
            int y = 0;
            int z = 0;
            Position start = new Position(x, y, z);
            while (positions.contains(start) && x <= maxX) {
                x = x+1;
            }
            if (x == maxX) {
                throw new IllegalStateException();
            }
            return start;
        }
    }

    public void partOne(String kind, Path path) {
        ProjectedSpace space = new ProjectedSpace();
        stream(path, this::readLine).forEach(space::add);
        System.out.println(kind + " part 1 result : " + space.countVisible());
    }

    public void partTwo(String kind, Path path) {
        ProjectedSpace space = new ProjectedSpace();
        stream(path, this::readLine).forEach(space::add);
        System.out.println(kind + " part 2 result : " + space.getInHolesPositions());
    }

    @Override
    public void run() {
        partOne("Test", getTestInputPath());
        partTwo("Test", getTestInputPath());
        partOne("Real", getInputPath());
        partTwo("Test", getInputPath());
    }
}
