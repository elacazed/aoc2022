package fr.ela.aoc2022;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class D20 extends AoC {

    record Number(int position, int value) {

    }

    record Secret(List<Number> data) {

        public Secret mix() {
            Secret result = new Secret(new ArrayList<>(data));
            for (Number n : data) {
                result.move(n);
            }
            return result;
        }

        public void move(Number number) {
            int currentIndex = data.indexOf(number);
            Number toMove = data.remove(currentIndex);
            int size = data.size();
            int newPosition = ((toMove.value + currentIndex) >= 0 ?
                    ((toMove.value + currentIndex) % size) :
                    (size - (-(toMove.value + currentIndex) % (size)))
            );
            // Move the number:
            data.add(newPosition == 0 ? size : newPosition, toMove);
            //System.out.println("After moving " + number.value + " : " + this);
        }

        public String toString() {
            return data.stream().map(n -> Integer.toString(n.value())).collect(Collectors.joining(",", "[", "]"));
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
        Secret mixed = secret.mix();
        System.out.println(kind + " Mixed secret : " + mixed);
        System.out.println(kind + " coordinates : " + mixed.getGroveCoordinates());
    }

    @Override
    public void run() {
        Secret test = getInput(getTestInputPath());
        partOne("Test", test);

        Secret secret = getInput(getInputPath());
        partOne("Real", secret);
    }
}
