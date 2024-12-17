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
			@AocInputMapping(input = "https://adventofcode.com/2024/day/17/input", expected = "4,1,5,3,1,5,3,5,7")
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
		private long registerA;
		private long registerB;
		private long registerC;

		private int instPointer;

		private List<Integer> out = new ArrayList<>();

		private List<Integer> program;

		ThreeBitComputer(long A, long B, long C, List<Integer> program){
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
			//System.out.printf("%-10d %-30s %-10d %-10d%n", instPointer, Long.toBinaryString(registerA), registerB, registerC);
			//System.out.println(out);
			switch (opcode){
				case 0 -> adv(operand);
				case 1 -> bxl(operand);
				case 2 -> bst(operand);
				case 3 -> jnz(operand);
				case 4 -> bxc();
				case 5 -> out(operand);
				case 6 -> bdv(operand);
				case 7 -> cdv(operand);
				default -> new IllegalArgumentException();
			}
			instPointer += 2 ;
		}

		private void cdv(int operand) {
			registerC = Math.divideExact(registerA, (long)Math.pow(2, resolveComboOperand(operand)));
		}

		private void bdv(int operand) {
			registerB = Math.divideExact(registerA, (long)Math.pow(2, resolveComboOperand(operand)));
		}

		private void out(int operand) {
			out.add((int)(resolveComboOperand(operand) % 8L));
		}

		private void bxc() {
			registerB = registerB ^ registerC;
		}

		private void jnz(int operand) {
			if(registerA == 0){
				return;
			}
			instPointer = operand-2;
		}

		private void bst(int operand) {
			registerB = resolveComboOperand(operand) % 8L;
		}

		private void bxl(int operand) {
			registerB = registerB ^ operand;
		}

		private void adv(int operand) {
			registerA = Math.divideExact(registerA, (int)Math.pow(2, resolveComboOperand(operand)));
		}

		private long resolveComboOperand(int operand) {
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
			//@AocInputMapping(input = "test.txt", expected = "117440"),
			@AocInputMapping(input = "https://adventofcode.com/2024/day/17/input", expected = "-1")
	})
	void part2(Stream<String> input, String expected) {
		var inputList = input.toList();

		//min
		//var a = 35184372088832L;


		var b = Integer.parseInt(inputList.get(1).replaceAll("Register B: ", ""));
		var c = Integer.parseInt(inputList.get(2).replaceAll("Register C: ", ""));
		var program = Arrays.stream(inputList.get(4).replaceAll("Program: ", "").split(",")).map(Integer::parseInt).toList();


		long res = 1L << (program.size()-1L) *3L;
		for(int p = 0; p < program.size(); p++) {
			for(long i = 1; i < 8; i++){
				var candidate = i << ((program.size()-p-1L)*3L);
				candidate += res;
				System.out.println("res:  %50s".formatted(Long.toBinaryString(res)));
				System.out.println("cand: %50s".formatted(Long.toBinaryString(candidate)));
				var computer = new ThreeBitComputer(candidate, b, c, program);
				computer.run();

				if(IntStream.range(0, p).allMatch(k -> program.reversed().get(k).equals(computer.out.reversed().get(k)))){
					System.out.println(candidate);
					res = Math.min(res, candidate);
				}
			}

		}

		assertEquals(Long.parseLong(expected), res);
	}

}
