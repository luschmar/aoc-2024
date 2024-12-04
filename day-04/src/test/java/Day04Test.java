import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day04Test {
	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "18"),
			@AocInputMapping(input = "test2.txt", expected = "18"),
			@AocInputMapping(input = "test3.txt", expected = "4"),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/4/input", expected = "2554")
	})
	void part1(Stream<String> input, String expected) {
		var dataList = input.toList();
		var dataString = String.join("", dataList);

		var width = dataList.get(0).length();

		var res = IntStream.range(0, dataString.length()).map(i -> countXmas(i, dataString, width)).sum();

		assertEquals(Integer.parseInt(expected),res);
	}

	private int countXmas(int i, String dataString, int width) {
		if(dataString.charAt(i) != 'X') {
			return 0;
		}
		var height = dataString.length()/width;

		var coordX = i % width;
		var coordY = i / width;
		var validCoords = new ArrayList<Integer>();
		for(int x = -3; x < 4; x++) {
			for(int y = -3; y < 4; y++) {
				int newX = x + coordX;
				int nexY = y + coordY;
				if(newX >= 0 && newX < width &&
						nexY >= 0 && nexY < height) {
					validCoords.add(nexY*width + newX%width);
				}
			}
		}

		var res = 0;

		var mR = IntStream.range(i, i+4)
				.filter(validCoords::contains)
				.mapToObj(dataString::codePointAt)
				.collect(StringBuilder::new,
						StringBuilder::appendCodePoint, StringBuilder::append).toString();
		if(mR.equals("XMAS")) {
			res++;
		}

		var mL = IntStream.rangeClosed(i-3, i)
				.filter(validCoords::contains)
				.mapToObj(dataString::codePointAt)
				.collect(StringBuilder::new,
						StringBuilder::appendCodePoint, StringBuilder::append)
				.reverse().toString();
		if(mL.equals("XMAS")) {
			res++;
		}

		var tL = IntStream.range(0, 4).map(k -> i-(k*(width+1)))
				.filter(validCoords::contains)
				.mapToObj(dataString::codePointAt)
				.collect(StringBuilder::new,
						StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		if(tL.equals("XMAS")) {
			res++;
		}

		var bL = IntStream.range(0, 4).map(k -> i+(k*(width-1)))
				.filter(validCoords::contains)
				.mapToObj(dataString::codePointAt)
				.collect(StringBuilder::new,
						StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		if(bL.equals("XMAS")) {
			res++;
		}

		var bM = IntStream.range(0, 4).map(k -> i+(k*width))
				.filter(validCoords::contains)
				.mapToObj(dataString::codePointAt)
				.collect(StringBuilder::new,
						StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		if(bM.equals("XMAS")) {
			res++;
		}

		var tR = IntStream.range(0, 4).map(k -> i-(k*(width-1)))
				.filter(validCoords::contains)
				.mapToObj(dataString::codePointAt)
				.collect(StringBuilder::new,
						StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		if(tR.equals("XMAS")) {
			res++;
		}

		var bR = IntStream.range(0, 4).map(k -> i+(k*(width+1)))
				.filter(validCoords::contains)
				.mapToObj(dataString::codePointAt)
				.collect(StringBuilder::new,
						StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		if(bR.equals("XMAS")) {
			res++;
		}

		var tM = IntStream.range(0, 4).map(k -> i-(k*width))
				.filter(validCoords::contains)
				.mapToObj(dataString::codePointAt)
				.collect(StringBuilder::new,
						StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		if(tM.equals("XMAS")) {
			res++;
		}
		return res;
	}

	@Test
	void  getTl() {

		var data = """
			1234567890
			ABCDEFGHIJ
			KLMNO.QRST
			MSAMASMSMX
			XMASAMXAMM
			XXAMMXXAMA
			SMSMSASXSS
			SAXAMASTAM
			HAMMMXMMGG
			_XMXAXMLGX""".replaceAll("\\s", "");

		countXmas(0, data, 10);

	}

	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "9"),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/4/input", expected = "1916")
	})
	void part2(Stream<String> input, String expected) {
		var dataList = input.toList();
		var dataString = String.join("", dataList);

		var width = dataList.get(0).length();

		var res = IntStream.range(0, dataString.length()).map(i -> countXmasPart2(i, dataString, width)).sum();

		assertEquals(Integer.parseInt(expected),res);
	}

	private int countXmasPart2(int i, String dataString, int width) {
		var height = dataString.length()/width;

		var coordX = i % width;
		var coordY = i / width;
		var validCoords = new ArrayList<Integer>();
		for(int x = -1; x < 2; x++) {
			for(int y = -1; y < 2; y++) {
				int newX = x + coordX;
				int nexY = y + coordY;
				if(newX >= 0 && newX < width &&
						nexY >= 0 && nexY < height) {
					validCoords.add(nexY*width + newX%width);
				}
			}
		}
		if(validCoords.size() < 9) {
			return 0;
		}

		var res = 0;

		var tL = IntStream.range(-1, 3).map(k -> i-(k*(width+1)))
				.filter(validCoords::contains)
				.mapToObj(dataString::codePointAt)
				.collect(StringBuilder::new,
						StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		if(tL.equals("MAS")) {
			res++;
		}

		var bL = IntStream.range(-1, 3).map(k -> i+(k*(width-1)))
				.filter(validCoords::contains)
				.mapToObj(dataString::codePointAt)
				.collect(StringBuilder::new,
						StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		if(bL.equals("MAS")) {
			res++;
		}

		var tR = IntStream.range(-1, 3).map(k -> i-(k*(width-1)))
				.filter(validCoords::contains)
				.mapToObj(dataString::codePointAt)
				.collect(StringBuilder::new,
						StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		if(tR.equals("MAS")) {
			res++;
		}

		var bR = IntStream.range(-1, 3).map(k -> i+(k*(width+1)))
				.filter(validCoords::contains)
				.mapToObj(dataString::codePointAt)
				.collect(StringBuilder::new,
						StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		if(bR.equals("MAS")) {
			res++;
		}

		return res == 2 ? 1 : 0;
	}
}
