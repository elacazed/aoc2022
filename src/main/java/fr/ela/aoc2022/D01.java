package fr.ela.aoc2022;

import java.util.Comparator;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class D01 extends AoC {

    Function<String, Integer> mapper = s -> s.isEmpty() ? 0 : Integer.parseInt(s);

    BiConsumer<Stack<Integer>, Integer> accumulator = (stack, integer) -> {
        int total = integer;
        if (integer != 0 && !stack.isEmpty()) {
            total += stack.pop();
        }
        stack.push(total);
    };

    private int findTopNElves(Stream<Integer> lines, int number) {
        return lines.collect(Stack::new, accumulator, Stack::addAll)
                .stream().sorted(Comparator.reverseOrder()).limit(number).mapToInt(Integer::intValue).sum();
    }
    @Override
    public void run() {
        System.out.println("Most Carrying Elf : " + findTopNElves(stream(getTestInputPath(), mapper), 1));
        System.out.println("Most Carrying Elf : " + findTopNElves(stream(getInputPath(), mapper), 1));
        System.out.println("Top 3 elves : " + findTopNElves(stream(getTestInputPath(), mapper), 3));
        System.out.println("Top 3 elves : " + findTopNElves(stream(getInputPath(), mapper), 3));
    }



}
