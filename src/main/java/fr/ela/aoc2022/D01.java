package fr.ela.aoc2022;

import java.util.Arrays;
import java.util.List;

public class D01 extends AoC {
    @Override
    public void run() {
        System.out.println("Most Carrying Elf : " + findTopNElves(list(getTestInputPath()),1));
        System.out.println("Most Carrying Elf : " + findTopNElves(list(getInputPath()), 1));
        System.out.println("Top 3 elves : " + findTopNElves(list(getTestInputPath()),3));
        System.out.println("Top 3 elves : " + findTopNElves(list(getInputPath()), 3));
    }

    private int findTopNElves(List<String> lines, int number) {
        int[] values = new int[number];
        Arrays.fill(values, 0);
        int[] buffer = new int[number+1];
        Arrays.fill(buffer, 0);
        int current = 0;
        for (String line : lines) {
            if (line.isEmpty()) {
                addValueInTopN(number, values, buffer, current);
                current = 0;
            } else {
                current += Integer.parseInt(line);
            }
        }
        addValueInTopN(3, values, buffer, current);
        return Arrays.stream(values).sum();
    }

    private void addValueInTopN(int number, int[] values, int[] buffer, int current) {
        if (current > values[0]) {
            System.arraycopy(values, 0, buffer, 0, number);
            buffer[number] = current;
            Arrays.sort(buffer);
            System.arraycopy(buffer, 1, values, 0, number);
        }
    }
}
