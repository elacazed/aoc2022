package fr.ela.aoc2022;

import java.nio.file.Path;

public class D25 extends AoC {


    public long snafuToDecimal(String snafu) {
        long value = 0;
        int power = snafu.length() - 1;

        for (char c : snafu.toCharArray()) {
            long digitValue = (long) Math.pow(5, power);
            int digit = snafuDigitToValue(c);
            value += (digit * digitValue);
            power--;
        }
        return value;
    }

    int snafuDigitToValue(char c) {
        return switch (c) {
            case '1' -> 1;
            case '2' -> 2;
            case '0' -> 0;
            case '-' -> -1;
            case '=' -> -2;
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
    }


    public String decimalToSnafu(long number) {
        String base5 = Long.toString(number, 5);
        StringBuilder sb = new StringBuilder();
        int index = base5.length() - 1;
        int retenue = 0;
        while (index >= 0) {
            int base5Digit = base5.charAt(index) - '0';
            base5Digit += retenue;
            switch (base5Digit) {
                case 0, 1, 2 -> {
                    sb.append(base5Digit);
                    retenue = 0;
                }
                case 3 -> {
                    sb.append("=");
                    retenue = 1;
                }
                case 4 -> {
                    sb.append("-");
                    retenue = 1;
                }
                case 5 -> {
                    sb.append("0");
                    retenue = 1;
                }
                default -> throw new IllegalStateException("Unexpected value: " + base5Digit);
            }
            index --;
        }
        if (retenue > 0) {
            sb.append(retenue);
        }
        return sb.reverse().toString();
    }

    public void partOne(String kind, Path path) {
        long sum = stream(path).mapToLong(this::snafuToDecimal).sum();
        System.out.println(kind + " SNAFU sum in decimal = " + sum+" and in SNAFU : "+decimalToSnafu(sum));
    }


    @Override
    public void run() {
        partOne("Test", getTestInputPath()); // 2=-1=0
        partOne("Real", getInputPath()); // 2----0=--1122=0=0021

        //partTwo("Test", getTestInputPath()); // 54
        //partTwo("Real", getInputPath()); // 859

    }

}
