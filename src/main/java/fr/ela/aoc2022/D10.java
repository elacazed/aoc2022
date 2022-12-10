package fr.ela.aoc2022;


import java.util.Arrays;
import java.util.List;

public class D10 extends AoC {

    class Computer {
        int cycle = 0;
        int value = 1;
        int accumulator = 0;
        char[] screen = new char[6 * 40];

        String display() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                sb.append(new String(Arrays.copyOfRange(screen, i * 40, i * 40 + 40))).append("\n");
            }
            return sb.toString();
        }

        void incCycle() {
            cycle++;
            screen[cycle -1] = '.';
            if (cycle <= 220 && cycle % 40 == 20) {
                accumulator += cycle * value;
            }
            int col = (cycle - 1) % 40;
            if (value - 1 <= col && col <= value + 1) {
                screen[cycle - 1] = '#';
            }
        }

        void execute(String command) {
            incCycle();
            if (command.startsWith("addx")) {
                incCycle();
                int amount = Integer.parseInt(command.substring(5));
                value = value + amount;
            }
        }
    }

    public Computer calculate(List<String> commands) {
        Computer computer = new Computer();
        commands.forEach(computer::execute);
        return computer;
    }

    @Override
    public void run() {
        Computer test = calculate(list(getTestInputPath()));
        Computer real = calculate(list(getInputPath()));
        System.out.println("Test part one : " + test.accumulator);
        System.out.println("Real part one : " + real.accumulator);
        System.out.println("Test part two : \n" + test.display());
        System.out.println("Real part two : \n" + real.display());

    }
}
