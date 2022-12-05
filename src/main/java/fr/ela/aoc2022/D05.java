package fr.ela.aoc2022;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class D05 extends AoC {

    Pattern pattern = Pattern.compile("move ([0-9]+) from ([0-9]+) to ([0-9]+)");

    private List<Stack<Character>> stacks = new ArrayList();


    record Move(int number, int from, int to) {
        void apply(List<Stack<Character>> list) {
            Stack<Character> fromStack = list.get(from -1);
            Stack<Character> toStack = list.get(to -1);
            IntStream.range(0, number).forEach(i -> toStack.push(fromStack.pop()));

        }
    }

    public String getResult() {
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

    private String getResult(int size, List<String> input) {
        stacks = new ArrayList<>();
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
            readMove(it.next()).apply(stacks);
        }
        return getResult();
    }

    public String toString() {
        return stacks.stream().map(
                s -> String.join(",", toString(s))
        ).collect(Collectors.joining("\n"));
    }

    public String toString(Stack<Character> stack) {
        char[] chars = new char[stack.size()];
        for (int i = 0; i < stack.size(); i++) {
            chars[i] = stack.get(i);
        }
        return new String(chars);
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
        System.out.println("Test Score Part 1 : " + getResult(3, list(getTestInputPath())));
        System.out.println("Real Score Part 1 : " + getResult(9, list(getInputPath())));

        System.out.println("Test Score Part 2 : ");
        System.out.println("Real Score Part 2 : ");

    }


}
