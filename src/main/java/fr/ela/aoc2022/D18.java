package fr.ela.aoc2022;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class D18 extends AoC {

    public record Pos2D(int x, int y) {
    }

    public record Pos3d(int x, int y, int z) {
    }

    Pos3d readLine(String s) {
        String[] coords = s.split(",");
        return new Pos3d(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
    }

    public class ProjectedSpace {

        Set<Pos3d> positions = new HashSet<>();
        Map<Pos2D, List<Integer>> px = new HashMap<>();
        Map<Pos2D, List<Integer>> py = new HashMap<>();
        Map<Pos2D, List<Integer>> pz = new HashMap();

        public void add(Pos3d pos) {
            positions.add(pos);
            px.computeIfAbsent(new Pos2D(pos.y, pos.z), i -> new LinkedList<>()).add(pos.x);
            py.computeIfAbsent(new Pos2D(pos.x, pos.z), i -> new LinkedList<>()).add(pos.y);
            pz.computeIfAbsent(new Pos2D(pos.x, pos.y), i -> new LinkedList<>()).add(pos.z);
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
    }

    public void partOne(String kind, Path path) {
        ProjectedSpace space = new ProjectedSpace();
        stream(path, this::readLine).forEach(space::add);
        System.out.println(kind + " part 1 result : " + space.countVisible());
    }

    @Override
    public void run() {
        partOne("Test", getTestInputPath());


        partOne("Real", getInputPath());
    }
}
