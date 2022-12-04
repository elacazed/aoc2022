package fr.ela.aoc2022;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class D04 extends AoC {

    Pattern pattern = Pattern.compile("([0-9]+)-([0-9]+),([0-9]+)-([0-9]+)");

    record Range(int start, int end) {
        public boolean contains(int value) {
            return value >= start && value <= end;
        }

        public boolean contains(Range other) {
            return contains(other.start) && contains(other.end);
        }

        public boolean overlap(Range other) {
            return contains(other.start) || contains(other.end);
        }
    }

    record RangePair(Range one, Range other) {
        boolean oneContainsOther() {
            return one.contains(other);
        }

        boolean oneOverlapOther() {
            return one.overlap(other);
        }
    }

    RangePair readLine(String s) {
        Matcher m = pattern.matcher(s);
        m.matches();
        Range first = new Range(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
        Range second = new Range(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)));
        // Trier les ranges permet d'éviter de faire 2 vérifications d'inclusion/recouvrement.
        if (first.start > second.start) {
            return new RangePair(second, first);
        } else {
            return new RangePair(first, second);
        }
    }


    @Override
    public void run() {
        System.out.println("Test Score Part 1 : " + stream(getTestInputPath(), this::readLine).filter(RangePair::oneContainsOther).count());
        System.out.println("Real Score Part 1 : " + stream(getInputPath(), this::readLine).filter(RangePair::oneContainsOther).count());

        System.out.println("Test Score Part 2 : " + stream(getTestInputPath(), this::readLine).filter(RangePair::oneOverlapOther).count());
        System.out.println("Real Score Part 2 : " + stream(getInputPath(), this::readLine).filter(RangePair::oneOverlapOther).count());

    }


}
