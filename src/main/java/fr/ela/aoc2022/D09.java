package fr.ela.aoc2022;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

public class D09 extends AoC {

    public enum Direction {
        U(pos -> new Position(pos.x, pos.y+1)),
        D(pos -> new Position(pos.x, pos.y-1)),
        L(pos -> new Position(pos.x - 1, pos.y)),
        R(pos -> new Position(pos.x + 1, pos.y));

        private final Function<Position, Position> move;

        Direction(Function<Position, Position> move) {
            this.move = move;
        }
    }

    public record Move(Direction d, int amount) {
    }

    public class Knot {
        private Position pos;
        public Knot(Position pos) {
            this.pos = pos;
        }

        public Position move(Direction d) {
            this.pos = d.move.apply(pos);
            return pos;
        }

        public Position follow(Position precedent) {
            this.pos = pos.follow(precedent);
            return pos;
        }

    }

    public record Position(int x, int y) {
        boolean touches(Position other) {
            return Math.abs(x - other.x) < 2 && Math.abs(y - other.y) < 2;
        }

        public Position follow(Position head) {
            if (touches(head)) {
                return this;
            }
            int y = this.y;
            int x = this.x;
            if (head.y != y) {
                y += (head.y - y) / Math.abs(head.y - y);
            }
            if (head.x != x) {
                x += (head.x - x) / Math.abs(head.x - x);
            }
            return new Position(x, y);
        }
    }

    public Move readMove(String line) {
        return new Move(Direction.valueOf(line.substring(0, 1)), Integer.parseInt(line.substring(2)));
    }


    public Set<Position> path(List<Move> moves, int knotsNumber) {
        LinkedList<Knot> knots = new LinkedList<>();
        IntStream.range(0, knotsNumber).mapToObj(i -> new Knot(new Position(0, 0))).forEach(knots::add);

        Knot head = knots.getFirst();
        Knot tail = knots.getLast();

        Set<Position> path = new HashSet<>();
        for (Move move : moves) {
            for (int i = 0; i < move.amount; i++) {
                Position pos = head.move(move.d);
                for (int k = 1; k < knotsNumber; k++) {
                    Knot current = knots.get(k);
                    pos = current.follow(pos);
                }
                path.add(tail.pos);
            }
        }
        return path;
    }


    @Override
    public void run() {
        System.out.println("Test part one : " + path(list(getTestInputPath(), this::readMove), 2).size());
        System.out.println("Real part one : " + path(list(getInputPath(), this::readMove), 2).size());
        System.out.println("Test part two : " + path(list(getTestInputPath(), this::readMove), 10).size());
        System.out.println("Real part two : " + path(list(getInputPath(), this::readMove), 10).size());

    }
}
