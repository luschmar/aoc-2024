import org.junit.jupiter.params.ParameterizedTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day15Test {
	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = ""),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/15/input", expected = "-1")
	})
	void part1(Stream<String> input, String expected) {
		String res = "res";
		assertEquals(expected,res);
	}

	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = ""),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/15/input", expected = "-1")
	})
	void part2(Stream<String> input, String expected) {
		String res = "res";
		assertEquals(expected,res);
	}
}
