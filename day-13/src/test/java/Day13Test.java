import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.lang.Math.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day13Test {
	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "480"),
			@AocInputMapping(input = "edge.txt", expected = "285"),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/13/input", expected = "37128")
	})
	void part1(Stream<String> input, String expected) {
		var inputStr = input.toList();

		var machines = IntStream.range(0, (inputStr.size() + 1) / 4).mapToObj(i -> {
			var index = i * 4;
			return new Machine(inputStr.get(index), inputStr.get(index + 1), inputStr.get(index + 2));
		}).toList();


		var minCost = machines.stream().peek(Machine::trySolve).filter(Machine::isSolved).mapToLong(a -> (long) a.solution.cost).sum();
		machines.forEach(m -> System.out.println(m.solution == null ? "0" : m.solution.cost));

		assertEquals(Integer.parseInt(expected), minCost);
	}

	@Disabled
	@ParameterizedTest
	@AocFileSource(inputs = {
			//@AocInputMapping(input = "test.txt", expected = "875318608908"),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/13/input", expected = "74914228471331"),
			//@AocInputMapping(input = "edge.txt", expected = "0")
	})
	void part2(Stream<String> input, String expected) {
		var inputStr = input.toList();

		var machines = IntStream.range(0, (inputStr.size() + 1) / 4).mapToObj(i -> {
			var index = i * 4;
			return new Machine(inputStr.get(index), inputStr.get(index + 1), inputStr.get(index + 2), true);
		}).toList();


		var solvedMachines = machines.stream().peek(Machine::solveCorrect).toList();

		solvedMachines.forEach(m -> System.out.println(m.solution == null ? "0" : m.solution.cost));

		assertEquals(Long.parseLong(expected), solvedMachines.stream().filter(Machine::isSolved).mapToLong(s -> s.solution.cost).sum());
	}

	///  Soluton from 🙄
	///  https://github.com/ash42/adventofcode/blob/main/adventofcode2024/src/nl/michielgraat/adventofcode2024/day13/Day13.java
	///  Needed for my issue


	private Equation parse(final List<String> input) {
		final String line1 = input.get(0);
		final String line2 = input.get(1);
		final String line3 = input.get(2);

		final int xa = Integer.parseInt(line1.split("X+")[1].split(",")[0]);
		final int ya = Integer.parseInt(line1.split("Y+")[1]);
		final int xb = Integer.parseInt(line2.split("X+")[1].split(",")[0]);
		final int yb = Integer.parseInt(line2.split("Y+")[1]);
		final int xPrize = Integer.parseInt(line3.split("X=")[1].split(",")[0]);
		final int yPrize = Integer.parseInt(line3.split("Y=")[1]);

		return new Equation(xa, xb, xPrize, ya, yb, yPrize);
	}

	private List<Equation> parseInput(final List<String> input) {
		final List<Equation> result = new ArrayList<>();
		List<String> current = new ArrayList<>();
		for (int i = 0; i < input.size(); i++) {
			final String line = input.get(i);
			if (line.isBlank()) {
				result.add(parse(current));
				current = new ArrayList<>();
			} else {
				current.add(input.get(i));
				if (i == input.size() - 1) {
					result.add(parse(current));
				}
			}
		}
		return result;
	}

	private void gaussianElimination(final double[][] coefficients, final double rhs[]) {
		final int nrVariables = coefficients.length;
		for (int i = 0; i < nrVariables; i++) {
			// Select pivot
			final double pivot = coefficients[i][i];
			// Normalize row i
			for (int j = 0; j < nrVariables; j++) {
				coefficients[i][j] = coefficients[i][j] / pivot;
			}
			rhs[i] = rhs[i] / pivot;
			// Sweep using row i
			for (int k = 0; k < nrVariables; k++) {
				if (k != i) {
					final double factor = coefficients[k][i];
					for (int j = 0; j < nrVariables; j++) {
						coefficients[k][j] = coefficients[k][j] - factor * coefficients[i][j];
					}
					rhs[k] = rhs[k] - factor * rhs[i];
				}
			}
		}
	}

	private long solveEquation(final Equation e, final long prizeIncrement) {
		final double[][] coefficients = new double[2][2];
		final double[] rhs = new double[2];
		final long xPrize = e.xPrize() + prizeIncrement;
		final long yPrize = e.yPrize() + prizeIncrement;
		coefficients[0][0] = e.xa();
		coefficients[0][1] = e.xb();
		coefficients[1][0] = e.ya();
		coefficients[1][1] = e.yb();
		rhs[0] = xPrize;
		rhs[1] = yPrize;
		gaussianElimination(coefficients, rhs);
		final long a = Math.round(rhs[0]);
		final long b = Math.round(rhs[1]);
		// Double-check after rounding
		if (a * e.xa() + b * e.xb() == xPrize && a * e.ya() + b * e.yb() == yPrize) {
			return a * 3 + b;
		} else {
			return 0;
		}
	}

	@ParameterizedTest
	@AocFileSource(inputs = {
			//@AocInputMapping(input = "test.txt", expected = "875318608908"),
			//@AocInputMapping(input = "https://adventofcode.com/2024/day/13/input", expected = "74914228471331"),
			@AocInputMapping(input = "edge.txt", expected = "0")
	})
	void runPart2(Stream<String> input, String expected) {
		final List<Equation> equations = parseInput(input.toList());
		var result = equations.stream().mapToLong(e -> solveEquation(e, 10000000000000L)).toArray();

		for (var r : result) {
			System.out.println(r);
		}

		assertEquals(Long.parseLong(expected), Arrays.stream(result).sum());
	}

	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "480"),
			@AocInputMapping(input = "edge.txt", expected = "285"),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/13/input", expected = "37128")
	})
	void runPart1(Stream<String> input, String expected) {
		final List<Equation> equations = parseInput(input.toList());


		var arrayResult = equations.stream().mapToLong(e -> solveEquation(e, 0)).toArray();

		for (var a : arrayResult) {
			System.out.println(a);
		}

		assertEquals(Integer.parseInt(expected), LongStream.of(arrayResult).sum());
	}

	record Solution(long pushA, long pushB, long cost) {
	}

	record Equation(int xa, int xb, int xPrize, int ya, int yb, int yPrize) {
	}

	class Machine {
		private final int aX;
		private final int aY;
		private final int bX;
		private final int bY;
		private final long pX;
		private final long pY;
		private final boolean highReward;
		Pattern p = Pattern.compile("[X|Y][\\+|=](\\d+)");
		private Solution solution = null;

		Machine(String buttonA, String buttonB, String prize) {
			this(buttonA, buttonB, prize, false);
		}

		Machine(String buttonA, String buttonB, String prize, boolean highReward) {
			var m = p.matcher(buttonA);
			m.find();
			aX = Integer.parseInt(m.group(1));
			m.find();
			aY = Integer.parseInt(m.group(1));
			m = p.matcher(buttonB);
			m.find();
			bX = Integer.parseInt(m.group(1));
			m.find();
			bY = Integer.parseInt(m.group(1));

			m = p.matcher(prize);
			m.find();
			pX = Math.addExact(highReward ? 10000000000000L : 0L, Integer.parseInt(m.group(1)));
			m.find();
			pY = Math.addExact(highReward ? 10000000000000L : 0L, Integer.parseInt(m.group(1)));
			this.highReward = highReward;
		}

		private void solveCorrect() {
			// Oh no - there is an edge-case
			//Button A: X+42, Y+96
			//Button B: X+96, Y+48
			//Prize: X=13398, Y=13824
			var _pY = BigDecimal.valueOf(pY);
			var _pX = BigDecimal.valueOf(pX);
			var _aX = BigDecimal.valueOf(aX);
			var _aY = BigDecimal.valueOf(aY);
			var _bX = BigDecimal.valueOf(bX);
			var _bY = BigDecimal.valueOf(bY);

			var q1 = _pY.multiply(_aX);
			var q2 = _pX.multiply(_aY);
			var d1 = _bY.multiply(_aX);
			var d2 = _bX.multiply(_aY);

			var q = q1.subtract(q2);
			var d = d1.subtract(d2);

			var res = q.divideAndRemainder(d);

			if (BigDecimal.ZERO != res[1]) {
				return;
			}

			// b=(py*ax-px*ay)/(by*ax-bx*ay)
			var b = res[0].longValue();
			// a=(px-b*bx)/ax
			var a = divideExact(subtractExact(pX, multiplyExact(b, bX)), aX);

			solution = mapToSolution(a, b);
		}

		private void trySolve() {
			var start = highReward ? 100 : 0;

			// STUPID - (resX / aDeltaX) + 1 Idiot!!!
			var aMaxXSteps = highReward ? Math.addExact(divideExact(pX, aX), 1) : Math.min(100, Math.addExact(divideExact(pX, aX), 1));
			var bMaxXSteps = highReward ? Math.addExact(divideExact(pX, bX), 1) : Math.min(100, Math.addExact(divideExact(pX, bX), 1));

			var solutionsX = LongStream.range(start, aMaxXSteps)
					.mapToObj(pA -> LongStream.range(start, bMaxXSteps)
							.filter(pB -> pX == (pA * aX + pB * bX))
							.mapToObj(pB -> mapToSolution(pA, pB))).flatMap(a -> a)
					.toList();

			var aMaxYSteps = highReward ? Math.addExact(divideExact(pY, aY), 1) : Math.min(100, Math.addExact(divideExact(pY, aY), 1));
			var bMaxYSteps = highReward ? Math.addExact(divideExact(pY, bY), 1) : Math.min(100, Math.addExact(divideExact(pY, bY), 1));

			var solutionsY = LongStream.range(start, aMaxYSteps)
					.mapToObj(pA -> LongStream.range(start, bMaxYSteps)
							.filter(pB -> pY == (pA * aY + pB * bY))
							.mapToObj(pB -> mapToSolution(pA, pB))).flatMap(a -> a)
					.toList();

			var possibleSolution = solutionsX.stream().filter(solutionsY::contains).toList();

			if (!possibleSolution.isEmpty()) {
				solution = possibleSolution.get(0);
			}
		}

		private Solution mapToSolution(long pA, long pB) {
			return new Solution(pA, pB,
					Math.addExact(Math.multiplyExact(pA, 3L), pB));
		}

		private boolean isSolved() {
			return solution != null;
		}
	}
}
