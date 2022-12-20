package fr.ela.aoc2022;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class D19 extends AoC {
    public static final int ORE = 0;
    public static final int CLAY = 1;
    public static final int OBSIDIAN = 2;
    public static final int GEODE = 3;

    public static final int[] TYPES = {ORE, CLAY, OBSIDIAN};

    public static final int[] ROBOT_TYPES = {ORE, CLAY, OBSIDIAN, GEODE};

    boolean isRobot(int option) {
        return inRange(option, ORE, GEODE + 1);
    }
    private static final Pattern ORE_COST_PATTERN = Pattern.compile(" Each [a-z]+ robot costs ([0-9]+) ore");
    private static final Pattern ORE_AND_CLAY_COST_PATTERN = Pattern.compile(" Each obsidian robot costs ([0-9]+) ore and ([0-9]+) clay");
    private static final Pattern ORE_AND_OBSIDIAN_COST_PATTERN = Pattern.compile(" Each [a-z]+ robot costs ([0-9]+) ore and ([0-9]+) obsidian");

    public class Blueprint {
        final int id;
        final int[][] costs;

        public Blueprint(int id, int[] oreRobot, int[] clayRobot, int[] obsidianRobot, int[] geodeRobot) {
            this.id = id;
            costs = new int[4][];
            costs[ORE] = oreRobot;
            costs[CLAY] = clayRobot;
            costs[OBSIDIAN] = obsidianRobot;
            costs[GEODE] = geodeRobot;
        }

        int[] getCost(int type) {
            return costs[type];
        }

        public int qualityLevel(int time) {
            int max = getMaxGeodes(time);
            System.out.println("Max geodes gathered in " + time + " minutes : " + max);
            return id * getMaxGeodes(time);
        }

        public int getMaxGeodes(int time) {
            int[] best = {0};
            // Start with one ore robot
            int[] fleet = new int[]{1, 0, 0, 0};
            int[] resources = new int[]{0, 0, 0, 0};
            getMaxGeodes(time, fleet, resources, best);
            return best[0];
        }


        private void getMaxGeodes(int time, int[] fleet, int[] resources, int[] best) {
            if (time == 0) {
                best[0] = Math.max(best[0], resources[3]);
                return;
            }

            for (int type = 0, n = ROBOT_TYPES.length; type < n; type++) {
                /*
                * Si on cherche à construire un robot pour autre chose que des géodes, et qu'on sait qu'on produit assez par tout pour le construire, on ne va pas plus loin.
                */
                int ii = type;
                if (type != GEODE && Arrays.stream(costs).allMatch(cost -> cost[ii] <= fleet[ii])) {
                    continue;
                }

                int[] cost = getCost(type);
                int dt = timeNeeded(cost, resources, fleet);
                if (dt < time) {
                    // On récupère les resources,
                    inc(resources, fleet, dt + 1);
                    // On paye le robot,
                    inc(resources, cost, - 1);
                    // On ajoute le robot dans la flotte.
                    fleet[type]++;
                    getMaxGeodes(time - dt - 1, fleet, resources, best);
                    // dépilage : on fait les opérations inverses.
                    fleet[type]--;
                    inc(resources, cost, 1);
                    inc(resources, fleet, - (dt + 1));
                } else {
                    inc(resources, fleet, time);
                    getMaxGeodes(0, fleet, resources, best);
                    inc(resources, fleet, - time);
                }
            }
        }

        /**
         * Détermine le temps nécessaire à la construction d'un robot d'un coût donné.
         *
         * @param cost      le coût de construction
         * @param resources les resources qu'on a déjà
         * @param fleet     le nombre de robots
         * @return the time delay
         */
        private static int timeNeeded(int[] cost, int[] resources, int[] fleet) {
            int timeNeeded = 0;
            // On cherche le temps pour les 3 types de resources, on retourne le max des 3.
            for (int type : TYPES) {
                if (resources[type] >= cost[type]) {
                    // On a déjà assez de la resource
                    continue;
                } else if (cost[type] > 0 && fleet[type] == 0) {
                    // on n'a pas assez d ela resource, et pas le bon robot pour la récupérer
                    return Integer.MAX_VALUE;
                } else {
                    // on a un/des robots pour récupérer la resource nécessaire,
                    int time = Math.ceilDiv(cost[type] - resources[type], fleet[type]);
                    timeNeeded = Math.max(timeNeeded, time);
                }
            }
            return timeNeeded;
        }

        // Ajoute Times fois le nombre de robots à la resource correspondante.
        private static void inc(int[] resources, int[] fleet, int times) {
            for (int i = 0, n = resources.length; i < n; i++) {
                resources[i] += fleet[i] * times;
            }
        }


    }


    Blueprint parseBlueprint(String line) {
        int indexOfColon = line.indexOf(":");
        int id = Integer.parseInt(line.substring("Blueprint ".length(), indexOfColon));
        String[] costs = line.substring(indexOfColon + 1).split("\\.");
        Matcher matcher = ORE_COST_PATTERN.matcher(costs[0]);
        matcher.matches();
        int[] ore = new int[]{Integer.parseInt(matcher.group(1)), 0, 0, 0};
        matcher = ORE_COST_PATTERN.matcher(costs[1]);
        matcher.matches();
        int[] clay = new int[]{Integer.parseInt(matcher.group(1)), 0, 0, 0};
        matcher = ORE_AND_CLAY_COST_PATTERN.matcher(costs[2]);
        matcher.matches();
        int[] obsidian = new int[]{Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), 0, 0};
        matcher = ORE_AND_OBSIDIAN_COST_PATTERN.matcher(costs[3]);
        matcher.matches();
        int[] geode = new int[]{Integer.parseInt(matcher.group(1)), 0, Integer.parseInt(matcher.group(2)), 0};

        return new Blueprint(id, ore, clay, obsidian, geode);
    }


    @Override
    public void run() {
        List<Blueprint> testBlueprints = stream(getTestInputPath(), this::parseBlueprint).toList();
        System.out.println("Test part one : " + testBlueprints.stream().mapToInt(bp -> bp.qualityLevel(24)).sum());
        System.out.println("Test part two : " + testBlueprints.stream().mapToInt(bp -> bp.getMaxGeodes(32)).reduce(1, (x,y) -> x*y));

        List<Blueprint> blueprints = stream(getInputPath(), this::parseBlueprint).toList();
        System.out.println("Real part one : " + blueprints.stream().mapToInt(bp -> bp.qualityLevel(24)).sum());
        System.out.println("Real part two : " +blueprints.stream().filter(bp -> bp.id < 4).mapToInt(bp -> bp.getMaxGeodes(32)).reduce(1, (x,y) -> x*y));
    }
}
