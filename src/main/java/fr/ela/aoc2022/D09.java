package fr.ela.aoc2022;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

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
    public record Position(int x, int y) {
        boolean touches(Position other) {
            return Math.abs(x - other.x) < 2 && Math.abs(y - other.y) < 2;
        }
    }

    public Move readMove(String line) {
        return new Move(Direction.valueOf(line.substring(0, 1)), Integer.parseInt(line.substring(2)));
    }

    public Position follow(Position head, Position tail) {
        if (head.touches(tail)) {
            return tail;
        }
        int y = tail.y;
        int x = tail.x;
        if (head.y != tail.y) {
            y += (head.y - tail.y) / Math.abs(head.y - tail.y);
        }
        if (head.x != tail.x) {
            x += (head.x - tail.x) / Math.abs(head.x - tail.x);
        }
        return new Position(x, y);
    }

    public Set<Position> path(List<Move> moves) {
        Position head = new Position(0, 0);
        Position tail = new Position(0, 0);
        Set<Position> path = new HashSet<>();
        for (Move move : moves) {
            for (int i = 0; i < move.amount; i++) {
                head = move.d.move.apply(head);
                tail = follow(head, tail);
                path.add(tail);
            }
        }
        return path;
    }


    @Override
    public void run() {
        System.out.println("Test part one : " + path(list(getTestInputPath(), this::readMove)).size());
        System.out.println("Real part one : " + path(list(getInputPath(), this::readMove)).size());

    }
}
