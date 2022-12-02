package fr.ela.aoc2022;

import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

public class D02 extends AoC {
    public static final int ROCK = 0;
    public static final int PAPER = 1;
    public static final int SCISSORS = 2;

    public static final int LOSE = 0;
    public static final int DRAW = 1;
    public static final int WIN = 2;

    record IntPair(int one, int two) {}

    static IntPair toPair(String line) {
        int one = line.charAt(0) - 'A';
        int two = line.charAt(2) - 'X';
        return new IntPair(one, two);
    }

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

    /**
     * Vazy, en fait c'est un module.
     * @param pair
     * @return
     */
    public static int smartOutcome(IntPair pair) {
        return ((pair.two - pair.one) + 4) % 3;
    }

    /** Inverse de fonction comme un goret :)
     * @param pair one = le coup joué, two = le résultat
     * @return le coup à jouer pour obtenir le résultat.
     */
    public static int whatToPlay(final IntPair pair) {
        return IntStream.range(0, 3).filter(two -> outcome(new IntPair(pair.one, two)) == pair.two).findFirst().orElseThrow();
    }

    /**
     * Là aussi c'est un modulo :)
     * @param pair
     * @return
     */
    public static int smartWhatToPlay(IntPair pair) {
        return (pair.one + pair.two + 2) % 3;
    }

    // Scoring function : what I played = one, outcome = two.
    ToIntFunction<IntPair> scoring = pair -> pair.one + 1 + pair.two * 3;

    ToIntBiFunction<IntPair, ToIntFunction<IntPair>> partOne = (pair, outcomeFunction) -> scoring.applyAsInt(new IntPair(pair.two, outcomeFunction.applyAsInt(pair)));
    ToIntBiFunction<IntPair, ToIntFunction<IntPair>> partTwo = (pair, whatToPlayFunction) -> scoring.applyAsInt(new IntPair(whatToPlayFunction.applyAsInt(pair), pair.two));

    @Override
    public void run() {
        System.out.println("Test Score : " + stream(getTestInputPath(), D02::toPair).mapToInt(pair -> partOne.applyAsInt(pair, D02::outcome)).sum());
        System.out.println("Real Score : " + stream(getInputPath(), D02::toPair).mapToInt(pair -> partOne.applyAsInt(pair, D02::outcome)).sum());
        System.out.println("Real Smart Score : " + stream(getInputPath(), D02::toPair).mapToInt(pair -> partOne.applyAsInt(pair, D02::smartOutcome)).sum());
        System.out.println("Test Score : " + stream(getTestInputPath(), D02::toPair).mapToInt(pair-> partTwo.applyAsInt(pair, D02::whatToPlay)).sum());
        System.out.println("Real Score : " + stream(getInputPath(), D02::toPair).mapToInt(pair-> partTwo.applyAsInt(pair, D02::whatToPlay)).sum());
        System.out.println("Real Smart Score : " + stream(getInputPath(), D02::toPair).mapToInt(pair-> partTwo.applyAsInt(pair, D02::smartWhatToPlay)).sum());
    }


}
