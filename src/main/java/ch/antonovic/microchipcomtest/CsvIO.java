package ch.antonovic.microchipcomtest;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CsvIO {

	// do this initially
	public static void main(final String[] args) throws IOException {
		createArrayAndWriteToCsvFile();
	}

	private static void createArrayAndWriteToCsvFile() throws IOException {
		final String[] combinations = Commons.createCombinatoricalArray();
		final var linesToWrite = Arrays.stream(combinations) //
				.map(s -> s + ";\n") //
				.collect(Collectors.joining());
		Files.writeString(CsvCommons.PATH, linesToWrite);
	}

	public static String[] readCombinationsFromCsvFile() throws IOException {
		final var string = Files.readString(CsvCommons.PATH);
		return string.split(";\n");
	}
}
