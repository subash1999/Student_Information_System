/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package settings.newSession;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import settings.SettingsController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class Addsession1Controller extends SettingsController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private ComboBox session_year;
    protected static String session;
    @FXML
    private DatePicker ad_year_start;
    protected static String session_start;
    @FXML
    private DatePicker ad_year_end;
    protected static String session_end;
    @FXML
    private Button next_btn;
    @FXML
    private Label check;

    protected static ArrayList<String> session_name;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        next_btn.setDisable(true);
        check.setVisible(false);
        comboBox();
        onNextBtnClicked();
        onNextBtnEnterPressed();
    }

    public void onNextBtnEnterPressed() {
        next_btn.setOnKeyPressed(e -> {
            if (next_btn.isFocused()) {
                if (e.getCode() == KeyCode.ENTER) {
                    next_btn.fire();
                }
            }
        });

    }

    @FXML
    private void onNextBtnClicked() {
        next_btn.setOnAction((ActionEvent e) -> {
            if (!session_year.getSelectionModel().getSelectedItem().toString().isEmpty()
                    && !ad_year_start.getValue().toString().isEmpty()
                    && !ad_year_end.getValue().toString().isEmpty()) {
                if (checkStartEnd()) {
                    session = session_year.getSelectionModel().getSelectedItem().toString();
                    session_start = ad_year_start.getValue().toString();
                    session_end = ad_year_end.getValue().toString();
                    Parent root = null;
                    FXMLLoader loader = new FXMLLoader();
                    try {
                        root = FXMLLoader.load(getClass().getResource("/settings/newSession/addsession2.fxml"));
                        Scene scene = new Scene(root);
                        Stage window = new Stage();
                        //closing the current window
                        window = (Stage) next_btn.getScene().getWindow();
                        window.close();
                        //new window of the step 2 is being displayed
                        window.setScene(scene);
                        window.setResizable(false);
                        window.setTitle("Create New Session / STEP:-2");
                        //window.initModality(Modality.APPLICATION_MODAL);
                        window.show();
                        window.setOnCloseRequest(f -> {
                            Platform.runLater(() -> {
                                Stage stage = new Stage();
                                try {
                                    Parent parent = FXMLLoader.load(getClass().getResource("/settings/settings.fxml"));
                                    Scene sc = new Scene(parent);
                                    stage.setScene(sc);
                                    stage.show();
                                } catch (IOException ex) {
                                    System.out.println("Exception at onNextBtnClicked() "
                                            + "at Addsession1Controller : " + ex.getMessage());
                                    ex.printStackTrace();
                                }

                            });

                        });

                    } catch (Exception ex) {
                        System.out.println("Exception at addsession2 resource getting : " + ex.getMessage());
                    }
                    Stage window = new Stage();

                } else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText("End date is before start date");
                    alert.setContentText("Starting date always occurs before the end date");
                    alert.show();
                }
            } else {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setHeaderText("Fill all");
                alert.setContentText("Please fill up all the necessary information");
                alert.show();
            }
        });
    }

    public boolean checkStartEnd() {
        int i = ad_year_end.getValue().compareTo(ad_year_start.getValue());

        if (i > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void comboBox() {
        //making comboBox editable i.e new values can be kept on combobox
        session_year.setEditable(true);
        //getting the year from calendar

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR) + 57 - 5;
        ArrayList<String> available_session = new ArrayList<String>();
        for (int i = year; i < (year + 10); i++) {
            available_session.add(String.valueOf(i));
        }
        session_name = new ArrayList<String>();
        database.Connection.connect();
        Connection conn = database.Connection.conn;
        String query = "select `Year` from session";
        ResultSet result = null;
        try {
            result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                session_name.add(result.getString("Year"));
            }
            session_name.add("First Use");            
        } catch (Exception f) {
            System.out.println("Exception at checking session name");
            System.out.println(f.getMessage());
        }
        //deleting the present session from the already made session
        available_session.removeAll(session_name);
        session_year.getItems().addAll(available_session);

        session_year.getSelectionModel().selectedItemProperty().addListener(e -> {
            if (session_year.getSelectionModel().getSelectedItem().toString().isEmpty()) {
                check.setVisible(false);

            } else {
                check.setVisible(true);
            }
            try {
                for (String s : session_name) {
                    if (!s.equalsIgnoreCase(session_year.getSelectionModel().getSelectedItem().toString())) {
                        check.setText("√");
                        check.setStyle("-fx-text-fill : green;");
                        next_btn.setDisable(false);

                    } else {
                        check.setText("X");
                        check.setStyle("-fx-text-fill : red;");
                        next_btn.setDisable(true);
                        break;
                    }
                }
            } catch (Exception g) {
                System.out.println("Exception message : " + g.getMessage());
            }
        });

    }
}
