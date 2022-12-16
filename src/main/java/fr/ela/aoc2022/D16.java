package fr.ela.aoc2022;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class D16 extends AoC {

    private static final Pattern INPUT_PATTERN = Pattern.compile("Valve ([A-Z]+) has flow rate=([0-9]+); tunnels? leads? to valves? ([A-Z, ]+)");

    public record Valve(String name, int flowRate, String[] destinations) {
    }

    public record Pipe(String from, String to) {
    }

    public class PipesSystem {
        final Map<String, Valve> valves;
        final Map<Pipe, Integer> distances;

        public PipesSystem(Map<String, Valve> valves) {
            this.valves = valves;
            this.distances = findAllDistances(valves);
        }

        /**
         * On précalcule les "distances" (ie: temps de déplacement) entre toutes les paires de valves.
         *
         * @param valves
         * @return
         */
        private Map<Pipe, Integer> findAllDistances(Map<String, Valve> valves) {
            Map<Pipe, Integer> pipes = new HashMap<>();
            for (Valve from : valves.values()) {
                for (Valve to : valves.values()) {
                    int distance = from.equals(to) ? 0 : 1000000000;
                    pipes.put(new Pipe(from.name, to.name), distance);
                }
            }
            for (int i = 0; i < valves.size(); i++) {
                for (Pipe p : pipes.keySet()) {
                    for (String name : valves.get(p.from).destinations) {
                        Pipe newPipe = new Pipe(name, p.to);
                        pipes.put(newPipe, Math.min(pipes.get(newPipe), pipes.get(p) + 1));
                    }
                }
            }
            return pipes;
        }
    }


    /**
     * Le machin qui parcours l'arbre.
     */
    public class PipesCrawler {
        final PipesSystem system;
        // Memoization : set des valves ouvertes + flowrate correspondant.
        private final Map<Set<String>, Integer> knownStates;
        final int totalMinutes;
        final boolean withBabar;

        public PipesCrawler(PipesSystem system, int totalMinutes, boolean withBabar) {
            this.system = system;
            this.totalMinutes = totalMinutes;
            this.withBabar = withBabar;
            knownStates = new HashMap<>();
        }

        public int findMostPressure() {
            return findMostPressure("AA", totalMinutes, 0, new HashSet<>(), withBabar);
        }

        protected int findMostPressure(String start, int minutes, int pressure, Set<String> openValves, boolean withAnElephant) {
            if (minutes <= 1) {
                if (withAnElephant) {
                    return addElephantPressure(pressure, openValves);
                } else {
                    return pressure;
                }
            }
            int minutesRemaining = minutes;
            int currentPressure = pressure;

            Valve current = system.valves.get(start);

            if (current.flowRate > 0) {
                // On ouvre une valve => ca prend du temps.
                minutesRemaining--;
                // Mais on ajoute de la pression (le nombre de minutes qu'il reste * le flowrate de cette valve)
                currentPressure += minutesRemaining * current.flowRate;
            }
            int totalPressure = currentPressure;
            for (Valve next : system.valves.values()) {
                // Pour chaque valve avec un flowrate > 0 qu'on n'a pas déjà ouverte,
                if (next.flowRate > 0 && !openValves.contains(next.name)) {
                    openValves.add(next.name);
                    // On va mettre distances.get(cur, next) à y arriver,
                    int minutesArrived = minutesRemaining - system.distances.get(new Pipe(current.name, next.name));
                    // Et si on l'ouvre, alors on ajoute la pression max obtenue dans le temps restant avec ce point de départ à la pression totale.
                    totalPressure = Math.max(
                            findMostPressure(next.name, minutesArrived, currentPressure, openValves, withAnElephant),
                            totalPressure);
                    openValves.remove(next.name);
                }
            }
            return totalPressure;
        }


        private int addElephantPressure(int pressure, Set<String> open) {
            Set<String> key = Set.copyOf(open);
            return pressure + knownStates.computeIfAbsent(key, s -> findMostPressure("AA", totalMinutes, 0, open, false));
        }

    }


    public Valve readInput(String line) {
        Matcher m = INPUT_PATTERN.matcher(line);
        if (m.matches()) {
            return new Valve(m.group(1), Integer.parseInt(m.group(2)), m.group(3).split(", "));
        }
        throw new IllegalArgumentException(line);
    }


    @Override
    public void run() {
        PipesSystem testSystem = new PipesSystem(stream(getTestInputPath(), this::readInput).collect(Collectors.toMap(v -> v.name, Function.identity())));

        PipesCrawler testCrawler = new PipesCrawler(testSystem, 30, false);
        System.out.println("Test part one : " + testCrawler.findMostPressure());

        PipesCrawler testElephantCrawler = new PipesCrawler(testSystem, 26, true);
        System.out.println("Test part two : " + testElephantCrawler.findMostPressure());

        PipesSystem system = new PipesSystem(stream(getInputPath(), this::readInput).collect(Collectors.toMap(v -> v.name, Function.identity())));

        PipesCrawler crawler = new PipesCrawler(system, 30, false);
        System.out.println("Real part one : " + crawler.findMostPressure());
        PipesCrawler elephantCrawler = new PipesCrawler(system, 26, true);
        System.out.println("Real part two : " + elephantCrawler.findMostPressure());
    }
}
