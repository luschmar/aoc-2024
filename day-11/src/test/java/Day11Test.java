import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day11Test {
    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "55312"),
            @AocInputMapping(input = "https://adventofcode.com/2024/day/11/input", expected = "186203")
    })
    void part1(Stream<String> input, String expected) {
        List<Long> stones = new ArrayList<>(Arrays.stream(input.findFirst().get().split("\\s")).map(Long::parseLong).toList());

        for (int i = 0; i < 25; i++) {
            stones = blink(stones);
        }

        assertEquals(Long.parseLong(expected), stones.size());
    }

    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "2"),
            @AocInputMapping(input = "https://adventofcode.com/2024/day/11/input", expected = "-1")
    })
    void part2(Stream<String> input, String expected) {
        List<Stone> stones = new ArrayList<>(Arrays.stream(input.findFirst().get().split("\\s")).map(s -> new Stone(Long.parseLong(s), Long.parseLong(s), 0)).toList());

		List<Stone>  thirtyFive = null;
        for (int i = 0; i < 40; i++) {
			stones = blinkStones(stones);
			if(i == 35){
				thirtyFive = stones;
			}
        }

        assertEquals(Long.parseLong(expected), stones.size());
    }

    @Test
    void blinkTester() {
        var a = Arrays.stream("0 1 10 99 999".split("\\s")).map(Long::parseLong).toList();

        System.out.println(a.size());
    }


	static class Stone {
		final static Stone ONE = new Stone(0L, 1L, 0);

		Long base;
		Long value;
		String valueStr;
		int gen;

		Stone(long base, long value, int gen){
			this.base = base;
			this.value = value;
			this.valueStr = Long.toString(value);
			this.gen = gen;
		}

		List<Stone> blink() {
			if(base == 0){
				return List.of(ONE);
			}
			if (valueStr.length() % 2 == 0) {
				var str1 = valueStr.substring(0, valueStr.length() / 2);
				var str2 = valueStr.substring(valueStr.length() / 2);

				return List.of(new Stone(base, Long.parseLong(str1), gen+1), new Stone(base, Long.parseLong(str2), gen+1));
			}
			return List.of(new Stone(base, Math.multiplyExact(value, 2024), gen+1));
		}
	}
	private List<Stone> blinkStones(List<Stone> stones) {
		var newStones = new ArrayList<Stone>();
		for (int i = 0; i < stones.size(); i++) {
			var l = stones.get(i);
			newStones.addAll(l.blink());
		}
		return newStones;
	}
    private List<Long> blink(List<Long> stones) {
        var newStones = new ArrayList<Long>();
        for (int i = 0; i < stones.size(); i++) {
            var l = stones.get(i);
            if (l == 0) {
                newStones.add(1L);
            } else {
                var str = l.toString();
                if (str.length() % 2 == 0) {
                    var str1 = str.substring(0, str.length() / 2);
                    var str2 = str.substring(str.length() / 2, str.length());

                    newStones.add(Long.parseLong(str1));
                    newStones.add(Long.parseLong(str2));
                } else {
                    newStones.add(Math.multiplyExact(l, 2024));
                }
            }
        }
        return newStones;
    }
}
