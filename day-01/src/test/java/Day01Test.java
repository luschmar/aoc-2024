import org.junit.jupiter.params.ParameterizedTest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Long.parseLong;
import static java.util.Objects.requireNonNullElse;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day01Test {
    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "11"),
            //@AocInputMapping(input = "input.txt", expected = "1603498")
    })
    void part1(Stream<String> input, String expected) {
        var lists = input.map(s -> s.split("\\s+")).collect(teeing(
                mapping(a -> parseLong(a[0]), collectingAndThen(toList(), l -> l.stream().sorted().toList())),
                mapping(b -> parseLong(b[1]), collectingAndThen(toList(), l -> l.stream().sorted().toList())),
                List::of));

        var left = lists.get(0);
        var right = lists.get(1);

        var res = IntStream.range(0, left.size()).mapToLong(i -> Math.abs(right.get(i) - left.get(i))).sum();

        assertEquals(parseLong(expected), res);
    }

    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "31"),
            //@AocInputMapping(input = "input.txt", expected = "25574739")
    })
    void part2(Stream<String> input, String expected) {
        var lists = input.map(s -> s.split("\\s+")).collect(teeing(
                mapping(a -> parseLong(a[0]), collectingAndThen(toList(), l -> l.stream().toList())),
                mapping(b -> parseLong(b[1]), collectingAndThen(toList(), l -> l.stream().collect(Collectors.groupingBy(
                        identity(),
                        counting())))), List::of));

        var left = (List<Long>) lists.get(0);
        var right = (Map<Long, Long>) lists.get(1);


        var res = left.stream().mapToLong(l -> requireNonNullElse(right.get(l), 0L) * l).sum();

        assertEquals(Integer.parseInt(expected), res);
    }
}
