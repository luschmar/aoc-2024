import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day14Test {
	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "12"),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/14/input", expected = "230900224")
	})
	void part1(Stream<String> input, String expected) {
		var list = input.toList();
		var maxX = list.size() < 13 ? 11 : 101;
		var maxY = list.size() < 13 ? 7 : 103;
		var robots = list.stream().map(s -> new Robot(s, maxX, maxY)).toList();

		IntStream.range(0, 100).forEach(i -> robots.forEach(Robot::move));

		printRobots(robots);

		var q1 = IntStream.range(0, (maxY/2)).flatMap(y -> IntStream.range(0, (maxX/2)).map(
				x -> {
					var count = robots.stream().filter(r -> r.x.get() == x && r.y.get() == y).count();
					return (int)count;
				}
		)).sum();

		var q2 = IntStream.range((maxY/2)+1, maxY).flatMap(y -> IntStream.range(0, (maxX/2)).map(
				x -> {
					var count = robots.stream().filter(r -> r.x.get() == x && r.y.get() == y).count();
					return (int)count;
				}
		)).sum();

		var q3 = IntStream.range(0, (maxY/2)).flatMap(y -> IntStream.range((maxX/2)+1, maxX).map(
				x -> {
					var count = robots.stream().filter(r -> r.x.get() == x && r.y.get() == y).count();

					return (int)count;
				}
		)).sum();

		var q4 = IntStream.range((maxY/2)+1, maxY).flatMap(y -> IntStream.range((maxX/2)+1, maxX).map(
				x -> {
					var count = robots.stream().filter(r -> r.x.get() == x && r.y.get() == y).count();
					System.out.println(x+":"+y+"  "+count);
					return (int)count;
				}
		)).sum();

		System.out.println(q1+" "+q2+" "+q3+" "+q4);

		assertEquals(Integer.parseInt(expected), q1*q2*q3*q4);
	}

	private void printRobots(List<Robot> robots) {
		IntStream.range(0, 101).forEach(y -> {
			IntStream.range(0, 103).forEach(x -> {
				var count = robots.stream().filter(r -> r.x.get() == x && r.y.get() == y).count();
						if (count > 0) {
							System.out.print(count);
						} else {
							System.out.print(".");
						}
					}
			);
			System.out.println();
		});
		System.out.println();
	}

	@Test
	void testMove(){
		var r = new Robot("p=2,4 v=2,-3", 11, 7);

		printRobot(r);
		r.move();
		printRobot(r);
		r.move();
		printRobot(r);
		r.move();
		printRobot(r);
		r.move();
		printRobot(r);
		r.move();
		printRobot(r);
	}

	private void printRobot(Robot r) {
		IntStream.range(0, 7).forEach(y -> {
			IntStream.range(0, 11).forEach(x -> {
						if (r.x.get() == x && r.y.get() == y) {
							System.out.print("1");
						} else {
							System.out.print(".");
						}
					}
			);
			System.out.println();
		});
		System.out.println();
	}

	class Robot {
		private final int maxX;
		private final int maxY;
		Pattern p = Pattern.compile("p=(\\d+),(\\d+) v=(-?\\d+),(-?\\d+)");

		final AtomicInteger x;
		final AtomicInteger y;
		final int vX;
		final int vY;

		Robot(String str, int maxX, int maxY){
			var s = p.matcher(str);
			s.matches();

			this.maxX = maxX;
			this.maxY = maxY;

			x = new AtomicInteger(Integer.parseInt(s.group(1)));
			y = new AtomicInteger(Integer.parseInt(s.group(2)));

			vX = Integer.parseInt(s.group(3));
			vY = Integer.parseInt(s.group(4));

		}

		void move()      {
			var newX = x.addAndGet(vX);
			var newY = y.addAndGet(vY);
			if(newX < 0) {
				x.set(maxX+newX);
			}
			if(newY < 0) {
				y.set(maxY+newY);
			}
			if(newX >= maxX) {
				x.set(newX-maxX);
			}
			if(newY >= maxY) {
				y.set(newY-maxY);
			}
		}
	}

	@Disabled("Will run 3min 20sec :-)")
	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = ""),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/14/input", expected = "6532")
	})
	void part2(Stream<String> input, String expected) {
		var list = input.toList();
		var maxX = list.size() < 13 ? 11 : 101;
		var maxY = list.size() < 13 ? 7 : 103;
		var robots = list.stream().map(s -> new Robot(s, maxX, maxY)).toList();

		IntStream.range(0, 6533).forEach(i -> {robots.forEach(
				r -> {
					r.move();
				});
			if(isTreeCandidate(robots, maxX, maxY)){
				System.out.println("-----------");
				System.out.println(i);
				System.out.println("-----------");
				printRobots(robots);
			}
		});


		//assertEquals(Integer.parseInt(expected), "");
	}

	private boolean isTreeCandidate(List<Robot> robots, int maxX, int maxY) {

		var line = IntStream.range(0, 103).mapToObj(y -> {
			return IntStream.range(0, 101).map(x -> {
						var count = robots.stream().filter(r -> r.x.get() == x && r.y.get() == y).count();
						return (int)count > 0 ? '#' : '.';
					}).collect(StringBuilder::new,
							StringBuilder::appendCodePoint, StringBuilder::append)
					.toString();
		}).toList();
		// naive approach
		return line.stream().anyMatch(s -> s.contains("###########"));

		//return IntStream.range(0, 100).allMatch(y -> IntStream.range(2, 99).mapToObj(x -> hasTree(line, x, y)).allMatch(b -> b));

/**
		var should0 = IntStream.range(0, (maxY/2)).flatMap(y -> IntStream.range(0, (maxX/2)-1-y).map(
				x -> {
					var count = robots.stream().filter(r -> r.x.get() == x && r.y.get() == y).count();
					return (int)count;
				}
		)).sum();**/
		//System.out.println(should0);

		//return should0 < 10;
	}

	///
	/// Search for shape
	///   #
	///  # #
	/// #   #
	private boolean hasTree(List<String> line, int x, int y) {
		if(line.get(y).charAt(x) == '#' &&
		   line.get(y+1).charAt(x-1) == '#' &&
		   line.get(y+1).charAt(x+1) == '#' &&
				line.get(y+2).charAt(x-2) == '#' &&
				line.get(y+2).charAt(x+2) == '#' ) {
			return true;
		}

		return false;
	}
}
