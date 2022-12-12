package fr.ela.aoc2022;


import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.LongUnaryOperator;

public class D11 extends AoC {


    public class Item {
        long worryLevel;

        public Item(int worryLevel) {
            this.worryLevel = worryLevel;
        }

        public void afterInspection(LongUnaryOperator afterInspection) {
            this.worryLevel = afterInspection.applyAsLong(worryLevel);
        }
    }


    public class Monkey {
        private final LongUnaryOperator worryLevelFunction;
        private final LinkedList<Item> items;
        private final int divisor;
        private final int ifTrue;
        private final int ifFalse;

        private long inspected = 0L;

        public Monkey(List<Item> items, LongUnaryOperator worryLevelFunction, int divisor, int ifTrue, int ifFalse) {
            this.worryLevelFunction = worryLevelFunction;
            this.divisor = divisor;
            this.ifTrue = ifTrue;
            this.ifFalse = ifFalse;
            this.items = new LinkedList<>(items);
        }

        public void inspect(Item item) {
            inspected++;
            item.worryLevel = worryLevelFunction.applyAsLong(item.worryLevel);
        }

        public long countInspected() {
            return inspected;
        }

        public void turn(Monkey[] monkeys, LongUnaryOperator afterInspection) {
            if (items.isEmpty()) {
                return;
            }
            while (!items.isEmpty()) {
                Item current = items.pollFirst();
                inspect(current);
                current.afterInspection(afterInspection);
                int dest = current.worryLevel % divisor == 0 ? ifTrue : ifFalse;
                monkeys[dest].receives(current);
            }
        }

        private void receives(Item current) {
            items.addLast(current);
        }

    }

    public void round(Monkey[] monkeys, LongUnaryOperator afterInspection) {
        for (Monkey monkey : monkeys) {
            monkey.turn(monkeys, afterInspection);
        }
    }

    public Monkey readMonkey(List<String> lines) {
        List<Item> items = Arrays.stream(lines.get(1).substring(18).split(",")).map(String::trim)
                .mapToInt(Integer::parseInt).mapToObj(Item::new).toList();
        LongUnaryOperator wf = i -> i;
        String[] op = lines.get(2).substring("  Operation: new = ".length()).split(" ");
        boolean sameOperands = op[0].equals("old") && op[2].equals("old");

        switch (op[1]) {
            case "*":
                wf = sameOperands ? i -> i * i : i -> i * Integer.parseInt(op[2]);
                break;
            case "+":
                wf = sameOperands ? i -> i + i : i -> i + Integer.parseInt(op[2]);
                break;
        }
        int dec = Integer.parseInt(lines.get(3).substring("  Test: divisible by ".length()));
        int ifTrue = Integer.parseInt(lines.get(4).substring("    If true: throw to monkey ".length()));
        int ifFalse = Integer.parseInt(lines.get(5).substring("    If false: throw to monkey ".length()));

        return new Monkey(items, wf, dec, ifTrue, ifFalse);
    }

    public Monkey[] readInput(Path path) {
        List<List<String>> monks = splitOnEmptyLines(path);
        return monks.stream().map(this::readMonkey).toList().toArray(new Monkey[monks.size()]);
    }

    public long getMonkeyScore(Monkey[] monkeys) {
        return Arrays.stream(monkeys).mapToLong(Monkey::countInspected).sorted().skip(monkeys.length - 2).reduce(1, (a, b) -> a * b);
    }

    private long partOne(Path path) {
        Monkey[] monkeys = readInput(path);
        for (int i = 0; i < 20; i++) {
            round(monkeys, wl -> wl / 3);
        }
        return getMonkeyScore(monkeys);
    }

    private long partTwo(Path path) {
        Monkey[] monkeys = readInput(path);
        long val = Arrays.stream(monkeys).mapToLong(m -> m.divisor).reduce(1, (a,b) -> a*b);
        for (int i = 0; i < 10000; i++) {
            round(monkeys, wl -> wl % val);
        }
        return getMonkeyScore(monkeys);
    }

    @Override
    public void run() {
        System.out.println("Test part one : " + partOne(getTestInputPath()));
        System.out.println("Real part one : " + partOne(getInputPath()));

        System.out.println("Test part two : " + partTwo(getTestInputPath()));
        System.out.println("Real part one : " + partTwo(getInputPath()));

    }
}
