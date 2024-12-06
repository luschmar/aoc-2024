import org.junit.jupiter.params.ParameterizedTest;

import java.nio.CharBuffer;
import java.security.Guard;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day06Test {
	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "41"),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/6/input", expected = "4665")
	})
	void part1(Stream<String> input, String expected) {
		var map = input.map(String::toCharArray).toArray(char[][]::new);

		var startY = IntStream.range(0, map.length).filter(y -> new String(map[y]).contains("^")).findFirst().getAsInt();
		var startX = new String(map[startY]).indexOf("^");

		var g = new Guard(map, startX, startY);
		while(g.move()) {
		}

		g.printMap();
		assertEquals(Integer.parseInt(expected),g.countVisited());
	}

	enum Dir {
		UP,RIGHT,DOWN,LEFT
	}

	class Guard {
		private final char[][] map;
		private int currentX;
		private int currentY;
		private Dir currentDir;
		private int moves;
		private boolean isLooped;

		Guard(char[][] map, int startX, int startY) {
			this.map = map;
			this.currentX = startX;
			this.currentY = startY;
			this.currentDir = Dir.UP;
		}

		void printMap() {
			for (char[] line : map) {
				System.out.println(new String(line));
			}
			}

		long countVisited() {
			return Arrays.stream(map).flatMapToInt(c -> CharBuffer.wrap(c).chars()).filter(i -> i == 'X').count();
		}

		boolean move() {
			moves++;
			var nextX = getNextX();
			var nextY = getNextY();
			if(checkOutside(nextX, nextY)) {
				map[currentY][currentX] = 'X';
				return false;
			}

			if(canMove(nextX, nextY)) {
				map[currentY][currentX] = 'X';
				currentX = nextX;
				currentY = nextY;
				return true;
			}
			currentDir = Dir.values()[(currentDir.ordinal()+1)%4];

			return true;

		}

		private boolean canMove(int nextX, int nextY) {
			return map[nextY][nextX] != '#';
		}

		private boolean checkOutside(int nextX, int nextY) {
			if(nextX < 0 || nextX > map[0].length-1) {
				return true;
			}
			if(nextY < 0 || nextY > map.length-1) {
				return true;
			}
			if(moves > map[0].length*map.length) {
				isLooped = true;
				return true;
			}
			return false;
		}

		private int getNextX() {
			return switch (currentDir) {
				case UP, DOWN -> currentX;
				case LEFT -> currentX-1;
				case RIGHT -> currentX+1;
			};
		}

		private int getNextY() {
			return switch (currentDir) {
				case LEFT, RIGHT -> currentY;
				case UP -> currentY-1;
				case DOWN -> currentY+1;
			};
		}
	}

	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "6"),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/6/input", expected = "1688")
	})
	void part2(Stream<String> input, String expected) {
		var map = input.map(String::toCharArray).toArray(char[][]::new);

		var startY = IntStream.range(0, map.length).filter(y -> new String(map[y]).contains("^")).findFirst().getAsInt();
		var startX = new String(map[startY]).indexOf("^");

		int loopCount = 0;
		for(int y = 0; y < map.length; y++) {
			for(int x = 0; x < map[y].length; x++) {
				var copy = copyMap(map);
				copy[y][x] = '#';
				var g = new Guard(copy, startX, startY);
				while(g.move()) {
				}
				if(g.isLooped) {
					loopCount++;
				}
			}
		}
		assertEquals(Integer.parseInt(expected),loopCount);
	}

	private char[][] copyMap(char[][] map) {
		char[][] copy = new char[map.length][map[0].length];
		for(int y = 0; y < map.length; y++) {
			System.arraycopy(map[y], 0, copy[y], 0, map[y].length);
		}
		return copy;
	}
}
