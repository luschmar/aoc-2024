import org.junit.jupiter.params.ParameterizedTest;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.Long.parseLong;
import static java.lang.Math.multiplyExact;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day03Test {
	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "161"),
			@AocInputMapping(input = "input.txt", expected = "180233229")
	})
	void part1(Stream<String> input, String expected) {
		var mul = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");

		var res = input.map(mul::matcher).flatMap(Matcher::results)
				.mapToLong(a -> multiplyExact(parseLong(a.group(1)), parseLong(a.group(2))))
				.sum();

		assertEquals(parseLong(expected), res);
	}

	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test2.txt", expected = "48"),
			@AocInputMapping(input = "input.txt", expected = "95411583")
	})
	void part2(Stream<String> input, String expected) {
		var mul = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)|don't\\(\\)|do\\(\\)");

		var on = new AtomicBoolean(true);
		var res = input.map(mul::matcher).flatMap(Matcher::results).mapToLong(a ->
				{
					var t = a.group(0);
					if (t.startsWith("do(")) {
						on.set(true);
						return 0;
					}
					if (t.startsWith("don't")) {
						on.set(false);
						return 0;
					}
					if (on.get()) {
						return multiplyExact(parseLong(a.group(1)), parseLong(a.group(2)));
					}
					return 0;
				}
		).sum();

		assertEquals(parseLong(expected), res);
	}
}
