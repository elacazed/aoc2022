package fr.ela.aoc2022;


import java.util.LinkedList;
import java.util.function.IntPredicate;

public class D06 extends AoC {


    public int getIndex(String line) {
        char[] start = new char[4];
        line.getChars(0, 4, start, 0);
        CharsWindow window = new CharsWindow(start);
        int value = line.chars().skip(4).dropWhile(window).findFirst().orElse(-1);
        return window.index;
    }


    public class CharsWindow implements IntPredicate {
        private int index = 0;
        private LinkedList<Integer> buffer = new LinkedList<>();

        public CharsWindow(char[] start) {
            for (char c : start) {
                buffer.add((int) c);
            }
            index = 4;
        }

        @Override
        public boolean test(int c) {
            index++;
            buffer.removeFirst();
            buffer.addLast(c);
            return (buffer.stream().distinct().count() < 4);
        }
    }


    @Override
    public void run() {
        stream(getTestInputPath()).forEach(
                s -> System.out.println("Test result : "+getIndex(s)));

        stream(getInputPath()).forEach(
                s -> System.out.println("Real result : "+getIndex(s)));

    }
}
