package fr.ela.aoc2022;

import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;

public class D21 extends AoC {

    private class MonkeyMap {
        Map<String, OpMonkey> unresolved = new HashMap<>();
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
            if (monkey instanceof OpMonkey) {
                unresolved.put(monkey.name(), (OpMonkey) monkey);
            }
        }

        boolean isResolved(String name) {
            return resolved.containsKey(name);
        }

        void resolve() {
            while (!unresolved.isEmpty()) {
                List<String> calculated = new ArrayList<>();
                for (Map.Entry<String, OpMonkey> current : unresolved.entrySet()) {
                    String name = current.getKey();
                    OpMonkey monkey = current.getValue();
                    if (monkey.dependsOn().stream().allMatch(this::isResolved)) {
                        calculated.add(name);
                        NumberMonkey nm = monkey.resolve(resolved);
                        resolved.put(nm.name, nm);
                    }
                }
                calculated.forEach(unresolved::remove);
            }
        }
    }

    public interface Monkey {
        String name();

    }

    public record NumberMonkey(String name, long value) implements Monkey {
    }

    public record OpMonkey(String name, BiFunction<Long, Long, Long> operation, String left,
                           String right) implements Monkey {

        public List<String> dependsOn() {
            return List.of(left, right);
        }

        public NumberMonkey resolve(Map<String, NumberMonkey> monkeyMap) {
            return new NumberMonkey(name, operation.apply(monkeyMap.get(left).value(), monkeyMap.get(right).value()));
        }
    }

    public Monkey parseMonkey(String line) {
        String[] parts = line.split(":");
        String name = parts[0];
        String[] val = parts[1].trim().split(" ");
        if (val.length == 1) {
            //dbpl: 5
            long value = Long.parseLong(val[0]);
            return new NumberMonkey(name, value);
        }
        //cczh: sllz + lgvd
        String left = val[0];
        String right = val[2];
        BiFunction<Long, Long, Long> operation = switch (val[1].charAt(0)) {
            case '+' -> (a,b) -> a+b;
            case '*' -> (a,b) -> a*b;
            case '-' -> (a,b) -> a-b;
            case '/' -> (a,b) -> a/b;
            default -> throw new IllegalStateException();
        };
        return new OpMonkey(name, operation, left, right);
    }

    public MonkeyMap parse(Path path) {
        return stream(path, this::parseMonkey).collect(MonkeyMap::new, MonkeyMap::add, MonkeyMap::combine);
    }


    public void partOne(String kind, Path path) {
        MonkeyMap map = parse(path);
        map.resolve();
        System.out.println(kind+" result part one : "+map.resolved.get("root").value);
    }

    public void partTwo(String kind) {
    }

    @Override
    public void run() {
        partOne("Test", getTestInputPath());
        partOne("Real", getInputPath());

        //partTwo("Test");
        //partTwo("Real");
    }

}
