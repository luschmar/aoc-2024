import org.junit.jupiter.params.ParameterizedTest;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day09Test {
    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "1928"),
            @AocInputMapping(input = "test1.txt", expected = "60"),
            @AocInputMapping(input = "https://adventofcode.com/2024/day/9/input", expected = "6367087064415")
    })
    void part1(Stream<String> input, String expected) {
        var inStr = input.toList().getFirst();
        var disk = IntStream.range(0, inStr.length()).flatMap(i -> {
            int a = inStr.charAt(i) - 48;
            if (i % 2 == 0) {
                return IntStream.range(0, a).map(m -> (i / 2));
            }
            return IntStream.range(0, a).map(m -> -1);
        }).toArray();

        for (int i = 0; i < disk.length; i++) {
            var right = disk.length - i - 1;
            var left = IntStream.range(0, disk.length).filter(f -> disk[f] == -1).findFirst().getAsInt();
            if (right <= left) {
                continue;
            }
            if (disk[right] >= 0) {
                disk[left] = disk[right];
                disk[right] = -1;
            }
        }

        var res = IntStream.range(0, disk.length).mapToLong(i -> disk[i] == -1 ? 0 : disk[i] * i).sum();

        assertEquals(Long.parseLong(expected), res);
    }

    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "2858"),
            @AocInputMapping(input = "https://adventofcode.com/2024/day/9/input", expected = "-1")
    })
    void part2(Stream<String> input, String expected) {
        var inStr = input.toList().getFirst();
        var disk = IntStream.range(0, inStr.length()).flatMap(i -> {
            int a = inStr.charAt(i) - 48;
            if (i % 2 == 0) {
                return IntStream.range(0, a).map(m -> (i / 2));
            }
            return IntStream.range(0, a).map(m -> -1);
        }).toArray();

        for (int right = disk.length - 1; right >= 0; right--) {
			if(disk[right] == -1){
				continue;
			}
            var r = right;
            var right2 = IntStream.range(0, disk.length).filter(f -> disk[f] == disk[r]).findFirst().getAsInt();
            var blocksize = right - right2 + 1;

            var left = IntStream.range(0, disk.length).filter(f -> IntStream.range(f, f + blocksize).allMatch(b -> {
                                if (b >= disk.length) {
                                    return false;
                                }
                                return disk[b] == -1;
                            }
                    )
            ).findFirst();

            if (left.isEmpty()) {
                right = right2;
                continue;
            }
            var l = left.getAsInt();
			if(l >= right2){
				right = right2;
				continue;
			}
            IntStream.range(l, l + blocksize).forEach(i -> {
                disk[i] = disk[r];
            });
            IntStream.range(right2, right2 + blocksize).forEach(i -> {
                disk[i] = -1;
            });
            right = right2;
        }

        var res = IntStream.range(0, disk.length).mapToLong(i -> disk[i] == -1 ? 0 : disk[i] * i).sum();

        assertEquals(Long.parseLong(expected), res);
    }
}
