module ch.antonovic.microchipcomtest {
	requires javafx.controls;
	requires javafx.fxml;

	opens ch.antonovic.microchipcomtest to javafx.fxml;
	exports ch.antonovic.microchipcomtest;
}
