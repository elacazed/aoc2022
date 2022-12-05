package fr.ela.aoc2022;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class D05 extends AoC {

    Pattern pattern = Pattern.compile("move ([0-9]+) from ([0-9]+) to ([0-9]+)");

    record Move(int number, int from, int to) {
        void applyPartOne(List<Stack<Character>> list) {
            apply(list.get(from -1), list.get(to -1));
        }

        void applyPartTwo(List<Stack<Character>> list) {
            Stack<Character> fromStack = list.get(from - 1);
            Stack<Character> toStack = list.get(to - 1);
            Stack s = new Stack();
            apply(fromStack, s);
            apply(s, toStack);
        }

        void apply(Stack<Character> fromStack, Stack<Character> toStack) {
            IntStream.range(0, number).forEach(i -> toStack.push(fromStack.pop()));
        }
    }

    public String getResult(List<Stack<Character>> stacks) {
        char[] word = new char[stacks.size()];
        for (int i = 0; i < word.length; i++) {
            word[i] = stacks.get(i).peek();
        }
        return new String(word);
    }

    private Move readMove(String line) {
        Matcher m = pattern.matcher(line);
        m.matches();
        return new Move(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
    }

    private int indexOfCrate(int indexOfStack) {
        return 1 + indexOfStack * 4;
    }

    private String getResult(int size, List<String> input, BiConsumer<Move, List<Stack<Character>>> moveFunction) {
        List<Stack<Character>> stacks = new ArrayList<>();
        IntStream.range(0, size).forEach(i -> stacks.add(new Stack<>()));
        Iterator<String> it = input.iterator();
        String s = it.next();
        while (it.hasNext() && !s.isBlank()) {
            final String line = s;
            IntStream.range(0, size).forEach(i -> addChar(stacks.get(i), line, indexOfCrate(i)));
            s = it.next();
        }
        // Fin de la lecture de l'init.
        while (it.hasNext()) {
            moveFunction.accept(readMove(it.next()), stacks);
        }
        return getResult(stacks);
    }

    void addChar(Stack<Character> stack, String line, int index) {
        if (index < line.length()) {
            char c = line.charAt(index);
            if (c != ' ' && ! Character.isDigit(c)) {
                stack.add(0, line.charAt(index));
            }
        }
    }


    @Override
    public void run() {
        System.out.println("Test Score Part 1 : " + getResult(3, list(getTestInputPath()), Move::applyPartOne));
        System.out.println("Real Score Part 1 : " + getResult(9, list(getInputPath()), Move::applyPartOne));

        System.out.println("Test Score Part 2 : " + getResult(3, list(getTestInputPath()), Move::applyPartTwo));
        System.out.println("Real Score Part 2 : " + getResult(9, list(getInputPath()), Move::applyPartTwo));

    }


}
