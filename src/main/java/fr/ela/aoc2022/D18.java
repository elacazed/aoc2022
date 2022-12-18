package fr.ela.aoc2022;

import org.w3c.dom.css.Counter;

import java.nio.file.Path;
import java.util.*;
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

        public long countNeighborsIn(Set<Position> holes) {
            return adjacent().stream().filter(holes::contains).count();
        }
    }

    public record Hole(Set<Position> touching) {
        boolean contains(Position p) {
            return touching.contains(p);
        }
    }

    Position readLine(String s) {
        String[] coords = s.split(",");
        return new Position(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
    }

    /**
     * For part one : projects all positions in a Map<Position, List[position on projected dimension]> and count extremities in segments.
     */
    public class VisibleSidesCounter {

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

        public int countVisible(Space space) {
            Map<Position, List<Integer>> px = new HashMap<>();
            Map<Position, List<Integer>> py = new HashMap<>();
            Map<Position, List<Integer>> pz = new HashMap<>();
            space.positions.forEach(pos -> {
                px.computeIfAbsent(new Position(pos.y, pos.z, 0), i -> new LinkedList<>()).add(pos.x);
                py.computeIfAbsent(new Position(pos.x, pos.z, 0), i -> new LinkedList<>()).add(pos.y);
                pz.computeIfAbsent(new Position(pos.x, pos.y, 0), i -> new LinkedList<>()).add(pos.z);
            });
            return Stream.of(px, py, pz).mapToInt(m ->
                    m.values().stream().mapToInt(this::countVisible).sum()).sum();
        }
    }

    class TouchingSidesCounter {
        long count;

        public void add(long count) {
            this.count += count;
        }
    }

    public class HoleDetector {

        public long getInHolesPositions(Space space) {
            Set<Position> holes = new HashSet<>();
            for (int x = 0; x < space.maxX; x++) {
                for (int y = 0; y < space.maxY; y++) {
                    for (int z = 0; z < space.maxZ; z++) {
                        holes.add(new Position(x, y, z));
                    }
                }
            }
            Set<Position> visitables = fill(findStart(space), space, new TouchingSidesCounter());

            holes.removeAll(space.positions);
            holes.removeAll(visitables);
            int nbPositionsInHoles = holes.size();
            long touchingSidesInHoles = holes.stream().mapToLong(pos -> pos.countNeighborsIn(holes)).sum();

            return 6 * nbPositionsInHoles - touchingSidesInHoles;
        }

        public Set<Position> fill(Position start, Space space, TouchingSidesCounter counter) {
            Set<Position> filled = new HashSet<>();
            fill(start, filled, space, counter);
            return filled;
        }

        private Set<Position> fill(Position start, Set<Position> filled, Space space, TouchingSidesCounter counter) {
            Set<Position> newVisited = new HashSet<>();
            filled.add(start);
            for (Position next : start.adjacent()) {
                if (!newVisited.contains(next) && !filled.contains(next) && !space.positions.contains(next) && space.inGrid(next)) {
                    counter.add(1);
                    newVisited.addAll(fill(next, filled, space, counter));
                }
            }
            return newVisited;
        }


        private Position findStart(Space space) {
            int x = 0;
            int y = 0;
            int z = 0;
            Position start = new Position(x, y, z);
            while (space.positions.contains(start) && x <= space.maxX) {
                x = x + 1;
            }
            if (x == space.maxX) {
                throw new IllegalStateException();
            }
            return start;
        }
    }

    public class Space {

        final int maxX, maxY, maxZ, minX, minY, minZ;

        Set<Position> positions = new HashSet<>();

        public Space(List<Position> list) {
            int mx = 0, my = 0, mz = 0, mX = 0, mY = 0, mZ = 0;
            for (Position pos : list) {
                positions.add(pos);
                mX = Math.max(mX, pos.x);
                mY = Math.max(mY, pos.y);
                mZ = Math.max(mZ, pos.z);
                mx = Math.min(mx, pos.x);
                my = Math.min(my, pos.y);
                mz = Math.min(mz, pos.z);
            }
            maxX = mX;
            maxZ = mZ;
            maxY = mY;
            minX = mx;
            minY = my;
            minZ = mz;
        }

        public boolean inGrid(Position pos) {
            return inRange(pos.x, minX, maxX + 1) && inRange(pos.y, minY, maxY + 1) && inRange(pos.z, minZ, maxZ + 1);
        }

    }

    public int partOne(String kind, Space space) {
        VisibleSidesCounter counter = new VisibleSidesCounter();
        int count = counter.countVisible(space);
        System.out.println(kind + " part 1 result : " + counter.countVisible(space));
        return count;
    }

    public void partTwo(String kind, Space space, int visibleSidesFromPartOne) {
        HoleDetector holeDetector = new HoleDetector();
        long facesVisibleInHoles = holeDetector.getInHolesPositions(space);
        long result = visibleSidesFromPartOne - facesVisibleInHoles;
        System.out.println(kind + " part 2 result : " + result);
    }

    @Override
    public void run() {
        Space test = new Space(list(getTestInputPath(), this::readLine));
        int visibleInTest = partOne("Test", test);
        partTwo("Test", test, visibleInTest);

        Space space = new Space(list(getInputPath(), this::readLine));
        int visible = partOne("Real", space);
        partTwo("Real", space, visible);
    }
}
