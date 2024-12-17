import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day17Test {
	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "4,6,3,5,6,3,5,2,1,0"),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/17/input", expected = "-1")
	})
	void part1(Stream<String> input, String expected) {
		var inputList = input.toList();

		var a = Integer.parseInt(inputList.get(0).replaceAll("Register A: ", ""));
		var b = Integer.parseInt(inputList.get(1).replaceAll("Register B: ", ""));
		var c = Integer.parseInt(inputList.get(2).replaceAll("Register C: ", ""));
		var program = Arrays.stream(inputList.get(4).replaceAll("Program: ", "").split(",")).map(Integer::parseInt).toList();

		var computer = new ThreeBitComputer(a, b, c, program);
		computer.run();

		assertEquals(expected, computer.out.stream().map(Object::toString).collect(Collectors.joining(",")));
	}

	class ThreeBitComputer {
		private int registerA;
		private int registerB;
		private int registerC;

		private int instPointer;

		private List<Integer> out = new ArrayList<>();

		private List<Integer> program;

		ThreeBitComputer(int A, int B, int C, List<Integer> program){
			this.registerA = A;
			this.registerB = B;
			this.registerC = C;
			this.program = program;
		}

		void run(){
			while(instPointer+1 < program.size() && instPointer >= 0){
				exec(program.get(instPointer), program.get(instPointer+1));
			}
		}

		void exec(int opcode, int operand) {
			switch (opcode){
				case 0 -> adv(opcode, operand);
				case 1 -> bxl(opcode, operand);
				case 2 -> bst(opcode, operand);
				case 3 -> jnz(opcode, operand);
				case 4 -> bxc(opcode, operand);
				case 5 -> out(opcode, operand);
				case 6 -> bdv(opcode, operand);
				case 7 -> cdv(opcode, operand);
				default -> new IllegalArgumentException();
			}
			instPointer += 2 ;
		}

		private void cdv(int opcode, int operand) {
			registerC = Math.divideExact(registerA, (int)Math.pow(2, resolveComboOperand(operand)));

		}

		private void bdv(int opcode, int operand) {
			registerB = Math.divideExact(registerA, (int)Math.pow(2, resolveComboOperand(operand)));
		}

		private void out(int opcode, int operand) {
			out.add(resolveComboOperand(operand) % 8);
		}

		private void bxc(int opcode, int operand) {
			registerB = registerB ^ registerC;
		}

		private void jnz(int opcode, int operand) {
			if(registerA == 0){
				return;
			}
			instPointer = operand-2;
		}

		private void bst(int opcode, int operand) {
			registerB = resolveComboOperand(operand) % 8;
		}

		private void bxl(int opcode, int operand) {
			registerB = registerB ^ operand;
		}

		private void adv(int opcode, int operand) {
			registerA = Math.divideExact(registerA, (int)Math.pow(2, resolveComboOperand(operand)));
		}

		private int resolveComboOperand(int operand) {
			return switch (operand){
				case 0,1,2,3 -> operand;
				case 4 -> registerA;
				case 5 -> registerB;
				case 6 -> registerC;
				default -> throw new IllegalArgumentException();
			};
		}
	}

	@ParameterizedTest
	@CsvSource(value = {
			//"0 0 9 2,6",
			"10 0 0 5,0,5,1,5,4"

	}, delimiter = ' ')
	void test(String a, String b, String c, String program){
		var iA = Integer.parseInt(a);
		var iB = Integer.parseInt(b);
		var iC = Integer.parseInt(c);
		var programList = Arrays.stream(program.split(",")).map(Integer::parseInt).toList();

		var tbc = new ThreeBitComputer(iA, iB, iC, programList);
		tbc.run();

		System.out.println(tbc.out.stream().map(Objects::toString).collect(Collectors.joining(",")));
	}

	@ParameterizedTest
	@AocFileSource(inputs = {
			@AocInputMapping(input = "test.txt", expected = "117440"),
			//@AocInputMapping(input = "https://adventofcode.com/2024/day/17/input", expected = "-1")
	})
	void part2(Stream<String> input, String expected) {
		var inputList = input.toList();

		var a = Integer.parseInt(inputList.get(0).replaceAll("Register A: ", ""));
		var b = Integer.parseInt(inputList.get(1).replaceAll("Register B: ", ""));
		var c = Integer.parseInt(inputList.get(2).replaceAll("Register C: ", ""));
		var program = Arrays.stream(inputList.get(4).replaceAll("Program: ", "").split(",")).map(Integer::parseInt).toList();

		var res = IntStream.range(0, Integer.MAX_VALUE).parallel().filter(aa -> {
			var computer = new ThreeBitComputer(a, b, c, program);
			computer.run();

			return IntStream.range(0, program.size()).allMatch(ii -> program.get(ii) == computer.out.get(ii));
		}).min().getAsInt();

		assertEquals(Integer.parseInt(expected), res);
	}
}
