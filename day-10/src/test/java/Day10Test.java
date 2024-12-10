import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day10Test {
	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "36"),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/10/input", expected = "472")
	})
	void part1(Stream<String> input, String expected) {
		var map = input.map(i -> i.chars().map(a -> a - '0').toArray()).toArray(int[][]::new);

		var possiblePaths = generateAllPossiblePaths();
		var startPoints = findStartPoints(map);

		var sum = startPoints.stream()
				.mapToInt(s -> possiblePaths.stream().map(p -> new Hike(map, s, p))
						.filter(Hike::isLongHike).map(h -> h.end).collect(toSet()).size()).sum();

		assertEquals(Integer.parseInt(expected), sum);
	}

	private List<Pos> findStartPoints(int[][] map) {
		return IntStream.range(0, map.length).mapToObj(y -> IntStream.range(0, map[y].length).mapToObj(x -> {
			if (map[y][x] == 0) {
				return new Pos(x, y);
			}
			return null;
		}).filter(Objects::nonNull)).flatMap(f -> f).toList();
	}

	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "36")
	}
	)
	void debug(Stream<String> input, String expected) {
		var map = input.map(i -> i.chars().map(a -> a - '0').toArray()).toArray(int[][]::new);

		var h = new Hike(map, new Pos(4, 2), List.of(Dir.NORTH, Dir.WEST, Dir.SOUTH, Dir.SOUTH, Dir.WEST, Dir.WEST, Dir.NORTH, Dir.WEST, Dir.SOUTH));

		System.out.println(h.isLongHike());
	}

	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "81"),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/10/input", expected = "969")
	})
	void part2(Stream<String> input, String expected) {
		var map = input.map(i -> i.chars().map(a -> a - '0').toArray()).toArray(int[][]::new);

		var possiblePaths = generateAllPossiblePaths();
		var startPoints = findStartPoints(map);

		var sum = startPoints.stream()
				.mapToLong(s -> possiblePaths.stream().map(p -> new Hike(map, s, p))
						.filter(Hike::isLongHike).count()).sum();

		assertEquals(Integer.parseInt(expected), sum);
	}

	private Pos getEnd(List<Dir> dirs) {
		return new Pos(dirs.stream().mapToInt(d -> d.x).sum(),dirs.stream().mapToInt(d -> d.y).sum());
	}

    private List<List<Dir>> generateAllPossiblePaths() {
		return IntStream.range(0, (int) Math.pow(4, 9))
				.mapToObj(i -> String.format("%9s", Integer.toUnsignedString(i, 4)).replaceAll(" ", "0"))
				.filter(s -> !s.contains("02") &&
						!s.contains("20") &&
						!s.contains("13") &&
						!s.contains("31"))
				.map(s -> s.chars().mapToObj(i -> Dir.values()[i - '0']).toList())
				.toList();
	}

	enum Dir {
		NORTH(0, -1),
		EAST(1, 0),
		SOUTH(0, 1),
		WEST(-1, 0);

		final int x;
		final int y;

		Dir(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	record Pos(int x, int y) {
		Pos move(Dir d) {
			return new Pos(x() + d.x, y() + d.y);
		}
	}

	static class Hike {
		private final int[][] map;
		private final Pos start;
		private final Pos end;
		private final List<Dir> path;

		Hike(int[][] map, Pos start, List<Dir> path) {
			this.map = map;
			this.start = start;
			this.path = path;
			this.end = hike();
		}

		private Pos hike() {
			var currentPos = start;
			var height = 0;
			for (var p : path) {
				var nextPos = currentPos.move(p);
				if (nextPos.y() < 0 || nextPos.y() >= map.length) {
					return start;
				}
				if (nextPos.x() < 0 || nextPos.x() >= map[nextPos.y()].length) {
					return start;
				}
				if (map[nextPos.y()][nextPos.x()] != ++height) {
					return start;
				}
				currentPos = nextPos;
			}
			return currentPos;
		}

		boolean isLongHike() {
			return map[end.y()][end.x()] == 9;
		}
	}


	///
	/// EXPERIMENTAL
	/// Wanted to implement another Hike Planner;
	/// #1 generate all paths from 0,0 ordered by ends (done; we get 100 possible hike targets)
	/// #2 find all 0
	/// #3 find to every 0 matching 9
	/// #4 walk from 0 to 9
	///
	@Disabled
	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "36"),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/10/input", expected = "472")
	})
	void experiment(Stream<String> input, String expected) {
		var map = input.map(i -> i.chars().map(a -> a - '0').toArray()).toArray(int[][]::new);

		// #1
		var pathWithEnds = generateAllPossiblePathsWithEnds();
		// #2
		var startPoints = findStartPoints(map);

		var sum = startPoints.stream()
				.map(start -> new AbstractMap.SimpleEntry<>(start, getPathsToCheck(map, start, pathWithEnds)))
				.filter(this::isHikable).map(e -> e.getValue().keySet()).collect(toSet()).size();

		//System.out.println(sum);
		assertEquals(Integer.parseInt(expected), sum);
	}

	private boolean isHikable(AbstractMap.SimpleEntry<Pos, Map<Pos, Set<List<Dir>>>> endPosWithHike) {
		for(var possibleHikes : endPosWithHike.getValue().values()){
		}

		return true;
	}

	private Map<Pos, Set<List<Dir>>> getPathsToCheck(int[][] map, Pos start, Map<Pos, Set<List<Dir>>> pathWithEnds) {
		Map<Pos, Set<List<Dir>>> endpointsWithPaths = new HashMap<>();
		for(var entry : pathWithEnds.entrySet()){
			var end = new Pos(start.x()+entry.getKey().x(), start.y()+entry.getKey().y());
			if (end.y() < 0 || end.y() >= map.length) {
				continue;
			}
			if (end.x() < 0 || end.x() >= map[end.y()].length) {
				continue;
			}
			if(map[end.y][end.x] == 9){
				endpointsWithPaths.put(end, entry.getValue());
			}
		}
		return endpointsWithPaths;
	}

	private Map<Pos, Set<List<Dir>>> generateAllPossiblePathsWithEnds() {
		return IntStream.range(0, (int) Math.pow(4, 9))
				.mapToObj(i -> String.format("%9s", Integer.toUnsignedString(i, 4)).replaceAll(" ", "0"))
				.filter(s -> !s.contains("02") &&
						!s.contains("20") &&
						!s.contains("13") &&
						!s.contains("31"))
				.map(s -> s.chars().mapToObj(i -> Dir.values()[i - '0']).toList())
				.filter(this::nonCyclic)
				.collect(groupingBy(this::getEnd, toSet()));
	}

	private boolean nonCyclic(List<Dir> dirs) {
		var pos = new Pos(0,0);
		var visited = new HashSet<Pos>();
		visited.add(pos);
		for(var d : dirs) {
			pos = pos.move(d);
			if(!visited.add(pos)){
				return false;
			}
		}
		return true;
	}

}
