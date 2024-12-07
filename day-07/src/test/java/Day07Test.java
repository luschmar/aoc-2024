import org.junit.jupiter.params.ParameterizedTest;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day07Test {


    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "3749"),
            @AocInputMapping(input = "https://adventofcode.com/2024/day/7/input", expected = "14711933466277")
    })
    void part1(Stream<String> input, String expected) {
        var res = input.map(Equation::new).filter(Equation::valid).mapToLong(Equation::getResult).sum();
        assertEquals(Long.parseLong(expected), res);
    }

    class Equation {
        final long result;
        final List<Long> numbers;

        Equation(String e) {
            result = Long.parseLong(e.split(":")[0]);
            numbers = Stream.of(e.split(":")[1].trim().split("\\s")).map(Long::parseLong).toList();
        }

        boolean valid() {
            return IntStream.range(0, (int) Math.pow(2, numbers.size() - 1))
                    .parallel()
                    .mapToObj(i -> String.format("%" + (numbers.size() - 1) + "s",
                            Integer.toBinaryString(i)).replaceAll("0", "0")).map(s -> {
                        long res = numbers.getFirst();
                        for (int i = 0; i < s.length(); i++) {
                            if (s.charAt(i) == '1') {
                                res *= numbers.get(i + 1);
                            } else {
                                res += numbers.get(i + 1);
                            }
                            // Overshoot the number; and we will quit.
                            if(res > result) {
                                return 0L;
                            }
                        }
                        return res;
                    }).anyMatch(l -> l == result);
        }

        boolean valid2() {
            return IntStream.range(0, (int) Math.pow(3, numbers.size() - 1))
                    .parallel()
                    .mapToObj(i -> String.format("%" + (numbers.size() - 1) + "s",
                            Integer.toUnsignedString(i, 3)).replaceAll("\\s", "0")).map(s -> {
                        long res = numbers.getFirst();
                        for (int i = 0; i < s.length(); i++) {
                            if (s.charAt(i) == '0') {
                                res *= numbers.get(i + 1);
                            } else if (s.charAt(i) == '1') {
                                res += numbers.get(i + 1);
                            } else {
                                res = Long.parseLong(res + "" + numbers.get(i + 1));
                            }
                            // Overshoot the number; and we will quit.
                            if(res > result) {
                                return 0L;
                            }
                        }
                        return res;
                    }).anyMatch(l -> l == result);
        }

        long getResult() {
            return result;
        }
    }

    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "11387"),
            @AocInputMapping(input = "https://adventofcode.com/2024/day/7/input", expected = "286580387663654")
    })
    void part2(Stream<String> input, String expected) {
        var res = input.map(Equation::new).filter(Equation::valid2).mapToLong(Equation::getResult).sum();
        assertEquals(Long.parseLong(expected), res);
    }
}
