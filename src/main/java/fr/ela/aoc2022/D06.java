package fr.ela.aoc2022;


import java.util.Arrays;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class D06 extends AoC {


    public int getIndex(String line, int length) {
        int[] start = new int[length];
        IntStream.range(0, length).forEach(i -> start[i] = line.charAt(i));
        CharsWindow window = new CharsWindow(start);

        int value = line.chars().skip(length).dropWhile(window).findFirst().orElse(-1);
        return window.index;
    }


    public class CharsWindow implements IntPredicate {
        private int index = 0;
        private final int size;
        private int[] buffer;

        public CharsWindow(int[] start) {
            buffer = start;
            size = start.length;
            index = start.length;
        }

        @Override
        public boolean test(int c) {
            int pos = index % size;
            buffer[pos] = c;
            index++;
            return (Arrays.stream(buffer).distinct().count() < size);
        }
    }


    @Override
    public void run() {
        stream(getTestInputPath()).forEach(
                s -> System.out.println("Test result 1 : "+getIndex(s, 4)));

        stream(getInputPath()).forEach(
                s -> System.out.println("Real result 1 : "+getIndex(s, 4)));
        stream(getTestInputPath()).forEach(
                s -> System.out.println("Test result 2 : "+getIndex(s, 14)));

        stream(getInputPath()).forEach(
                s -> System.out.println("Real result 3 : "+getIndex(s, 14)));

    }
}
