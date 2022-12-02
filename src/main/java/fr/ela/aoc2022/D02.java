package fr.ela.aoc2022;

import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

public class D02 extends AoC {

    public static final int ROCK = 0;
    public static final int PAPER = 1;
    public static final int SCISSORS = 2;

    public static final int LOSE = 0;
    public static final int DRAW = 1;
    public static final int WIN = 2;

    static int score(int play, int outcome) {
        return play + 1 + outcome * 3;
    }

    ToIntFunction<String> scorePartOne = (line) -> {
        int one = line.charAt(0) - 'A';
        int two = line.charAt(2) - 'X';
        return score(two, outcome(one, two));
    };

    ToIntFunction<String> scorePartTwo = (line) -> {
        int one = line.charAt(0) - 'A';
        int outcome = line.charAt(2) - 'X';
        return score(whatToPlay(one, outcome), outcome);
    };

    public static int outcome(int one, int two) {
        if (one == two) {
            return DRAW;
        }
        return switch (one) {
            case ROCK -> two == PAPER ? WIN : LOSE;
            case SCISSORS -> two == ROCK ? WIN : LOSE;
            case PAPER -> two == SCISSORS ? WIN : LOSE;
            default -> throw new IllegalArgumentException(one + " : invalid play value");
        };
    }

    public static int whatToPlay(int one, int outcome) {
        return IntStream.range(0, 3).filter(two -> outcome(one, two) == outcome).findFirst().orElseThrow();
    }


    @Override
    public void run() {
        System.out.println("Test Score : " + stream(getTestInputPath()).mapToInt(scorePartOne).sum());
        System.out.println("Real Score : " + stream(getInputPath()).mapToInt(scorePartOne).sum());
        System.out.println("Test Score : " + stream(getTestInputPath()).mapToInt(scorePartTwo).sum());
        System.out.println("Real Score : " + stream(getInputPath()).mapToInt(scorePartTwo).sum());
    }


}
