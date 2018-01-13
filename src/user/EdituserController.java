/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import login.LoginController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class EdituserController implements Initializable {

    @FXML
    private JFXTextField name;

    @FXML
    private JFXTextField phone;

    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField password;

    @FXML
    private Label password_strength;

    @FXML
    private JFXPasswordField re_enter_password;

    @FXML
    private ComboBox user_type;

    @FXML
    private Button save_btn;

    public IntegerProperty user_id = new SimpleIntegerProperty();
    private String current_username = "";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        user_type.getItems().addAll("Admin", "Regular");
        listenerForUserId();
        listenerForReEnterPassword();
        listenerForPassword();
        listenerForUsername();
        makePhoneTextFieldNumeric();
    }

    public void listenerForUserId() {
        user_id.addListener(e -> {
            getValues();
        });
    }

    public void getValues() {
        if (user_id.get() != 0) {
            int id = user_id.get();
            String query = "SELECT * FROM User WHERE User_id = " + id + " ;";
            try {
                Connection conn = database.Connection.conn;
                ResultSet result = conn.createStatement().executeQuery(query);
                while (result.next()) {
                    name.setText(result.getString("Name"));
                    phone.setText(result.getString("Phone"));
                    user_type.getSelectionModel().select(result.getString("Type"));
                    user_type.setDisable(true);
                    current_username = result.getString("Username");
                    username.setText(result.getString("Username"));
                }
            } catch (Exception e) {
                System.out.println("Exception at setValues() at EdituserController "
                        + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private boolean areAllFieldsFilled() {
        boolean filled = !name.getText().isEmpty()
                && !phone.getText().isEmpty()
                && !username.getText().isEmpty()
                && !password.getText().isEmpty()
                && !re_enter_password.getText().isEmpty()
                && !(user_type.getSelectionModel().getSelectedItem() == null);
        return filled;
    }

    private void listenerForReEnterPassword() {
        re_enter_password.textProperty().addListener(e -> {
            String pass = password.getText();
            String re = re_enter_password.getText();
            if (re.equals(pass)) {
                re_enter_password.setStyle("-fx-background-color:white;");
                save_btn.setDisable(false);
            } else {
                re_enter_password.setStyle("-fx-background-color:#DC143C;");
                save_btn.setDisable(true);
            }
        });
    }

    private void listenerForPassword() {
        password.textProperty().addListener(e -> {
            String pass = password.getText();
            String re = re_enter_password.getText();
            if (re.equals(pass)) {
                re_enter_password.setStyle("-fx-background-color:white;");
                save_btn.setDisable(false);
            } else {
                re_enter_password.setStyle("-fx-background-color:#DC143C;");
                save_btn.setDisable(true);
            }
            if (pass.isEmpty()) {
                password_strength.setVisible(false);
            } else {
                password_strength.setVisible(true);
                int strength = checkPasswordStrength(pass);
                password_strength.setText("Strength : " + strength + "%");
                if (strength <= 25) {
                    password_strength.setTextFill(Color.RED);
                } else if (strength <= 50) {
                    password_strength.setTextFill(Color.ORANGE);
                } else if (strength <= 75) {
                    password_strength.setTextFill(Color.GREENYELLOW);
                } else if (strength <= 100) {
                    password_strength.setTextFill(Color.GREEN);
                }
            }
        });
    }

    private void listenerForUsername() {
        this.username.textProperty().addListener(e -> {
            if (!username.getText().isEmpty()) {
                if (!username.getText().equals(current_username)) {
                    String username = this.username.getText();
                    try {
                        String query = "SELECT * FROM User "
                                + "WHERE Username = '" + username + "' ;";
                        Connection conn = database.Connection.conn;
                        ResultSet result = conn.createStatement().executeQuery(query);
                        boolean present = false;
                        while (result.next()) {
                            present = true;
                        }
                        if (present) {
                            this.username.setStyle("-fx-background-color:#DC143C;");
                            save_btn.setDisable(true);
                        } else {
                            this.username.setStyle("-fx-background-color:white;");
                            save_btn.setDisable(false);
                        }
                    } catch (Exception ex) {
                        System.out.println("Exception at listenerForUsername()  "
                                + "at Username : " + ex.getMessage());
                        ex.printStackTrace();
                        this.username.setStyle("-fx-background-color:#DC143C;");
                        save_btn.setDisable(true);
                    }
                } else {
                    this.username.setStyle("-fx-background-color:white;");
                    save_btn.setDisable(false);
                }
            } else {
                this.username.setStyle("-fx-background-color:white;");
                save_btn.setDisable(false);
            }
        });
    }

    @FXML
    private void clickSaveBtn(MouseEvent event) {
        if (areAllFieldsFilled()) {
            String name = this.name.getText();
            String username = this.username.getText();
            String pass = this.password.getText();
            String type = this.user_type.getSelectionModel().getSelectedItem().toString();
            String phone = this.phone.getText();
            try {
                Connection conn = database.Connection.conn;
                String year = LoginController.current_year;
                String query = "UPDATE User SET "
                        + "Name = ? "
                        + ",Username = ?"
                        + ",Password = ?"
                        + ",Phone = ?"
                        + ",Type = ? "
                        + "WHERE User_id = ?";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, name);
                pst.setString(2, username);
                pst.setString(3, pass);
                pst.setString(4, phone);
                pst.setString(5, type);
                pst.setInt(6, user_id.get());
                pst.execute();
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setHeaderText("User Updated successfully !!!");
                a.setContentText("The user was updated. \n Username : " + username
                        + "\nName : " + name + "\nPassword : " + pass);
                a.show();
                Stage stage = (Stage)save_btn.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                System.out.println("Exception at clickSaveBtn() "
                        + "at EdituserController " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Fill all Required Fields");
            a.setContentText("All Fields are required please fill them all ");
            a.show();
        }
    }

    private int checkPasswordStrength(String password) {
        int strengthPercentage = 0;
        String[] partialRegexChecks = {".*[a-z]+.*", // lower
            ".*[A-Z]+.*", // upper
            ".*[\\d]+.*", // digits
            ".*[@#$%]+.*" // symbols
    };
        if (password.matches(partialRegexChecks[0])) {
            strengthPercentage += 25;
        }
        if (password.matches(partialRegexChecks[1])) {
            strengthPercentage += 25;
        }
        if (password.matches(partialRegexChecks[2])) {
            strengthPercentage += 25;
        }
        if (password.matches(partialRegexChecks[3])) {
            strengthPercentage += 25;
        }

        return strengthPercentage;
    }
    
private void makePhoneTextFieldNumeric() {
        phone.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    char ch = phone.getText().charAt(oldValue.intValue());
                    // Check if the new character is the number or other's
                    if (!(ch >= '0' && ch <= '9' || ch == '+' || ch == '-' || ch == ',')) {
                        // if it's not number then just setText to previous one
                        phone.setText(phone.getText().substring(0, phone.getText().length() - 1));
                    }
                }
            }
        });

    }

}
