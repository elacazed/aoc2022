package fr.ela.aoc2022;


import com.sun.source.tree.BreakTree;

import java.util.*;
import java.util.stream.Collectors;

public class D08 extends AoC {

    record Tree(int x, int y, int height) {
    }

    record Forest(int size, List<Tree> trees, List<Tree[]> horizontally, List<Tree[]> vertically) {

        Tree[] getHorizontalLine(Tree tree) {
            return horizontally.get(tree.y);
        }

        Tree[] getVerticalLine(Tree tree) {
            return vertically.get(tree.x);
        }

        public int getVisibleTrees() {
            Set<Tree> result = new HashSet<>();
            horizontally.forEach(line -> addVisibleTrees(line, result));
            vertically.forEach(line -> addVisibleTrees(line, result));
            return result.size();
        }

        private void addVisibleTrees(Tree[] line, Set<Tree> trees) {
            int max1 = -1;
            int max2 = -1;
            for (int i = 0; i < line.length; i++) {
                if (line[i].height > max1) {
                    trees.add(line[i]);
                    max1 = line[i].height;
                }
                if (line[size - 1 - i].height > max2) {
                    trees.add(line[size - 1 - i]);
                    max2 = line[size - 1 - i].height;
                }
            }
        }

        public long getHighestVisibilityScore() {
            return trees.stream().mapToInt(this::getVisibilityScore).max().orElseThrow();
        }


        public int getVisibilityScore(Tree tree) {
            return getVisibilityScore(tree.y, tree.height, getVerticalLine(tree)) * getVisibilityScore(tree.x, tree.height, getHorizontalLine(tree));
        }

        int getVisibilityScore(int pos, int height, Tree[] line) {
            if (pos == 0 || pos == size - 1) {
                return 0;
            }
            int left = 1;
            int right = 1;
            for (int i = pos + 1; i < size - 1; i++) {
                if (line[i].height >= height) {
                    break;
                }
                right++;
            }
            for (int i = pos - 1; i > 0; i--) {
                if (line[i].height >= height) {
                    break;
                }
                left++;
            }
            return left * right;
        }

    }

    private Forest readInput(List<String> lines) {
        int size = lines.size();
        List<Tree[]> horizontalLines = new ArrayList<>();
        List<Tree[]> verticalLines = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            horizontalLines.add(new Tree[size]);
            verticalLines.add(new Tree[size]);
        }
        List<Tree> set = new ArrayList<>();
        for (int y = 0; y < lines.size(); y++) {
            String hLine = lines.get(y);
            for (int x = 0; x < lines.size(); x++) {
                Tree t = new Tree(x, y, hLine.charAt(x) - '0');
                horizontalLines.get(y)[x] = t;
                verticalLines.get(x)[y] = t;
                set.add(t);
            }
        }
        return new Forest(size, set, horizontalLines, verticalLines);
    }


    @Override
    public void run() {
        Forest testForest = readInput(list(getTestInputPath()));
        Forest forest = readInput(list(getInputPath()));
        System.out.println("Test part one : " + testForest.getVisibleTrees());
        System.out.println("Real part one : " + forest.getVisibleTrees());
        System.out.println("Test part two : " + testForest.getHighestVisibilityScore());
        System.out.println("Real part two : " + forest.getHighestVisibilityScore());

    }
}
