/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chart;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import login.LoginController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class ChartController implements Initializable {

    @FXML
    private Label exam_label;

    @FXML
    private Label grade_label;

    @FXML
    private Label section_label;

    @FXML
    private ComboBox chart_about_combobox;

    @FXML
    private RadioButton pie_chart_radio_btn;

    @FXML
    private ToggleGroup chart_type;

    @FXML
    private RadioButton bar_chart_radio_btn;

    @FXML
    private ImageView save_chart;

    @FXML
    private VBox grade_chart_vbox;

    @FXML
    private ComboBox exam_choicebox;

    @FXML
    private ComboBox grade_choicebox;

    @FXML
    private Button view_chart_btn;

    @FXML
    private ComboBox section_choicebox;

    @FXML
    private Button full_size_exam_chart_btn;

    @FXML
    private VBox exam_chart_vbox;

    private List<String> grade_id = new ArrayList<String>();
    private String year = LoginController.current_year;
    private StringProperty exam_id = new SimpleStringProperty();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectionComboBox();
        exam_chart_vbox.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                full_size_exam_chart_btn.fire();
            }
        });
        view_chart_btn.setDisable(true);
        listenerForChartAboutComboBox();
        listenerForChartGroup();
    }

    public void selectionComboBox() {
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            String query = "SELECT Exam_id,Name FROM year_"
                    + year + "_exam";
            ResultSet result = conn.createStatement().executeQuery(query);
            exam_choicebox.getItems().clear();
            while (result.next()) {
                exam_choicebox.getItems().add(result.getString(2));
            }
            grade_choicebox.setDisable(true);
            section_choicebox.setDisable(true);
            /*
             adding listener to the exam_choicebox so only after the
             selection of exam_choicebox, the grade_choicebox will be
             available for selection with valid options for that exam
             */
            exam_choicebox.getSelectionModel().selectedItemProperty().addListener(e -> {
                final String sql = "SELECT DISTINCT Grade FROM year_" + year + "_grade "
                        + "WHERE Grade_id IN (SELECT Grade_id FROM "
                        + "year_" + year + "_ledger WHERE Exam_id IN "
                        + "(SELECT Exam_id FROM"
                        + " year_" + year + "_exam WHERE Name = '"
                        + exam_choicebox.getSelectionModel().getSelectedItem().toString()
                        + "'));";
                grade_choicebox.getItems().clear();
                section_choicebox.getItems().clear();
                view_chart_btn.setDisable(true);
                try {
                    final ResultSet res = conn.createStatement().executeQuery(sql);
                    while (res.next()) {
                        grade_choicebox.getItems().add(res.getString(1));
                    }
                    grade_choicebox.setDisable(false);
                } catch (Exception ex) {
                    System.out.println("Exception while addiing listener"
                            + " to exam_choicebox at selectionChoiceBox() "
                            + "at ReportController : " + ex.getMessage());
                }
            });
            grade_choicebox.getSelectionModel().selectedItemProperty().addListener(e -> {
                final String sql = "SELECT DISTINCT Section FROM year_" + year + "_grade "
                        + "WHERE Grade_id IN (SELECT Grade_id FROM "
                        + "year_" + year + "_ledger WHERE Grade_id IN "
                        + "(SELECT Grade_id FROM"
                        + " year_" + year + "_grade WHERE Grade = '"
                        + grade_choicebox.getSelectionModel().getSelectedItem().toString()
                        + "') AND Exam_id IN "
                        + "(SELECT Exam_id FROM"
                        + " year_" + year + "_exam WHERE Name = '"
                        + exam_choicebox.getSelectionModel().getSelectedItem().toString()
                        + "')"
                        + ");";
                section_choicebox.getItems().clear();
                view_chart_btn.setDisable(true);
                try {
                    final ResultSet res = conn.createStatement().executeQuery(sql);
                    section_choicebox.getItems().add("All Section Combined");
                    while (res.next()) {
                        section_choicebox.getItems().add(res.getString(1));
                    }
                    section_choicebox.setDisable(false);
                } catch (Exception ex) {
                    System.out.println("Exception while addiing listener"
                            + "to grade_choicebox at selectionChoiceBox() "
                            + "at ReportController : " + ex.getMessage());
                }
            });
            section_choicebox.getSelectionModel().selectedItemProperty().addListener(e -> {
                view_chart_btn.setDisable(false);
            });

        } catch (Exception e) {
            System.out.println("Exception at selectionChoiceBox() "
                    + "at ReportController : " + e.getMessage());
        }
    }

    private void listenerForChartGroup() {
        chart_type.selectedToggleProperty().addListener(e -> {
            if (chart_about_combobox.getSelectionModel().getSelectedItem() != null) {
                String chart_about = chart_about_combobox.getSelectionModel().getSelectedItem().toString();
                if (!chart_about.isEmpty()) {
                    if (chart_about.equalsIgnoreCase("Pass And Fail Chart")) {
                        if (pie_chart_radio_btn.isSelected()) {
                            passFailPieChart();
                        } else if (bar_chart_radio_btn.isSelected()) {
                            passFailBarChart();
                        }
                    } else if (chart_about.equalsIgnoreCase("Gender Wise Pass And Fail Chart")) {
                        if (pie_chart_radio_btn.isSelected()) {
                            chart_type.selectToggle(bar_chart_radio_btn);
                        } else if (bar_chart_radio_btn.isSelected()) {
                            genderWisePassFailBarChart();
                        }
                    } else if (chart_about.equalsIgnoreCase("Division Based Chart")) {
                        if (pie_chart_radio_btn.isSelected()) {
                            divisionPieChart();
                        } else if (bar_chart_radio_btn.isSelected()) {
                            divisionBarChart();
                        }
                    }
                }
            }
        });

    }

    private void listenerForChartAboutComboBox() {
        chart_about_combobox.getSelectionModel().selectedItemProperty().addListener(e -> {
            if (chart_about_combobox.getSelectionModel().getSelectedItem() != null) {
                if (!"".equals(chart_about_combobox.getSelectionModel().getSelectedItem().toString())) {
                    bar_chart_radio_btn.setSelected(false);
                    pie_chart_radio_btn.setSelected(false);
                    int selection_index = chart_about_combobox.getSelectionModel().getSelectedIndex();
                    switch (selection_index) {
                        case 0:
                            bar_chart_radio_btn.setDisable(false);
                            pie_chart_radio_btn.setDisable(false);
                            chart_type.selectToggle(pie_chart_radio_btn);
                            break;
                        case 1:
                            bar_chart_radio_btn.setDisable(false);
                            pie_chart_radio_btn.setDisable(true);
                            chart_type.selectToggle(bar_chart_radio_btn);
                            break;
                        case 2:
                            bar_chart_radio_btn.setDisable(false);
                            pie_chart_radio_btn.setDisable(false);
                            chart_type.selectToggle(pie_chart_radio_btn);
                            break;
                    }
                }
            }
        });

    }

    public void chartAboutComboBox() {
        chart_about_combobox.getItems().clear();
        chart_about_combobox.getItems().add("Pass And Fail Chart");
        chart_about_combobox.getItems().add("Gender Wise Pass And Fail Chart");
        chart_about_combobox.getItems().add("Division Based Chart");
        chart_about_combobox.getSelectionModel().selectFirst();
    }

    public void examPassFailPieChart() {
        PieChart pie_chart = new PieChart();
        database.Connection.connect();
        Connection conn = database.Connection.conn;
        String exam_id = this.exam_id.get();
        try {
            ChartData chart_data = new ChartData();
            List<List> data = chart_data.getExamPassFailData(exam_id);
            ObservableList<PieChart.Data> pie_chart_data = FXCollections.observableArrayList();
            data.forEach((data1) -> {
                pie_chart_data.add(new PieChart.Data((String) data1.get(0), (Double.valueOf(String.valueOf(data1.get(1))))));
            });
            pie_chart.setData(pie_chart_data);
            Platform.runLater(() -> {
                pie_chart.setTitle(exam_label.getText() + " Pass and Fail");
                pie_chart.setStyle("-fx-text-fill: white;");
                exam_chart_vbox.getChildren().setAll(pie_chart);
            });
        } catch (Exception e) {
            System.out.println("Exception at examPassFailPieChart() "
                    + "at ChartController : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void passFailPieChart() {
        PieChart pie_chart = new PieChart();
        String exam_id = this.exam_id.get();
        try {
            ChartData chart_data = new ChartData();
            List<List> data = chart_data.getGradePassFailData(exam_id, grade_id);
            ObservableList<PieChart.Data> pie_chart_data = FXCollections.observableArrayList();
            data.forEach((data1) -> {
                PieChart.Data current_data = new PieChart.Data((String) data1.get(0), (Double.valueOf(String.valueOf(data1.get(1)))));
                pie_chart_data.add(current_data);

            });
            pie_chart.setData(pie_chart_data);

            Platform.runLater(() -> {
                if (section_label.getText().equalsIgnoreCase("All Section Combined") || section_label.getText().equals("null")) {
                    pie_chart.setTitle("Grade : " + grade_label.getText() + "\n Pass And Fail");
                } else {
                    pie_chart.setTitle("Grade : " + grade_label.getText() + " Section : " + section_label.getText() + " \n Pass And Fail");
                }
                grade_chart_vbox.getChildren().setAll(pie_chart);
            });
        } catch (Exception e) {
            System.out.println("Exception at passFailPieChart() "
                    + "at ChartController : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void passFailBarChart() {
        CategoryAxis x_axis = new CategoryAxis();
        NumberAxis y_axis = new NumberAxis();
        BarChart<String, Number> bar_chart = new BarChart<>(x_axis, y_axis);
        x_axis.setLabel("Result");
        y_axis.setLabel("Number Of Students");
        XYChart.Series result_series = new XYChart.Series();
        if (section_label.getText().equalsIgnoreCase("All Section Combined") || section_label.getText().equals("null")) {
            result_series.setName("Grade : " + grade_label.getText());
            bar_chart.setTitle("Grade : " + grade_label.getText() + "\n Pass And Fail");
        } else {
            result_series.setName("Grade : " + grade_label.getText() + " '" + section_label.getText() + "'");
            bar_chart.setTitle("Grade : " + grade_label.getText() + " Section : " + section_label.getText() + " \n Pass And Fail");
        }
        ChartData chart_data = new ChartData();
        List<List> data = chart_data.getGradePassFailData(exam_id.get(), grade_id);
        double maximum = 0;
        for (List d : data) {
            XYChart.Data<String, Number> single_data = new XYChart.Data<String, Number>((String) d.get(0), (Integer) d.get(1));
            result_series.getData().add(single_data);
            single_data.nodeProperty().addListener(e -> {
                displayLabelForData(single_data);
            });
            if (maximum < (Integer) d.get(1)) {
                maximum = (Integer) d.get(1);
            }
        }
        bar_chart.getData().addAll(result_series);
        //assigning the maximum*1.3 value in the chart as the range
        maximum = maximum * 1.3;
        y_axis.setAutoRanging(false);
        y_axis.setUpperBound(Math.round(maximum / 5) * 5);
        grade_chart_vbox.getChildren().setAll(bar_chart);

    }

    public void divisionPieChart() {
        PieChart pie_chart = new PieChart();
        String exam_id = this.exam_id.get();
        try {
            ChartData chart_data = new ChartData();
            List<List> data = chart_data.getDivisionData(exam_id, grade_id);
            ObservableList<PieChart.Data> pie_chart_data = FXCollections.observableArrayList();
            data.forEach((data1) -> {
                pie_chart_data.add(new PieChart.Data((String) data1.get(0), (Double.valueOf(String.valueOf(data1.get(1))))));
            });
            pie_chart.setData(pie_chart_data);
            Platform.runLater(() -> {
                if (section_label.getText().equalsIgnoreCase("All Section Combined") || section_label.getText().equals("null")) {
                    pie_chart.setTitle("Grade : " + grade_label.getText() + "\n Division");
                } else {
                    pie_chart.setTitle("Grade : " + grade_label.getText() + " Section : " + section_label.getText() + " \n Division");
                }
                grade_chart_vbox.getChildren().setAll(pie_chart);
            });
        } catch (Exception e) {
            System.out.println("Exception at divisionPieChart() "
                    + "at ChartController : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void divisionBarChart() {
        CategoryAxis x_axis = new CategoryAxis();
        NumberAxis y_axis = new NumberAxis();
        BarChart<String, Number> bar_chart = new BarChart<>(x_axis, y_axis);
        x_axis.setLabel("Division");
        y_axis.setLabel("Number Of Students");
        XYChart.Series division_series = new XYChart.Series();
        if (section_label.getText().equalsIgnoreCase("All Section Combined") || section_label.getText().equals("null")) {
            division_series.setName("Grade : " + grade_label.getText());
            bar_chart.setTitle("Grade : " + grade_label.getText() + "\n Division ");
        } else {
            division_series.setName("Grade : " + grade_label.getText() + " '" + section_label.getText() + "'");
            bar_chart.setTitle("Grade : " + grade_label.getText() + " Section : " + section_label.getText() + " \n Division");
        }
        ChartData chart_data = new ChartData();
        List<List> data = chart_data.getDivisionData(exam_id.get(), grade_id);
        double maximum = 0;
        for (List d : data) {
            XYChart.Data<String, Number> single_data = new XYChart.Data<String, Number>((String) d.get(0), (Integer) d.get(1));
            division_series.getData().add(single_data);
            single_data.nodeProperty().addListener(e -> {
                displayLabelForData(single_data);
            });
            if (maximum < (Integer) d.get(1)) {
                maximum = (Integer) d.get(1);
            }
        }
        //assigning maximum * 1.3 as the maximum y axis bound
        maximum = maximum * 1.3;
        y_axis.setAutoRanging(false);
        y_axis.setUpperBound(Math.round(maximum / 5) * 5);
        bar_chart.getData().addAll(division_series);
        grade_chart_vbox.getChildren().setAll(bar_chart);

    }

    public void genderWisePassFailBarChart() {
        CategoryAxis x_axis = new CategoryAxis();
        NumberAxis y_axis = new NumberAxis();
        BarChart<String, Number> bar_chart = new BarChart<>(x_axis, y_axis);
        x_axis.setLabel("Gender");
        y_axis.setLabel("Number Of Students");
        //male series of data
        XYChart.Series male_series = new XYChart.Series();
        if (section_label.getText().equalsIgnoreCase("All Section Combined") || section_label.getText().equals("null")) {
            male_series.setName("Male : Grade : " + grade_label.getText());
            bar_chart.setTitle("Grade : " + grade_label.getText() + "\n Genderwise Pass And Fail ");
        } else {
            male_series.setName("Grade : " + grade_label.getText() + " '" + section_label.getText() + "'");
            bar_chart.setTitle("Male : Grade : " + grade_label.getText() + " Section : " + section_label.getText() + " \n Genderwise Pass And Fail");
        }
        ChartData chart_data = new ChartData();
        List<List> data = chart_data.getMalePassFailData(exam_id.get(), grade_id);
        double maximum = 0;
        for (List d : data) {
            XYChart.Data<String, Number> single_data = new XYChart.Data<String, Number>((String) d.get(0), (Integer) d.get(1));
            male_series.getData().add(single_data);
            single_data.nodeProperty().addListener(e -> {
                displayLabelForData(single_data);
            });
            if (maximum < (Integer) d.get(1)) {
                maximum = (Integer) d.get(1);
            }
        }
        //male series of data
        XYChart.Series female_series = new XYChart.Series();
        if (section_label.getText().equalsIgnoreCase("All Section Combined") || section_label.getText().equals("null")) {
            female_series.setName("Female : Grade : " + grade_label.getText());

        } else {
            female_series.setName("Female : Grade : " + grade_label.getText() + " '" + section_label.getText() + "'");

        }
        chart_data = new ChartData();
        data = chart_data.getFemalePassFailData(exam_id.get(), grade_id);
        for (List d : data) {
            XYChart.Data<String, Number> single_data = new XYChart.Data<String, Number>((String) d.get(0), (Integer) d.get(1));
            female_series.getData().add(single_data);
            single_data.nodeProperty().addListener(e -> {
                displayLabelForData(single_data);
            });
            if (maximum < (Integer) d.get(1)) {
                maximum = (Integer) d.get(1);
            }
        }
        //setting the maximum bound of y-axis, as maximum*1.3
        maximum = 1.3 * maximum;
        y_axis.setAutoRanging(false);
        y_axis.setUpperBound(Math.round(maximum * 5) / 5);
        bar_chart.getData().addAll(male_series, female_series);
        grade_chart_vbox.getChildren().setAll(bar_chart);
    }

    @FXML
    private void clickSaveChartImage(MouseEvent event) {
        try {
            if (grade_chart_vbox.getChildren().get(0) != null) {
                FileChooser file_chooser = new FileChooser();
                file_chooser.setInitialFileName("Filename.png");
                file_chooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("All", "*.*"),
                        new FileChooser.ExtensionFilter("PNG (*.Png)", "*.png")
                );
                file_chooser.setTitle("Save Charts");
                file_chooser.setInitialDirectory(
                        new File(System.getProperty("user.home"))
                );
                Stage stage = (Stage) grade_chart_vbox.getScene().getWindow();
                File chart = file_chooser.showSaveDialog(stage);
                if (chart != null) {
                    //snapshot of borderpane is take and is assigned to WritableImage
                    WritableImage chart_image = grade_chart_vbox.snapshot(new SnapshotParameters(), null);
                    ImageIO.write(SwingFXUtils.fromFXImage(chart_image, null), "png", chart);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception at clickSaveChartImage() "
                    + "at ChartController : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void clickViewChartBtn(MouseEvent event) {
        exam_label.setText(exam_choicebox.getSelectionModel().getSelectedItem().toString());
        grade_label.setText(grade_choicebox.getSelectionModel().getSelectedItem().toString());
        section_label.setText(section_choicebox.getSelectionModel().getSelectedItem().toString());
        //finding the exam_id of the selected exam
        database.Connection.connect();
        Connection conn = database.Connection.conn;
        String query = "SELECT Exam_id FROM year_" + year + "_exam WHERE Name = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, exam_label.getText());
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                this.exam_id.set(String.valueOf(result.getInt(1)));
            }
        } catch (Exception e) {
            System.out.println("Exception while finding exam_id "
                    + "at clickViewChartBtn(MouseEvent event) "
                    + "at clickViewChartBtn :" + e.getMessage());
        }
        //finding the grade_id of the selected grade and section
        String grade = grade_label.getText();
        String section = section_label.getText();
        try {
            conn = database.Connection.conn;
            PreparedStatement pst = null;
            query = "SELECT Grade_id FROM year_" + year + "_grade WHERE "
                    + "Grade = ? ";
            if (!section.equalsIgnoreCase("All Section Combined")) {
                query = query + " AND Section = ?";
            }
            pst = conn.prepareStatement(query);
            pst.setString(1, grade);
            if (!section.equalsIgnoreCase("All Section Combined")) {
                pst.setString(2, section);
            }
            ResultSet result = pst.executeQuery();
            if (grade_id != null) {
                grade_id.clear();
            }
            while (result.next()) {
                this.grade_id.add(String.valueOf(result.getInt(1)));
            }
        } catch (Exception e) {
            System.out.println("Exception while finding grade_id list "
                    + "at clickViewChartBtn() "
                    + "at ChartController : " + e.getMessage());
            e.printStackTrace();
        }
        examPassFailPieChart();
        chartAboutComboBox();
    }

    @FXML
    private void clickViewFullSizeExamChartBtn(MouseEvent event) {
        try {
            if (exam_chart_vbox.getChildren().get(0) != null) {
                fullSizeExamChart();
            }
        } catch (Exception e) {
            System.out.println("Exception at clickViewFullSizeExamChartBtn(MouseEvent event) "
                    + "at ChartController : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Stage fullSizeExamChart() {
        Label save_label = new Label("Save :");
        save_label.setFont(Font.font(18));
        VBox inner_vbox = new VBox();
        ImageView image_view = new ImageView();
        image_view.setFitHeight(60);
        image_view.setFitWidth(60);
        Image img = new Image("/images/download.jpg");
        image_view.setImage(img);
        image_view.setStyle("-fx-cursor: hand;");
                inner_vbox.getChildren().addAll(save_label, image_view);
        VBox vbox = new VBox();
        vbox.getChildren().add(inner_vbox);
        //setting up the pie chart
        VBox pie_chart_vbox = new VBox();
        PieChart pie_chart = new PieChart();
        database.Connection.connect();
        Connection conn = database.Connection.conn;
        String exam_id = null;
        String query = "SELECT Exam_id FROM year_" + year + "_exam WHERE Name = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, exam_label.getText());
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                exam_id = String.valueOf(result.getInt(1));
            }
            ChartData chart_data = new ChartData();
            List<List> data = chart_data.getExamPassFailData(exam_id);
            ObservableList<PieChart.Data> pie_chart_data = FXCollections.observableArrayList();
            data.forEach((data1) -> {
                pie_chart_data.add(new PieChart.Data((String) data1.get(0), (Double.valueOf(String.valueOf(data1.get(1))))));
            });
            pie_chart.setData(pie_chart_data);
            pie_chart.setTitle(exam_label.getText() + " Pass and Fail");
            pie_chart_vbox.getChildren().setAll(pie_chart);
        } catch (Exception e) {
            System.out.println("Exception at examPassFailPieChart() "
                    + "at ChartController : " + e.getMessage());
        }
        vbox.getChildren().add(pie_chart_vbox);
        //saving the chart when image_view is clicked
        image_view.setOnMouseClicked(event->{
            try {            
                FileChooser file_chooser = new FileChooser();
                file_chooser.setInitialFileName("Filename.png");
                file_chooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("All", "*.*"),
                        new FileChooser.ExtensionFilter("PNG (*.Png)", "*.png")
                );
                file_chooser.setTitle("Save Charts");
                file_chooser.setInitialDirectory(
                        new File(System.getProperty("user.home"))
                );
                Stage stage = (Stage) pie_chart_vbox.getScene().getWindow();
                File chart = file_chooser.showSaveDialog(stage);
                if (chart != null) {
                    //snapshot of borderpane is take and is assigned to WritableImage
                    WritableImage chart_image = pie_chart_vbox.snapshot(new SnapshotParameters(), null);
                    ImageIO.write(SwingFXUtils.fromFXImage(chart_image, null), "png", chart);
                }
            
        } catch (Exception e) {
            System.out.println("Exception at clickSaveChartImage() "
                    + "at ChartController : " + e.getMessage());
            e.printStackTrace();
        }
        });
        pie_chart_vbox.prefWidthProperty().bind(vbox.widthProperty().multiply(0.7));
        vbox.setPadding(new Insets(10, 10, 10, 10));
        Scene scene = new Scene(vbox, 800, 600);
        Stage window = new Stage();
        window.setScene(scene);
        window.initModality(Modality.APPLICATION_MODAL);
        window.show();
        return window;
    }

    private void displayLabelForData(XYChart.Data<String, Number> data) {
        final Node node = data.getNode();
        final Text dataText = new Text(data.getYValue() + "");
        node.parentProperty().addListener(new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> ov, Parent oldParent, Parent parent) {
                Group parentGroup = (Group) parent;
                parentGroup.getChildren().add(dataText);
            }
        });

        node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
                dataText.setLayoutX(
                        Math.round(
                                bounds.getMinX() + bounds.getWidth() / 2 - dataText.prefWidth(-1) / 2
                        )
                );
                dataText.setLayoutY(
                        Math.round(
                                bounds.getMinY() - dataText.prefHeight(-1) * 0.5
                        )
                );
            }
        });
    }
}
