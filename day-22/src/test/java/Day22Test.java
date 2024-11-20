import org.junit.jupiter.params.ParameterizedTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day22Test {
	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = ""),
			@AocInputMapping(input = "input.txt", expected = "")
	})
	void part1(Stream<String> input, String expected) {
		String res = "res";
		assertEquals(expected, res);
	}

	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = ""),
			@AocInputMapping(input = "input.txt", expected = "")
	})
	void part2(Stream<String> input, String expected) {
		String res = "res";
		assertEquals(expected, res);
	}
}
