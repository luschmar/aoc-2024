import org.junit.jupiter.params.ParameterizedTest;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day03Test {
	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "161"),
			@AocInputMapping(input = "input.txt", expected = "180233229")
	})
	void part1(Stream<String> input, String expected) {
		var mul = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");

		var res = input.flatMap(s -> mul.matcher(s).results()).map(a -> Math.multiplyExact(Long.parseLong(a.group(1)), Long.parseLong(a.group(2)))).mapToLong(Long::longValue).sum();

		assertEquals(Long.parseLong(expected), res);
	}

	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = ""),
			@AocInputMapping(input = "input.txt", expected = "")
	})
	void part2(Stream<String> input, String expected) {
		String res = "res";
		assertEquals(expected,res);
	}
}
