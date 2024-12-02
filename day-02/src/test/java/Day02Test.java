import org.junit.jupiter.params.ParameterizedTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day02Test {
    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "2"),
            //@AocInputMapping(input = "input.txt", expected = "379")
    })
    void part1(Stream<String> input, String expected) {
        var reports = input.map(Report::new).toList();

        var safe = reports.stream().filter(Report::isSafe).count();

        assertEquals(Long.parseLong(expected), safe);
    }


    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "4"),
            //@AocInputMapping(input = "input.txt", expected = "430")
    })
    void part2(Stream<String> input, String expected) {
        var reports = input.map(Report::new).toList();

        var safe = reports.stream().filter(Report::isSafe).count();
        var unsafe = reports.stream().filter(r -> !r.isSafe()).toList();
        var repairable = unsafe.stream().filter(Report::problemDampenerSafe).count();

        assertEquals(Long.parseLong(expected), safe + repairable);
    }

    class Report {

        private final List<Integer> levels;
        private final List<Integer> diffs;

        Report(String levelReport) {
            this.levels = Arrays.stream(levelReport.split("\\s+")).map(Integer::parseInt).toList();
            this.diffs = IntStream.range(0, levels.size() - 1).mapToObj(i -> levels.get(i) - levels.get(i + 1)).toList();
        }

        boolean isSafe() {
            if (diffs.stream().anyMatch(i -> i == 0)) {
                return false;
            }

            if (diffs.stream().anyMatch(i -> Math.abs(i) > 3)) {
                return false;
            }

            if (diffs.getFirst() > 0) {
                return diffs.stream().allMatch(a -> a > 0);
            }

            return diffs.stream().allMatch(a -> a < 0);
        }

        boolean problemDampenerSafe() {
            return IntStream.range(0, levels.size()).mapToObj(i -> new Report(removeLevel(i))).anyMatch(Report::isSafe);
        }

        String removeLevel(int i) {
            var copy = new ArrayList<>(levels);
            copy.remove(i);
            return copy.stream().map(Object::toString).collect(Collectors.joining(" "));
        }
    }
}
