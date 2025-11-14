package ch.antonovic.microchipcomtest;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SearchApp extends Application {

	private static final String[] COMBINATORICAL_ARRAY;

	static {
		try {
			COMBINATORICAL_ARRAY = CsvIO.readCombinationsFromCsvFile();
		} catch (IOException e) {
			System.err.println("Fehler beim Lesen der CSV-Datei." + e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void start(final Stage stage) {
		final TextField textField = new TextField();
		textField.setPromptText("Suche… (Enter oder kurz warten)");

		final ListView<String> list = new ListView<>();

		final Label timeLabel = new Label();
		final VBox root = new VBox(10, textField, list, timeLabel);

		// Enter
		textField.setOnAction(e -> runSearch(textField.getText(), list, timeLabel));

		// Debounce
		final PauseTransition debounce = new PauseTransition(Duration.millis(300));
		textField.textProperty().addListener((o, ov, nv) -> {
			debounce.stop();
			debounce.setOnFinished(ev -> runSearch(nv, list, timeLabel));
			debounce.playFromStart();
		});

		root.setPadding(new Insets(12));
		stage.setScene(new Scene(root, 420, 280));
		stage.setTitle("Suche");
		stage.show();
	}

	private void runSearch(final String string, final ListView<String> list, final Label timeLabel) {
		final Task<List<String>> task = new Task<>() {
			@Override
			protected List<String> call() throws Exception {

				final long start = System.nanoTime();

				final String searchString = string.toUpperCase();
				List<String> result = Arrays.stream(COMBINATORICAL_ARRAY).parallel().filter(searchString::equals).toList();

				final long end = System.nanoTime();
				final long durationMs = (end - start) / 1_000_000;

				// Nachricht an UI senden
				updateMessage("Dauer: " + durationMs + " ms");

				return result;
			}
		};

		// wird aufgerufen, wenn Task erfolgreich fertig ist (UI-Thread)
		task.setOnSucceeded(e -> {
			list.getItems().setAll(task.getValue());
			timeLabel.setText(task.getMessage());   // Zeit anzeigen
		});

		task.setOnFailed(e -> {
			list.getItems().setAll("Fehler: " + task.getException());
			timeLabel.setText("Fehler!");
		});

		new Thread(task, "search").start();
	}

	public static void main(final String[] args) {
		final var availableProcessors = Runtime.getRuntime().availableProcessors(); // change this number if you want to change the number of threads
		System.out.println("availableProcessors: " + availableProcessors);
		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(availableProcessors));

		launch(args);
	}
}
