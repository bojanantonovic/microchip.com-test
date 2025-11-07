package ch.antonovic.microchipcomtest;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class SearchApp extends Application {
	@Override
	public void start(final Stage stage) {
		final TextField textField = new TextField();
		textField.setPromptText("Suche… (Enter oder kurz warten)");

		final ListView<String> list = new ListView<>();

		// Enter
		textField.setOnAction(e -> runSearch(textField.getText(), list));

		// Debounce
		final PauseTransition debounce = new PauseTransition(Duration.millis(300));
		textField.textProperty().addListener((o, ov, nv) -> {
			debounce.stop();
			debounce.setOnFinished(ev -> runSearch(nv, list));
			debounce.playFromStart();
		});

		final VBox root = new VBox(10, textField, list);
		root.setPadding(new Insets(12));
		stage.setScene(new Scene(root, 420, 280));
		stage.setTitle("Suche");
		stage.show();
	}

	private void runSearch(final String string, final ListView<String> list) {
		final Task<List<String>> task = new Task<>() {
			@Override
			protected List<String> call() throws Exception {
				//System.out.println("Suche: " + string);
				final String searchString = string.toUpperCase();
				final String[] array = IntStream.rangeClosed(0, 26 * 26 * 26 * 26) // absichtlich ungecached
						.mapToObj(SearchApp::numberToString) //
						.toArray(String[]::new);
				return Arrays.stream(array) //
						.parallel() //
						.filter(searchString::equals) //
						.toList();
			}
		};
		task.setOnSucceeded(e -> list.getItems().setAll(task.getValue()));
		task.setOnFailed(e -> list.getItems().setAll("Fehler: " + task.getException()));
		new Thread(task, "search").start();
	}

	private static String numberToString(int number) {
		final char[] chars = new char[4];

		for (int i = 0; i < 4; i++) {
			chars[i] = (char) ('A' + number % 26);
			number /= 26;
		}

		return new String(chars);
	}

	public static void main(final String[] args) {
		launch(args);
	}
}
