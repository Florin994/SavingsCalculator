import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UI extends Application {
    private LineChart<Number, Number> lineChart;

    @Override
    public void start(Stage window) {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10, 10, 10, 10));
        NumberAxis xAxis = new NumberAxis(0, 30, 1);
        NumberAxis yAxis = new NumberAxis();
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Savings calculator");
        layout.setCenter(lineChart);
        VBox groupComponents = new VBox();
        layout.setTop(groupComponents);
        BorderPane monthlySavings = new BorderPane();
        Label firstText = new Label("Monthly savings");
        Slider firstSlider = new Slider(25, 1000, 25);
        firstSlider.setShowTickLabels(true);
        firstSlider.setShowTickMarks(true);
        firstSlider.setMajorTickUnit(25);
        firstSlider.setMinorTickCount(0);
        firstSlider.setSnapToTicks(true);
        Label amount = new Label(String.format("%.2f", firstSlider.getValue()));
        BorderPane yearlyInterestRate = new BorderPane();
        Label secondText = new Label("Yearly interest rate");
        Slider secondSlider = new Slider(0, 10, 1);
        secondSlider.setShowTickLabels(true);
        secondSlider.setShowTickMarks(true);
        secondSlider.setSnapToTicks(true);
        secondSlider.setMajorTickUnit(1);
        secondSlider.setMinorTickCount(0);
        Label howManyYears = new Label(String.format("%.2f", secondSlider.getValue()));
        firstSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            amount.setText(String.format("%.2f", newValue));
            updateChart(newValue.doubleValue(), secondSlider.getValue());
        });
        secondSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            howManyYears.setText(String.format("%.0f", secondSlider.getValue()));
            updateChart(firstSlider.getValue(), newValue.doubleValue());
        });
        monthlySavings.setLeft(firstText);
        monthlySavings.setCenter(firstSlider);
        monthlySavings.setRight(amount);
        yearlyInterestRate.setLeft(secondText);
        yearlyInterestRate.setCenter(secondSlider);
        yearlyInterestRate.setRight(howManyYears);
        groupComponents.getChildren().addAll(monthlySavings, yearlyInterestRate);
        updateChart(firstSlider.getValue(), secondSlider.getValue());
        Scene view = new Scene(layout);
        window.setScene(view);
        window.show();
    }

    private void updateChart(double monthlySavings, double yearlyInterestRate) {
        lineChart.getData().clear();
        XYChart.Series<Number, Number> savingsSeries = new XYChart.Series<>();
        savingsSeries.setName("Savings without interest");
        XYChart.Series<Number, Number> interestSeries = new XYChart.Series<>();
        interestSeries.setName("Savings with interest");
        double totalSavings = 0;
        double totalSavingsWithInterest = 0;
        for (int year = 0; year <= 30; year++) {
            XYChart.Data<Number, Number> savingsData = new XYChart.Data<>(year, totalSavings);
            XYChart.Data<Number, Number> interestData = new XYChart.Data<>(year, totalSavingsWithInterest);
            addTooltipToData(savingsData, "Year: " + year + "\nSavings: "
                    + String.format("%.2f", totalSavings));
            addTooltipToData(interestData, "Year: " + year + "\nSavings with interest: "
                    + String.format("%.2f", totalSavingsWithInterest));
            savingsSeries.getData().add(savingsData);
            interestSeries.getData().add(interestData);
            totalSavings += (monthlySavings * 12);
            totalSavingsWithInterest += (monthlySavings * 12);
            totalSavingsWithInterest *= (1 + yearlyInterestRate / 100.0);  // Apply interest at the end of the year
        }
        lineChart.getData().addAll(savingsSeries, interestSeries);
    }

    private void addTooltipToData(XYChart.Data<Number, Number> data, String tooltipText) {
        Tooltip tooltip = new Tooltip(tooltipText);
        data.nodeProperty().addListener((observable, oldNode, newNode) -> {
            if (newNode != null) {
                Tooltip.install(newNode, tooltip);
                newNode.setOnMouseEntered(event -> newNode.setStyle("-fx-background-color: yellow;"));
                newNode.setOnMouseExited(event -> newNode.setStyle(""));
            }
        });
    }
}
