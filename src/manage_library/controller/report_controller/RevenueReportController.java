package manage_library.controller.report_controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import manage_library.data_object.report.CategoryBookReport;
import manage_library.data_object.report.RevenueReport;
import manage_library.data_object.report.StatusBookReport;
import manage_library.database.DatabaseHandler;
import manage_library.model.BookModel;
import manage_library.model.BorrowModel;
import manage_library.util.AlertCustom;
import manage_library.util.Constant;
import manage_library.util.format.CurrencyFormat;
import manage_library.util.format.DateFormat;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


public class RevenueReportController {
    @FXML
    private JFXDatePicker startDate;

    @FXML
    private JFXDatePicker endDate;

    @FXML
    private JFXButton dayInterval;

    @FXML
    private JFXButton monthInterval;

    @FXML
    private JFXButton yearInterval;

    @FXML
    private FontAwesomeIconView downloadData;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab revenueTab;

    @FXML
    private Tab bookTab;

    @FXML
    private LineChart<String, Number> lineChartRevenue;

    @FXML
    private PieChart pieChartBook;

    @FXML
    private BarChart<String, Number> barChartBook;



    private final BorrowModel borrowModel = new BorrowModel();
    private final BookModel bookModel = new BookModel();
    private final Connection connection = DatabaseHandler.getInstance().getDbConnection();
    private String timeInterval = Constant.ReportInterval.DAY;

    public RevenueReportController() throws SQLException {
    }

    @FXML
    void initialize() throws SQLException {
        selectedIntervalHandle();

        DateFormat.formatDatePicker(startDate, endDate);
        startDate.setValue(LocalDate.now().minusDays(7));
        endDate.setValue(LocalDate.now());
        Platform.runLater(() -> {
            startDate.valueProperty().addListener((observable, oldDate, newDate)-> {
                try {
                    selectedIntervalHandle();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            endDate.valueProperty().addListener((observable, oldDate, newDate)-> {
                try {
                    selectedIntervalHandle();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            dayInterval.setOnMouseClicked(e -> {
                timeInterval = Constant.ReportInterval.DAY;
                try {
                    selectedIntervalHandle();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            monthInterval.setOnMouseClicked(e -> {
                timeInterval = Constant.ReportInterval.MONTH;
                try {
                    selectedIntervalHandle();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            yearInterval.setOnMouseClicked(e -> {
                timeInterval = Constant.ReportInterval.YEAR;
                try {
                    selectedIntervalHandle();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            createBarChartBook();
            createPieChart();
        });

    }

    private void selectedIntervalHandle() throws SQLException {
        final String selectedColor = "-fx-background-color: green;" ;
        final String defaultColor = "-fx-background-color: blue;" ;
        switch (timeInterval) {
            case Constant.ReportInterval.MONTH:
                monthInterval.setStyle(selectedColor);
                yearInterval.setStyle(defaultColor);
                dayInterval.setStyle(defaultColor);
                break;

            case Constant.ReportInterval.YEAR:
                yearInterval.setStyle(selectedColor);
                dayInterval.setStyle(defaultColor);
                monthInterval.setStyle(defaultColor);
                break;

            default:
                dayInterval.setStyle(selectedColor);
                yearInterval.setStyle(defaultColor);
                monthInterval.setStyle(defaultColor);
                break;
        }

        createLineChartRevenue();
    }

    public void createPieChart() {
        List<StatusBookReport> statusBookReports = bookModel.getStatusAllBook(connection);
        ObservableList<PieChart.Data> series = FXCollections.observableArrayList();
        int totalAllBook = statusBookReports.stream().mapToInt(o -> o.getTotalBook()).sum();
        for (StatusBookReport statusBookReport: statusBookReports) {
            series.add(new PieChart.Data(convertTooltipChart(statusBookReport.getStatusBook(), statusBookReport.getTotalBook() * 1.0, totalAllBook * 1.0), statusBookReport.getTotalBook()));
        }
        pieChartBook.setTitle(("Tình trạng sách").toUpperCase());
        pieChartBook.setData(series);
    }

    private void createLineChartRevenue() throws SQLException {
        lineChartRevenue.getData().clear();
        XYChart.Series<String, Number> seriesRevenue = new XYChart.Series();
        XYChart.Series<String, Number> seriesReader = new XYChart.Series();

        List<RevenueReport> revenueReports  = borrowModel.getRevenueReport(connection, timeInterval, DateFormat.parseLocalDateToDateString(startDate.getValue() != null ? startDate.getValue() : LocalDate.now().minusDays(7), Constant.AddTimeToLocalDate.START_DAY), DateFormat.parseLocalDateToDateString(endDate.getValue() != null ? endDate.getValue() : LocalDate.now(), Constant.AddTimeToLocalDate.END_DAY));

        for (RevenueReport revenueReport: revenueReports) {
            seriesRevenue.getData().add(new XYChart.Data<String, Number>(revenueReport.getTime(), (long) Math.ceil((revenueReport.getRevenue() * 0.1) / 1000.0) * 1000));
            seriesReader.getData().add(new XYChart.Data<String, Number>(revenueReport.getTime(), revenueReport.getReader() * 50000));
        }
        seriesRevenue.setName("Doanh thu mượn sách");
        seriesReader.setName("Doanh thu làm thẻ");

        lineChartRevenue.getData().addAll(seriesRevenue, seriesReader);
        lineChartRevenue.setTitle(("Tổng doanh thu").toUpperCase());
        lineChartRevenue.getStylesheets().add(Objects.requireNonNull(AlertCustom.class.getResource("/manage_library/assets/css/chart.css")).toString());
        for (XYChart.Series<String, Number> data : lineChartRevenue.getData()) {
            for (XYChart.Data<String, Number> item : data.getData()) {
                Tooltip.install(item.getNode(), new Tooltip(
                        "Thời gian: " + item.getXValue() + "\n" +
                                "Doanh thu : " + CurrencyFormat.ceilCurrencyFormat((long) item.getYValue())));

                item.getNode().setOnMouseEntered(event -> item.getNode().getStyleClass().add("onHover"));
                item.getNode().setOnMouseExited(event -> item.getNode().getStyleClass().remove("onHover"));
            }
        }
    }

    private void createBarChartBook() {
        List<CategoryBookReport> categoryBookReports = bookModel.getCategoryBookReport(connection);
        XYChart.Series<String, Number> series = new XYChart.Series();
        for (CategoryBookReport categoryBookReport: categoryBookReports) {
            series.getData().add(new XYChart.Data<String, Number>(categoryBookReport.getCategoryName(), categoryBookReport.getTotalBook()));
        }
        series.setName("Số lượng sách");
        barChartBook.getData().add(series);
        barChartBook.setTitle(("Phân loại sách theo thể loại").toUpperCase());

        for (XYChart.Series<String, Number> data : barChartBook.getData()) {
            for (XYChart.Data<String, Number> item : data.getData()) {
                Tooltip.install(item.getNode(), new Tooltip(
                        item.getXValue() + ": " + item.getYValue()));
            }
        }
    }

    private String convertTooltipChart(String name, double value, double totalValue) {
        return name + "\n" + String.format("(%.2f%%)", value * 100 / totalValue);
    }
}
