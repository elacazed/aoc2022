package fr.ela.aoc2022;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

public class D17 extends AoC {

    record Position(int x, long y) {
    }

    static class WindConditions {
        private final String conditions;
        private int index = 0;
        private final int size;

        WindConditions(String conditions) {
            this.conditions = conditions;
            this.size = conditions.length();
        }

        int wind() {
            int wind = conditions.charAt(index) == '>' ? 1 : -1;
            index = (index + 1) % size;
            return wind;
        }
    }

    class CacheKey extends LinkedList<Byte> {

        final int patternDetectionSize;
        int top;
        int bottom;

        public CacheKey(int patternDetectionSize) {
            this.patternDetectionSize = patternDetectionSize;
            top = 0;
            bottom = 0;
        }

        @Override
        public boolean add(Byte aByte) {
            if (size() == patternDetectionSize) {
                removeFirst();
                bottom++;
            }
            top++;
            return super.add(aByte);
        }

        public String getKey() {
            byte[] bytes = new byte[patternDetectionSize];
            int i = 0;
            for (; i < Math.min(patternDetectionSize, size()); i++) {
                bytes[i] = get(i);
            }
            for (; i < patternDetectionSize; i++) {
                bytes[i] = 0;
            }
            return new BigInteger(bytes).toString(16);
        }

        public boolean isComplete() {
            return size() == patternDetectionSize;
        }
    }

    private enum Shape {
        HOR(4, 1, p -> List.of(p, new Position(p.x + 1, p.y), new Position(p.x + 2, p.y), new Position(p.x + 3, p.y))),
        PLUS(3, 3, p -> List.of(new Position(p.x + 1, p.y), new Position(p.x, p.y + 1), new Position(p.x + 1, p.y + 1), new Position(p.x + 2, p.y + 1), new Position(p.x + 1, p.y + 2))),
        CORNER(3, 3, p -> List.of(p, new Position(p.x + 1, p.y), new Position(p.x + 2, p.y), new Position(p.x + 2, p.y + 1), new Position(p.x + 2, p.y + 2))),
        VERT(1, 4, p -> List.of(p, new Position(p.x, p.y + 1), new Position(p.x, p.y + 2), new Position(p.x, p.y + 3))),
        SQUARE(2, 2, p -> List.of(p, new Position(p.x, p.y + 1), new Position(p.x + 1, p.y), new Position(p.x + 1, p.y + 1)));

        final Function<Position, List<Position>> positions;
        final int width;
        final int height;

        Shape(int width, int height, Function<Position, List<Position>> positions) {
            this.positions = positions;
            this.width = width;
            this.height = height;
        }

        List<Position> getAllPositions(Position bottomleft) {
            return positions.apply(bottomleft);
        }
    }

    record State(long shapes, long topOfPile) {
    }

    public class Pit {
        final Set<Position> positions;
        final int width;
        long topOfPile = 0;
        long nbShapesInPit = 0;
        boolean lookingForCycle = true;

        Map<String, State> cache = new HashMap<>();
        final CacheKey cacheKey;
        private long targetNumber;
        private long heightOfCycles = 0;

        public Pit(int width, int keySize) {
            positions = new HashSet<>();
            this.width = width;
            this.cacheKey = new CacheKey(keySize);
        }

        byte getLine(int y) {
            byte b = 0;
            for (int i = 0; i < 7; i++) {
                if (contains(new Position(i, y))) {
                    b |= 1 << i;
                }
            }
            return b;
        }

        public long getHeightOfPile() {
            return topOfPile+heightOfCycles;
        }

        public boolean contains(Position pos) {
            return pos.y < 0 || pos.x < 0 || pos.x >= width || positions.contains(pos);
        }

        public void add(Position position) {
            positions.add(position);
            topOfPile = Math.max(position.y + 1, topOfPile);
        }

        public long restAt(Position position, Shape shape) {
            List<Position> positions = shape.getAllPositions(position);
            LongSummaryStatistics stats = positions.stream().mapToLong(Position::y).summaryStatistics();
            positions.forEach(this::add);
            nbShapesInPit++;
            updateCacheKey(stats.getMin(), stats.getMax());
            if (cacheKey.isComplete() && lookingForCycle) {
                String key = cacheKey.getKey();
                if (cache.containsKey(key)) {
                    State state = cache.get(key);
                    System.out.println("Found pattern of " + cacheKey.size() + " lines at shape n°"+nbShapesInPit);
                    System.out.println(this);
                    int cycleLength = (int) (nbShapesInPit - state.shapes);
                    long cycleHeight = topOfPile - state.topOfPile;
                    long cyclesToTheTop = (targetNumber - nbShapesInPit) / cycleLength;
                    nbShapesInPit += cyclesToTheTop * cycleLength;
                    heightOfCycles = cyclesToTheTop * cycleHeight;
                    lookingForCycle = false;
                    return cyclesToTheTop * cycleLength;
                } else {
                    cache.put(cacheKey.getKey(), new State(nbShapesInPit, topOfPile));
                }
            }
            return 0;
        }

        private void updateCacheKey(long min, long max) {
            for (long y0 = Math.max(cacheKey.bottom, min); y0 <= max; y0++) {
                if (y0 >= cacheKey.top) {
                    cacheKey.add(getLine((int) y0));
                } else {
                    cacheKey.set((int) y0 - cacheKey.bottom, getLine((int) y0));
                }
            }
            for (int y = cacheKey.top; y < topOfPile; y++) {
                cacheKey.add(getLine(y));
            }
        }


        public long addShape(Shape shape, WindConditions wind, int startX) {
            int x = startX;

            long y = topOfPile + 3;
            boolean rest = false;
            while (!rest) {
                int windMove = wind.wind();
                if (canMoveTo(shape, x + windMove, y)) {
                    x = x + windMove;
                }
                // fall
                if (canMoveTo(shape, x, y - 1)) {
                    y = y - 1;
                } else {
                    return restAt(new Position(x, y), shape);
                }
            }
            return 0;
        }

        public boolean canMoveTo(Shape shape, int x, long y) {
            return shape.getAllPositions(new Position(x, y)).stream().noneMatch(this::contains);
        }

        public void addShapes(long number, Shape[] shapes, String windConditions, int startX) {
            this.targetNumber = number;
            int nbShapes = shapes.length;
            WindConditions wind = new WindConditions(windConditions);
            for (long i = 0; i < number; i++) {
                Shape shape = shapes[(int) (i % nbShapes)];
                i += addShape(shape, wind , startX);
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Top of pile : ").append(topOfPile).append("\n");
            for (long y = topOfPile; y >= 0; y--) {
                char[] line = new char[width + 3];
                line[0] = '|';
                line[width + 1] = '|';
                line[width + 2] = '\n';
                for (int x = 0; x < width; x++) {
                    Position current = new Position(x, y);
                    line[x + 1] = contains(current) ? '#' : '.';
                }
                sb.append(new String(line));
            }
            sb.append("+-------+\n");

            return sb.toString();
        }
    }

    public void partOne(String kind, String windConditions, int keySize) {
        Pit pit = new Pit(7, keySize);
        //pit.lookingForCycle = false;
        pit.addShapes(2022, Shape.values(), windConditions, 2);
        System.out.println(kind + " Tower Height [part 1]: " + pit.getHeightOfPile());
    }


    public void partTwo(String kind, String windConditions, int keySize) {
        Pit pit = new Pit(7, keySize);
        pit.addShapes(1000000000000L, Shape.values(), windConditions, 2);
        System.out.println(kind + " Tower Height [part 2]: " + pit.getHeightOfPile());
    }


    @Override
    public void run() {
        String testWindConditions = readFile(getTestInputPath());
        partOne("Test", testWindConditions, 32);
        partTwo("Test", testWindConditions, 32);

        String windConditions = readFile(getInputPath());
        partOne("Real", windConditions, 64);
        partTwo("Real", windConditions, 64);
    }
}
