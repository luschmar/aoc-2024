import org.junit.jupiter.params.ParameterizedTest;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day08Test {
    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "14"),
            @AocInputMapping(input = "test1.txt", expected = "2"),
            @AocInputMapping(input = "test2.txt", expected = "4"),
            @AocInputMapping(input = "test3.txt", expected = "4"),
            @AocInputMapping(input = "https://adventofcode.com/2024/day/8/input", expected = "398")
    })
    void part1(Stream<String> input, String expected) {
        var map = input.toList();
        var antennas = IntStream.range(0, map.size()).mapToObj(y -> IntStream.range(0, map.get(y).length()).mapToObj(
                x -> {
                    var c = map.get(y).charAt(x);
                    if (c != '.' && c != '#') {
                        return new AntennaLocation(x, y, c);
                    }
                    return null;
                }
        )).flatMap(s -> s).filter(Objects::nonNull).toList();

        Set<AntinodeLocation> antinodes = new HashSet<>();
        for (var a : antennas) {
            var otherAntennas = antennas.stream().filter(o -> o != a).toList();

            var nodes = otherAntennas.stream()
                    .filter(s -> a.frequency == s.frequency)
                    .map(b -> new AntennaPair(a, b))
                    .map(c -> c.toAntiNodeInMap(map.getFirst().length(), map.size())).filter(Objects::nonNull).collect(Collectors.toSet());
            antinodes.addAll(nodes);
        }

        assertEquals(Integer.parseInt(expected), antinodes.size());
    }

    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "34"),
            @AocInputMapping(input = "test4.txt", expected = "9"),
            @AocInputMapping(input = "https://adventofcode.com/2024/day/8/input", expected = "1333")
    })
    void part2(Stream<String> input, String expected) {
        var map = input.toList();
        var antennas = IntStream.range(0, map.size()).mapToObj(y -> IntStream.range(0, map.get(y).length()).mapToObj(
                x -> {
                    var c = map.get(y).charAt(x);
                    if (c != '.' && c != '#') {
                        return new AntennaLocation(x, y, c);
                    }
                    return null;
                }
        )).flatMap(s -> s).filter(Objects::nonNull).toList();

        Set<AntinodeLocation> antinodes = new HashSet<>();
        for (var a : antennas) {
            var otherAntennas = antennas.stream().filter(o -> o != a).toList();

            var nodes = otherAntennas.stream()
                    .filter(s -> a.frequency == s.frequency)
                    .map(b -> new AntennaPair(a, b))
                    .flatMap(c -> c.toAntiNodesInMap(map.getFirst().length(), map.size()).stream())
                    .filter(Objects::nonNull).collect(Collectors.toSet());
            antinodes.addAll(nodes);
        }

        assertEquals(Integer.parseInt(expected), antinodes.size());
    }


    record AntinodeLocation(int x, int y) {
    }

    record AntennaLocation(int x, int y, char frequency) {

    }

    record AntennaPair(AntennaLocation l1, AntennaLocation l2) {
        AntinodeLocation toAntiNodeInMap(int maxX, int maxY) {
            var aX = l1.x + (l1.x - l2.x);
            var aY = l1.y + (l1.y - l2.y);
            if ((aX >= 0 && aX < maxX) && (aY >= 0 && aY < maxY)) {
                return new AntinodeLocation(aX, aY);
            }
            return null;
        }

        List<AntinodeLocation> toAntiNodesInMap(int maxX, int maxY) {
            var sX = l1.x - l2.x;
            var sY = l1.y - l2.y;
            var gaX = sX / gcdByBruteForce(sX, sY);
            var gaY = sY / gcdByBruteForce(sX, sY);

            var nX = l1.x + gaX;
            var nY = l1.y + gaY;

            var result = new ArrayList<AntinodeLocation>();
            for (int i = 1; (nX >= 0 && nX < maxX) && (nY >= 0 && nY < maxY); i++) {
                nX = l1.x + gaX * i;
                nY = l1.y + gaY * i;
                if ((nX >= 0 && nX < maxX) && (nY >= 0 && nY < maxY)) {
                    result.add(new AntinodeLocation(nX, nY));
                }
            }
            result.add(new AntinodeLocation(l1.x, l1.y));
            return result;
        }
    }

    static int gcdByBruteForce(int n1, int n2) {
        int gcd = 1;
        for (int i = 1; i <= n1 && i <= n2; i++) {
            if (n1 % i == 0 && n2 % i == 0) {
                gcd = i;
            }
        }
        return gcd;
    }
}