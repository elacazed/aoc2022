package fr.ela.aoc2022;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class D20 extends AoC {

    private static final long KEY = 811589153;

    record Number(int position, long value) {

    }

    record Secret(List<Number> data) {

        public Secret multiply(Long key) {
            return new Secret(data.stream().map(n -> new Number(n.position, n.value * key)).collect(Collectors.toList()));
        }

        public Secret mix(int times) {
            Secret result = new Secret(new ArrayList<>(data));
            for (int i = 0; i < times; i++) {
                for (Number n : data) {
                    result.move(n);
                }
            }
            return result;
        }

        public void move(Number number) {
            int currentIndex = data.indexOf(number);
            Number toMove = data.remove(currentIndex);
            int size = data.size();
            int newIndex;
            if (toMove.value + currentIndex >= 0) {
                newIndex = (int) ((toMove.value + currentIndex) % size);
            } else {
                newIndex = (int) (size - (-(toMove.value + currentIndex) % (size)));
            }
            // Move the number:
            data.add(newIndex == 0 ? size : newIndex, toMove);
            //System.out.println("After moving " + number.value + " : " + this);
        }

        public String toString() {
            return data.stream().map(n -> Long.toString(n.value())).collect(Collectors.joining(",", "[", "]"));
        }

        public long getGroveCoordinates() {
            int size = data.size();
            int indexOfZero = IntStream.range(0, size).filter(i -> data.get(i).value == 0).findFirst().orElseThrow();
            Number mille = data.get((indexOfZero + 1000) % size);
            Number deuxmille = data.get((indexOfZero + 2000) % size);
            Number troismille = data.get((indexOfZero + 3000) % size);
            return mille.value + deuxmille.value + troismille.value;
        }

    }

    public Secret getInput(Path path) {
        List<Integer> secret = stream(path).map(Integer::parseInt).toList();
        LinkedList<Number> numbers = IntStream.range(0, secret.size()).mapToObj(i -> new Number(i, secret.get(i))).collect(Collectors.toCollection(LinkedList::new));
        return new Secret(numbers);
    }

    public void partOne(String kind, Secret secret) {
        Secret mixed = secret.mix(1);
        System.out.println(kind + " coordinates : " + mixed.getGroveCoordinates());
    }

    public void partTwo(String kind, Secret secret) {
        Secret mult = secret.multiply(KEY);
        Secret mixed = mult.mix(10);
        System.out.println(kind + " coordinates : " + mixed.getGroveCoordinates());
    }

    @Override
    public void run() {
        Secret test = getInput(getTestInputPath());
        partOne("Test", test);
        partTwo("Test", test);

        Secret secret = getInput(getInputPath());
        partOne("Real", secret);
        partTwo("Real", secret);
    }

}
