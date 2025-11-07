package ch.antonovic.microchipcomtest;

import java.util.stream.IntStream;

public class Commons {
	private static final int WIDTH = 4;
	public static final int TOTAL_COMBINATIONS = (int) Math.pow(26, WIDTH) - 1;

	public static String numberToString(int number) {
		final char[] chars = new char[WIDTH];

		for (int i = 0; i < WIDTH; i++) {
			chars[WIDTH - i - 1] = (char) ('A' + number % 26);
			number /= 26;
		}

		return new String(chars);
	}

	public static String[] createCombinatoricalArray() {
		return IntStream.rangeClosed(0, Commons.TOTAL_COMBINATIONS) // absichtlich ungecached
				.mapToObj(Commons::numberToString) //
				.toArray(String[]::new);
	}
}
