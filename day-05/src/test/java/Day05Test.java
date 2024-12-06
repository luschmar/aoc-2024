import org.junit.jupiter.params.ParameterizedTest;

import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day05Test {
	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "143"),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/5/input", expected = "4609")
	})
	void part1(Stream<String> input, String expected) {
		var all = input.toList();

		var printOrderRules = all.stream().map(s -> {
			if (s.contains("|")) {
				return new PageOrderingRule(s);
			}
			return null;
		}).filter(Objects::nonNull).toList();
		var updates = all.stream().map(s -> {
			if (s.contains(",")) {
				return new Update(s);
			}
			return null;
		}).filter(Objects::nonNull).toList();

		var sumUpdates = updates.stream().filter(f -> f.isValid(printOrderRules)).mapToLong(Update::middleNumber).sum();

		assertEquals(Long.parseLong(expected), sumUpdates);
	}

	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "123"),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/5/input", expected = "5723")
	})
	void part2(Stream<String> input, String expected) {
		var all = input.toList();

		var printOrderRules = all.stream().map(s -> {
			if (s.contains("|")) {
				return new PageOrderingRule(s);
			}
			return null;
		}).filter(Objects::nonNull).toList();
		var faultyUpdates = all.stream().map(s -> {
			if (s.contains(",")) {
				return new Update(s);
			}
			return null;
		}).filter(Objects::nonNull).filter(u -> !u.isValid(printOrderRules)).mapToLong(u -> u.reorder(printOrderRules).middleNumber()).sum();

		assertEquals(Long.parseLong(expected), faultyUpdates);
	}

	class PageOrderingRule {
		private final int min;
		private final int max;

		PageOrderingRule(String rule) {
			min = Integer.parseInt(rule.split("\\|")[0]);
			max = Integer.parseInt(rule.split("\\|")[1]);
		}
	}

	class Update {
		final List<Integer> printOrder;

		Update(List<Integer> printOrder) {
			this.printOrder = printOrder;
		}

		Update(String update) {
			printOrder = Arrays.stream(update.split(",")).map(Integer::parseInt).toList();
		}

		long middleNumber() {
			return printOrder.get(printOrder.size() / 2);
		}

		boolean isValid(List<PageOrderingRule> rules) {
			return rules.stream().allMatch(v -> {
				if (printOrder.contains(v.min) && printOrder.contains(v.max)) {
					return printOrder.indexOf(v.min) < printOrder.indexOf(v.max);
				}
				return true;
			});
		}

		public Update reorder(List<PageOrderingRule> rules) {
			var sorted = new ArrayList<>(printOrder);
			sorted.sort((o1, o2) -> rules.stream().anyMatch(s -> s.min == o1 && s.max == o2) ? -1 : 1);
			return new Update(sorted);
		}


		public void insert(List<PageOrderingRule> printOrderRules, int min, int max) {
			//printOrderRules;
		}
	}
}
