package fr.ela.aoc2022;


import java.nio.file.Path;
import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;

public class D11 extends AoC {


    public class Item {
        static int ID = 0;
        final int id;
        int worryLevel;

        public Item(int worryLevel) {
            this.worryLevel = worryLevel;
            this.id = ID;
            ID++;
        }

        public void afterInspection() {
            worryLevel = worryLevel / 3;
        }

        public String toString() {
            return "{id:" + id + ", wl:" + worryLevel + "}";
        }
    }


    public class Monkey {
        private final int id;
        private final IntUnaryOperator worryLevelFunction;
        private final LinkedList<Item> items;
        private final IntPredicate decider;
        private final int ifTrue;
        private final int ifFalse;

        private List<Integer> inspected = new ArrayList<>();

        public Monkey(int id, List<Item> items, IntUnaryOperator worryLevelFunction, IntPredicate decider, int ifTrue, int ifFalse) {
            this.id = id;
            this.worryLevelFunction = worryLevelFunction;
            this.decider = decider;
            this.ifTrue = ifTrue;
            this.ifFalse = ifFalse;
            this.items = new LinkedList<>(items);
        }

        public String toString() {
            return "Monkey [" + id + "] : [" + items.stream().map(Objects::toString).collect(Collectors.joining(", ")) + "], passTo " + ifTrue + "," + ifFalse;
        }

        public void inspect(Item item) {
            inspected.add(item.id);
            item.worryLevel = worryLevelFunction.applyAsInt(item.worryLevel);
        }

        public int countInspected() {
            return inspected.size();
        }

        public void turn(Monkey[] monkeys) {
            if (items.isEmpty()) {
                return;
            }
            while (! items.isEmpty()) {
                Item current = items.pollFirst();
                inspect(current);
                current.afterInspection();
                if (decider.test(current.worryLevel)) {
                    monkeys[ifTrue].receives(current);
                } else {
                    monkeys[ifFalse].receives(current);
                }
            }
        }

        private void receives(Item current) {
            items.addLast(current);
        }

    }

    public void round(Monkey[] monkeys) {
        for (Monkey monkey : monkeys) {
            monkey.turn(monkeys);
        }
    }

    /*
        Monkey 0:
          Starting items: 79, 98
          Operation: new = old * 19
          Test: divisible by 23
            If true: throw to monkey 2
            If false: throw to monkey 3
     */
    public Monkey readMonkey(List<String> lines) {
        String monkeyid = lines.get(0);
        int index = Integer.parseInt(monkeyid.substring(7, monkeyid.length() - 1));
        List<Item> items = Arrays.stream(lines.get(1).substring(18).split(",")).map(String::trim)
                .mapToInt(Integer::parseInt).mapToObj(Item::new).toList();
        IntUnaryOperator wf = i -> i;
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
        IntPredicate decider = i -> i % dec == 0;
        int ifTrue = Integer.parseInt(lines.get(4).substring("    If true: throw to monkey ".length()));
        int ifFalse = Integer.parseInt(lines.get(5).substring("    If false: throw to monkey ".length()));

        return new Monkey(index, items, wf, decider, ifTrue, ifFalse);
    }

    public Monkey[] readInput(Path path) {
        List<List<String>> monks = splitOnEmptyLines(path);
        return monks.stream().map(this::readMonkey).toList().toArray(new Monkey[monks.size()]);
    }

    private int partOne(Monkey[] test) {
        for (int i = 0; i < 20; i++) {
            round(test);
        }
        int result = Arrays.stream(test).mapToInt(Monkey::countInspected).sorted().skip(test.length -2).reduce(1, (a, b) -> a * b);
        return result;
    }

    @Override
    public void run() {
        Monkey[] test = readInput(getTestInputPath());
        int result = partOne(test);
        System.out.println("Test part one : "+result);

        Monkey[] real = readInput(getInputPath());
        result = partOne(real);
        System.out.println("Real part one : "+result);
    }

}
