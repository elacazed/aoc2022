package fr.ela.aoc2022;

import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class D21 extends AoC {

    private class MonkeyMap {
        Map<String, UnresolvedMonkey> unresolved = new HashMap<>();
        Map<String, NumberMonkey> resolved = new HashMap<>();

        public MonkeyMap combine(MonkeyMap other) {
            unresolved.putAll(other.unresolved);
            resolved.putAll(other.resolved);
            return this;
        }

        public void add(Monkey monkey) {
            if (monkey instanceof NumberMonkey) {
                resolved.put(monkey.name(), (NumberMonkey) monkey);
            }
            if (monkey instanceof UnresolvedMonkey) {
                unresolved.put(monkey.name(), (UnresolvedMonkey) monkey);
            }
        }

        boolean isResolved(String name) {
            return resolved.containsKey(name);
        }

        boolean resolve() {
            while (!unresolved.isEmpty()) {
                List<Monkey> calculated = new ArrayList<>();
                for (Map.Entry<String, UnresolvedMonkey> current : unresolved.entrySet()) {
                    UnresolvedMonkey monkey = current.getValue();
                    if (monkey.dependsOn().stream().anyMatch(this::isResolved)) {
                        Monkey nm = monkey.resolve(resolved);
                        calculated.add(nm);
                        add(nm);
                    }
                }
                if (calculated.isEmpty()) {
                    return false;
                }
                calculated.stream().filter(m -> m instanceof NumberMonkey).map(Monkey::name).forEach(unresolved::remove);
            }
            return true;
        }
    }

    public interface Monkey {
        String name();

    }

    public interface UnresolvedMonkey extends Monkey {
        String name();

        Monkey resolve(Map<String, NumberMonkey> monkeyMap);

        List<String> dependsOn();
    }

    public record NumberMonkey(String name, long value) implements Monkey {
    }

    public record FunctionMonkey(String name, Function<Long, Long> operation, Function<Long, Long> inverse,
                                 long value,
                                 String operand) implements UnresolvedMonkey {
        public List<String> dependsOn() {
            return List.of(operand);
        }

        public NumberMonkey resolve(Map<String, NumberMonkey> monkeyMap) {
            return new NumberMonkey(name, operation.apply(monkeyMap.get(operand).value()));
        }
    }

    public record BiFunctionMonkey(String name,
                                   BiFunction<Long, Long, Long> operation,
                                   BiFunction<Long, Long, Long> leftInverse,
                                   BiFunction<Long, Long, Long> rightInverse,
                                   String left,
                                   String right) implements UnresolvedMonkey {

        public List<String> dependsOn() {
            return List.of(left, right);
        }


        public Monkey resolve(Map<String, NumberMonkey> monkeyMap) {
            NumberMonkey leftMonkey = monkeyMap.get(left);
            NumberMonkey rightMonkey = monkeyMap.get(right);
            if (leftMonkey != null && rightMonkey != null) {
                return new NumberMonkey(name, operation.apply(leftMonkey.value, rightMonkey.value));
            }
            if (leftMonkey == null) {
                Function<Long, Long> f = l -> operation.apply(l, rightMonkey.value);
                Function<Long, Long> inverse = x -> leftInverse.apply(x, rightMonkey.value);
                return new FunctionMonkey(name, f, inverse, rightMonkey.value, left);
            } else {
                Function<Long, Long> f = r -> operation.apply(leftMonkey.value, r);
                Function<Long, Long> inverse = x -> rightInverse.apply(x, leftMonkey.value);
                return new FunctionMonkey(name, f, inverse, leftMonkey.value, right);
            }
        }
    }

    public Monkey parseMonkey(String line) {
        String[] parts = line.split(":");
        String name = parts[0];
        String[] val = parts[1].trim().split(" ");
        if (val.length == 1) {
            long value = Long.parseLong(val[0]);
            return new NumberMonkey(name, value);
        }
        String left = val[0];
        String right = val[2];
        return switch (val[1].charAt(0)) {
            case '+' -> new BiFunctionMonkey(name, (l, r) -> l + r, (x, r) -> x - r, (x, l) -> x - l, left, right);
            case '*' -> new BiFunctionMonkey(name, (l, r) -> l * r, (x, r) -> x / r, (x, l) -> x / l, left, right);
            case '-' -> new BiFunctionMonkey(name, (l, r) -> l - r, (x, r) -> x + r, (x, l) -> l - x, left, right);
            case '/' -> new BiFunctionMonkey(name, (l, r) -> l / r, (x, r) -> x * r, (x, l) -> l / x, left, right);
            default -> throw new IllegalStateException();
        };
    }

    public MonkeyMap parse(Path path) {
        return stream(path, this::parseMonkey).collect(MonkeyMap::new, MonkeyMap::add, MonkeyMap::combine);
    }


    public void partOne(String kind, Path path) {
        MonkeyMap map = parse(path);
        map.resolve();
        System.out.println(kind + " result part one : " + map.resolved.get("root").value);
    }

    public void partTwo(String kind, Path path) {
        MonkeyMap monkeyMap = parse(path);
        // Remove Human to stop when needed.
        monkeyMap.resolved.remove("humn");
        monkeyMap.resolve();
        if (monkeyMap.unresolved.values().stream().anyMatch(m -> m instanceof BiFunctionMonkey)) {
            throw new IllegalStateException("I can't solve this!");
        }

        FunctionMonkey root = (FunctionMonkey) monkeyMap.unresolved.get("root");
        long value = root.value;

        FunctionMonkey r = (FunctionMonkey) monkeyMap.unresolved.get(root.operand);
        Function<Long, Long> function = Function.identity();
        do {
            function = function.andThen(r.inverse);
            r = (FunctionMonkey) monkeyMap.unresolved.get(r.operand);
        } while (! "humn".equals(r.operand));
        function = function.andThen(r.inverse);
        System.out.println(kind + " result part two : " + function.apply(value));

    }

    @Override
    public void run() {
        partOne("Test", getTestInputPath());
        partOne("Real", getInputPath());

        partTwo("Test", getTestInputPath());
        partTwo("Real", getInputPath());

    }

}
