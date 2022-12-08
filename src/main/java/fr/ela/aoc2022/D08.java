package fr.ela.aoc2022;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class D08 extends AoC {

    record Tree(int x, int y, int height) {
    }


    public Set<Tree> getVisibleTrees(Tree[] line) {
        Accumulator increasing = new Accumulator();
        Accumulator decreasing = new Accumulator();
        for (int i = 0; i < line.length; i++) {
            increasing.accept(line[i]);
            decreasing.accept(line[line.length - 1 - i]);
        }
        increasing.trees.addAll(decreasing.trees);
        return increasing.trees;
    }

    public class Accumulator implements Consumer<Tree> {
        private Set<Tree> trees = new HashSet<>();
        int max = -1;

        @Override
        public void accept(Tree value) {
            if (value.height > max) {
                max = value.height;
                trees.add(value);
            }
        }

    }

    private List<Tree[]> readInput(List<String> lines) {
        int size = lines.size();
        Tree[][] treeLines = new Tree[2*size][size];
        for (int i = 0; i < 2*size; i++) {
            treeLines[i] = new Tree[size];
        }
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int j = 0; j < lines.size(); j++) {
                Tree t = new Tree(i, j, line.charAt(j));
                treeLines[i][j] = t;
                treeLines[size+j][i] = t;
            }
        }
        return Arrays.asList(treeLines);
    }

    public long getVisibleTrees(List<String> lines) {
        return readInput(lines).stream().map(this::getVisibleTrees).flatMap(Set::stream).distinct().count();
    }

    @Override
    public void run() {

        System.out.println("Test part one : "+getVisibleTrees(list(getTestInputPath())));
        System.out.println("Real part one : "+getVisibleTrees(list(getInputPath())));
        System.out.println("Test part two : ");
        System.out.println("Real part two : ");

    }
}
