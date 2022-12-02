package fr.ela.aoc2022;

import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

public class D02 extends AoC {

    record IntPair(int one, int two) {}

    static IntPair toPair(String line) {
        int one = line.charAt(0) - 'A';
        int two = line.charAt(2) - 'X';
        return new IntPair(one, two);
    }

    // Scoring function : what I played = one, outcome = two.
    ToIntFunction<IntPair> scoring = pair -> pair.one + 1 + pair.two * 3;

    // C'est un modulo en fait :)
    ToIntFunction<IntPair> partOneModulo = pair -> scoring.applyAsInt(new IntPair(pair.two, ((pair.two - pair.one) + 4) % 3));
    ToIntFunction<IntPair> partTwoModulo = pair -> scoring.applyAsInt(new IntPair((pair.one + pair.two + 2) % 3, pair.two));

    @Override
    public void run() {
        System.out.println("Test Score Part 1 : " + stream(getTestInputPath(), D02::toPair).mapToInt(partOne).sum());
        System.out.println("Real Score Part 1 : " + stream(getInputPath(), D02::toPair).mapToInt(partOne).sum());
        System.out.println("Real Smart Score Part 1 : " + stream(getInputPath(), D02::toPair).mapToInt(partOneModulo).sum());
        System.out.println("Test Score Part 2 : " + stream(getTestInputPath(), D02::toPair).mapToInt(partTwo).sum());
        System.out.println("Real Score Part 2 : " + stream(getInputPath(), D02::toPair).mapToInt(partTwo).sum());
        System.out.println("Real Smart Score Part 2 : " + stream(getInputPath(), D02::toPair).mapToInt(partTwoModulo).sum());
    }

    //-- Solution d'origine pour avoir les étoiles :)
    public static final int ROCK = 0;
    public static final int PAPER = 1;
    public static final int SCISSORS = 2;

    public static final int LOSE = 0;
    public static final int DRAW = 1;
    public static final int WIN = 2;

    /**
     * Calcule le résultat en fonction des 2 coups joués;
     * @param pair one = adversaire, two = moi
     * @return le résultat.
     */
    public static int outcome(IntPair pair) {
        if (pair.one == pair.two) {
            return DRAW;
        }
        return switch (pair.one) {
            case ROCK -> pair.two == PAPER ? WIN : LOSE;
            case SCISSORS -> pair.two == ROCK ? WIN : LOSE;
            case PAPER -> pair.two == SCISSORS ? WIN : LOSE;
            default -> throw new IllegalArgumentException(pair.one + " : invalid play value");
        };
    }
    /** Inverse de fonction comme un goret :)
     * @param pair one = le coup joué, two = le résultat
     * @return le coup à jouer pour obtenir le résultat.
     */
    public static int whatToPlay(final IntPair pair) {
        return IntStream.range(0, 3).filter(two -> outcome(new IntPair(pair.one, two)) == pair.two).findFirst().orElseThrow();
    }

    ToIntFunction<IntPair> partOne = pair -> scoring.applyAsInt(new IntPair(pair.two, outcome(pair)));
    ToIntFunction<IntPair> partTwo = pair -> scoring.applyAsInt(new IntPair(whatToPlay(pair), pair.two));



}
