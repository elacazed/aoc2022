package fr.ela.aoc2022;


import java.util.LinkedList;
import java.util.function.IntPredicate;

public class D06 extends AoC {


    public int getIndex(String line, int length) {
        char[] start = new char[length];
        line.getChars(0, length, start, 0);
        CharsWindow window = new CharsWindow(start);
        int value = line.chars().skip(length).dropWhile(window).findFirst().orElse(-1);
        return window.index;
    }


    public class CharsWindow implements IntPredicate {
        private int index = 0;
        private final int size;
        private LinkedList<Integer> buffer = new LinkedList<>();

        public CharsWindow(char[] start) {
            for (char c : start) {
                buffer.add((int) c);
            }
            size = start.length;
            index = start.length;
        }

        @Override
        public boolean test(int c) {
            index++;
            buffer.removeFirst();
            buffer.addLast(c);
            return (buffer.stream().distinct().count() < size);
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
