package fr.ela.aoc2022;


import java.util.List;

public class D10 extends AoC {
/*
Maybe you can learn something by looking at the value of the X register throughout execution.
 For now, consider the signal strength (the cycle number multiplied by the value of the X register)
 during the 20th cycle and every 40 cycles after that (that is, during the 20th, 60th, 100th, 140th, 180th, and 220th cycles).

20 * 21 = 420.
60 * 19 = 1140.
100 * 18 = 1800.
140 * 21 = 2940.
180 * 16 = 2880.
220 * 18 = 3960.

 */

    class Computer {
        int cycle = 0;
        int value = 1;
        int accumulator = 0;

        void incCycle() {
            cycle++;
            if (cycle > 0 && cycle <= 220 && cycle%40 == 20) {
                System.out.println("Adding value "+cycle+"*"+value+" = "+cycle*value);
                accumulator += cycle * value;
            }
        }

        void execute(String command) {
            if ("noop".equals(command)) {
               incCycle();
            }
            if (command.startsWith("addx")) {
                incCycle();
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
        System.out.println("Test part one : " + calculate(list(getTestInputPath())).accumulator);
        System.out.println("Real part one : " + calculate(list(getInputPath())).accumulator);
//        System.out.println("Test part two : " + path(list(getTestInputPath(), this::readMove), 10).size());
//        System.out.println("Real part two : " + path(list(getInputPath(), this::readMove), 10).size());

    }
}
