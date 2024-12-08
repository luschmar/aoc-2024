import org.junit.jupiter.params.ParameterizedTest;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day08Test {
	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "14"),
			@AocInputMapping(input = "test1.txt", expected = "2"),
			@AocInputMapping(input = "test2.txt", expected = "2"),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/8/input", expected = "-1")
	})
	void part1(Stream<String> input, String expected) {
		var map = input.toList();
		var antennas = IntStream.range(0, map.size()).mapToObj(y ->  IntStream.range(0, map.get(y).length()).mapToObj(
				x -> {
					var c = map.get(y).charAt(x);
					if(c != '.') {
						return new AntennaLocation(x, y, c);
					}
					return null;
				}
		)).flatMap(s -> s).filter(Objects::nonNull).toList();

		long antinodeCount = 0;
		for(var a : antennas){
			var otherAntennas = antennas.stream().filter(o -> o != a).toList();
			antinodeCount += otherAntennas.stream()
					.filter(s -> a.frequency == s.frequency)
					.map(b -> new AntennaPair(a, b))
					.filter(c -> c.countAntinodes(otherAntennas))
					.count();
		}

		assertEquals(Integer.parseInt(expected), antinodeCount);
	}

	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = ""),
			//@AocInputMapping(input = "https://adventofcode.com/2024/day/8/input", expected = "-1")
	})
	void part2(Stream<String> input, String expected) {
		String res = "res";
		assertEquals(expected,res);
	}

	record AntennaLocation(int x, int y, char frequency) {

	}

	record AntennaPair(AntennaLocation l1, AntennaLocation l2) {
		boolean countAntinodes(List<AntennaLocation> otherAntennas) {
			var aX =  l1.x + (l1.x - l2.x);
			var aY = l1.y + (l1.y - l2.y);

			var candidate = otherAntennas.stream().filter(s -> s.x == aX && s.y == aY).findFirst();
			if(candidate.isPresent()) {
				var cand = candidate.get();
				var bX = l2.x + -1*(l1.x - l2.x);
				var bY = l2.y + -1*(l1.y - l2.y);
				var candidate2 = otherAntennas.stream().filter(s -> s.x == bX && s.y == bY).findFirst();
				if(candidate2.isPresent()){
					return cand.frequency == candidate2.get().frequency;
				}

			}
			return false;
		}
	}

	static int gcdByBruteForce(int n1, int n2) {
		int gcd = 1;
		for (int i = 1; i <= n1 && i <= n2; i++) {
			if (n1 % i == 0 && n2 % i == 0) {
				gcd = i;
			}
		}
		return gcd;
	}
}