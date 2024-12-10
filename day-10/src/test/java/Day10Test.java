import org.junit.jupiter.params.ParameterizedTest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day10Test {
    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "36"),
            @AocInputMapping(input = "https://adventofcode.com/2024/day/10/input", expected = "472")
    })
    void part1(Stream<String> input, String expected) {
        var map = input.map(i -> i.chars().map(a -> a - '0').toArray()).toArray(int[][]::new);

        var possiblePaths = IntStream.range(0, (int) Math.pow(4, 9))
                .mapToObj(i -> String.format("%9s", Integer.toUnsignedString(i, 4)).replaceAll(" ", "0"))
                .filter(s -> !s.contains("02") &&
                        !s.contains("20") &&
                        !s.contains("13") &&
                        !s.contains("31"))
                .map(s -> s.chars().mapToObj(i -> Dir.values()[i - '0']).toList())
                .toList();

        List<Pos> startPoints = IntStream.range(0, map.length).mapToObj(y -> IntStream.range(0, map[y].length).mapToObj(x -> {
            if (map[y][x] == 0) {
                return new Pos(x, y);
            }
            return null;
        }).filter(Objects::nonNull)).flatMap(f -> f).toList();

        var sum = startPoints.stream()
                .mapToInt(s -> possiblePaths.stream().map(p -> new Hike(map, s, p))
                        .filter(h -> h.destinationValue() == 9).map(h -> h.end).collect(Collectors.toSet()).size()).sum();

        assertEquals(Integer.parseInt(expected), sum);
    }

    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "36")
    }
    )
    void debug(Stream<String> input, String expected) {
        var map = input.map(i -> i.chars().map(a -> a - '0').toArray()).toArray(int[][]::new);

        var h = new Hike(map, new Pos(4, 2), List.of(Dir.NORTH, Dir.WEST, Dir.SOUTH, Dir.SOUTH, Dir.WEST, Dir.WEST, Dir.NORTH, Dir.WEST, Dir.SOUTH));

        System.out.println(h.destinationValue());
    }

    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "81"),
            @AocInputMapping(input = "https://adventofcode.com/2024/day/10/input", expected = "969")
    })
    void part2(Stream<String> input, String expected) {
        var map = input.map(i -> i.chars().map(a -> a - '0').toArray()).toArray(int[][]::new);

        var possiblePaths = IntStream.range(0, (int) Math.pow(4, 9))
                .mapToObj(i -> String.format("%9s", Integer.toUnsignedString(i, 4)).replaceAll(" ", "0"))
                .filter(s -> !s.contains("02") &&
                        !s.contains("20") &&
                        !s.contains("13") &&
                        !s.contains("31"))
                .map(s -> s.chars().mapToObj(i -> Dir.values()[i - '0']).toList())
                .toList();

        List<Pos> startPoints = IntStream.range(0, map.length).mapToObj(y -> IntStream.range(0, map[y].length).mapToObj(x -> {
            if (map[y][x] == 0) {
                return new Pos(x, y);
            }
            return null;
        }).filter(Objects::nonNull)).flatMap(f -> f).toList();

        var sum = startPoints.stream()
                .mapToInt(s -> possiblePaths.stream().map(p -> new Hike(map, s, p))
                        .filter(h -> h.destinationValue() == 9).map(h -> h.end).toList().size()).sum();

        assertEquals(Integer.parseInt(expected), sum);
    }

    class Hike {
        private final int[][] map;
        private final Pos start;
        private final Pos end;
        private final List<Dir> path;

        Hike(int[][] map, Pos start, List<Dir> path) {
            this.map = map;
            this.start = start;
            this.path = path;
            this.end = hike();
        }

        private Pos hike() {
            var currentPos = start;
            var height = 0;
            for (var p : path) {
                var nextPos = currentPos.move(p);
                if (nextPos.y() < 0 || nextPos.y() >= map.length) {
                    return start;
                }
                if (nextPos.x() < 0 || nextPos.x() >= map[nextPos.y()].length) {
                    return start;
                }
                if (map[nextPos.y()][nextPos.x()] != height + 1) {
                    return start;
                }
                height++;

                currentPos = nextPos;
            }
            return currentPos;
        }

        int destinationValue() {
            return map[end.y()][end.x()];
        }
    }

    record Pos(int x, int y) {
        Pos move(Dir d) {
            return new Pos(x() + d.x, y() + d.y);
        }
    }

    enum Dir {
        NORTH(0, -1),
        EAST(1, 0),
        SOUTH(0, 1),
        WEST(-1, 0);

        int x;
        int y;

        Dir(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
