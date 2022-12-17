package fr.ela.aoc2022;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class D17 extends AoC {

    /*####

            .#.
            ###
            .#.

            ..#
            ..#
            ###

            #
            #
            #
            #

            ##
            ##

     */
    record Position(int x, int y) {
    }

    record WindConditions(String conditions) {
        int wind(int time) {
            return conditions.charAt(time % conditions.length()) == '>' ? 1 : -1;
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

        boolean canMoveSideways(int x, int width) {
            return x >= 0 && x + this.width <= width;
        }


    }

    public class Pit {
        final Set<Position> positions;
        final int width;
        int topOfPile = 0;

        public Pit(int width) {
            positions = new HashSet<>();
            this.width = width;
        }

        public boolean contains(Position pos) {
            return pos.y < 0 || pos.x < 0 || pos.x >= width || positions.contains(pos);
        }

        public void add(Position position) {
            positions.add(position);
            topOfPile = Math.max(position.y + 1, topOfPile);
        }

        public void restAt(Position position, Shape shape) {
            shape.getAllPositions(position).forEach(this::add);
        }


        public int addShape(Shape shape, int startTime, WindConditions wind, int startX) {
            int x = startX;
            int time = startTime;

            int y = topOfPile + 3;
            boolean rest = false;
            while (!rest) {
                int windMove = wind.wind(time);
                if (canMoveTo(shape, x + windMove, y)) {
                    x = x + windMove;
                }
                // fall
                if (canMoveTo(shape, x, y - 1)) {
                    y = y - 1;
                } else {
                   // System.out.println(toString(shape, new Position(x, y)));
                    restAt(new Position(x, y), shape);
                    rest = true;
                }
                time++;
            }
            return time;
        }

        public boolean canMoveTo(Shape shape, int x, int y) {
            return shape.getAllPositions(new Position(x, y)).stream().noneMatch(this::contains);
        }

        public void addShapes(int number, Shape[] shapes, String windConditions, int startX) {
            int nbShapes = shapes.length;
            int time = 0;
            for (int i = 0; i < number; i++) {
                Shape shape = shapes[i % nbShapes];
                time = addShape(shape, time, new WindConditions(windConditions), startX);
            }
        }

        public String toString(Shape shape, Position bottomLeft) {
            StringBuilder sb = new StringBuilder();
            sb.append("Top of pile : ").append(topOfPile).append("\n");
            List<Position> positions = shape.getAllPositions(bottomLeft);
            for (int y = topOfPile + 3 + shape.height; y >= 0; y--) {
                char[] line = new char[width + 3];
                line[0] = '|';
                line[width + 1] = '|';
                line[width + 2] = '\n';
                for (int x = 0; x < width; x++) {
                    Position current = new Position(x, y);
                    line[x + 1] = contains(current) ? '#' : '.';
                    if (positions.contains(current)) {
                        line[x + 1] = '@';
                    }
                }
                sb.append(new String(line));
            }
            sb.append("+-------+\n");
            return sb.toString();
        }
    }


    @Override
    public void run() {
        String testWindConditions = readFile(getTestInputPath());
        Pit testPit = new Pit(7);
        testPit.addShapes(2022, Shape.values(), testWindConditions, 2);
        System.out.println("Test Tower Height : " + testPit.topOfPile);

        String windConditions = readFile(getInputPath());
        Pit pit = new Pit(7);
        pit.addShapes(2022, Shape.values(), windConditions, 2);
        System.out.println("Real Tower Height : " + pit.topOfPile);
    }
}
