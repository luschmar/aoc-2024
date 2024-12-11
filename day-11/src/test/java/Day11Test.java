import org.junit.jupiter.params.ParameterizedTest;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day11Test {
	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "55312"),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/11/input", expected = "186203")
	})
	void part1(Stream<String> input, String expected) {
		Map<Long, Long> original = Arrays.stream(input.findFirst().get().split("\\s"))
				.map(Long::parseLong).collect(toMap(Long::longValue, v -> 1L));

		Map<Long, Long> stones = new TreeMap<>(original);
		for (int i = 0; i < 25; i++) {
			var afterBlink = new TreeMap<Long, Long>();
			for (var l : new HashSet<>(stones.entrySet())) {
				if (l.getKey() == 0L) {
					var l0 = afterBlink.getOrDefault(1L, 0L);
					afterBlink.put(1L, l.getValue() + l0);
				} else {
					var lStr = String.valueOf(l.getKey());
					if (lStr.length() % 2 == 0) {
						var l1 = Long.parseLong(lStr.substring(0, lStr.length() / 2));
						var l1Existing = afterBlink.getOrDefault(l1, 0L);
						afterBlink.put(l1, l1Existing + l.getValue());

						var l2 = Long.parseLong(lStr.substring(lStr.length() / 2));
						var l2Existing = afterBlink.getOrDefault(l2, 0L);
						afterBlink.put(l2, l2Existing + l.getValue());
					} else {
						var newL = Math.multiplyExact(l.getKey(), 2024L);
						var lExisting = afterBlink.getOrDefault(newL, 0L);
						afterBlink.put(newL, lExisting + l.getValue());
					}
				}
			}
			stones = afterBlink;
		}


		assertEquals(Long.parseLong(expected), stones.values().stream().mapToLong(Long::longValue).sum());
	}

	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "https://adventofcode.com/2024/day/11/input", expected = "221291560078593")
	})
	void part2(Stream<String> input, String expected) {
		Map<Long, Long> original = Arrays.stream(input.findFirst().get().split("\\s"))
				.map(Long::parseLong).collect(toMap(Long::longValue, v -> 1L));

		Map<Long, Long> stones = new TreeMap<>(original);
		for (int i = 0; i < 75; i++) {
			var afterBlink = new TreeMap<Long, Long>();
			for (var l : new HashSet<>(stones.entrySet())) {
				if (l.getKey() == 0L) {
					var l0 = afterBlink.getOrDefault(1L, 0L);
					afterBlink.put(1L, l.getValue() + l0);
				} else {
					var lStr = String.valueOf(l.getKey());
					if (lStr.length() % 2 == 0) {
						var l1 = Long.parseLong(lStr.substring(0, lStr.length() / 2));
						var l1Existing = afterBlink.getOrDefault(l1, 0L);
						afterBlink.put(l1, l1Existing + l.getValue());

						var l2 = Long.parseLong(lStr.substring(lStr.length() / 2));
						var l2Existing = afterBlink.getOrDefault(l2, 0L);
						afterBlink.put(l2, l2Existing + l.getValue());
					} else {
						var newL = Math.multiplyExact(l.getKey(), 2024L);
						var lExisting = afterBlink.getOrDefault(newL, 0L);
						afterBlink.put(newL, lExisting + l.getValue());
					}
				}
			}
			stones = afterBlink;
		}


		assertEquals(Long.parseLong(expected), stones.values().stream().mapToLong(Long::longValue).sum());
	}
}
