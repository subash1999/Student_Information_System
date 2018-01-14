/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import student.StudentController;
import teacher.TeacherController;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import ledger.LedgerController;
import login.LoginController;
import settings.SettingsController;
import user.EdituserController;
import user.PasswordDialog;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class MainwindowController implements Initializable {

    /**
     * Initializes the controller class.
     */

    @FXML
    private MenuBar menubar;

    @FXML
    private Menu file;

    @FXML
    private MenuItem view_session_menu_item;

    @FXML
    private MenuItem change_session_menuu_item;

    @FXML
    private MenuItem close_menu_item;

    @FXML
    private Menu edit;

    @FXML
    private MenuItem delete_menu_item;

    @FXML
    private Menu help;

    @FXML
    private MenuItem about_menu_item;

    @FXML
    private Menu teacher;

    @FXML
    private MenuItem settings_menu_item;

    @FXML
    private MenuItem add;

    @FXML
    private Menu chat;

    @FXML
    private MenuItem open_chat_menu_item;

    @FXML
    private Label current_time;

    @FXML
    private Label current_date;

    @FXML
    private Label current_user;

    @FXML
    private Label current_year;

    @FXML
    private Label current_user1;

    @FXML
    private ImageView student_image;

    @FXML
    private ImageView teacher_image;

    @FXML
    private ImageView report_image;

    @FXML
    private ImageView ledger_image;

    @FXML
    private ImageView chart_image;

    @FXML
    private AnchorPane backup_restore_anchorpane;

    @FXML
    private ImageView backup_image;

    @FXML
    private ImageView subject_image;

    @FXML
    private ImageView grade_image;

    @FXML
    private AnchorPane user_anchorpane;

    @FXML
    private ImageView user_image;

    @FXML
    private AnchorPane settings_anchorpane;

    @FXML
    private ImageView setting_image;

    @FXML
    private AnchorPane exam_management_anchorpane;

    @FXML
    private ImageView exam_image;

    @FXML
    private AnchorPane organization_details_anchorpane;

    @FXML
    private ImageView organization_image;

    @FXML
    private TextField display;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(!LoginController.user_type.equalsIgnoreCase("Admin")){
            exam_management_anchorpane.setVisible(false);
            user_anchorpane.setVisible(false);
            settings_anchorpane.setVisible(false);
            organization_details_anchorpane.setVisible(false);
            backup_restore_anchorpane.setVisible(false);
        }
        dateAndTime();
        current_user.setText("User : " + LoginController.current_user);
        current_year.setText("Session : " + LoginController.current_year);
    }

//GADGETS PORTION AT THE END OF THE CODE i.e The gadgets included are
//i) date and time display
//ii)Calculator
    //date and time portion of the main window
    public void dateAndTime() {
        //making the digital clock and assigning to the current_time label
        current_time.setFont(Font.font("Times New Roman", 32));
        final Timeline digitalTime = new Timeline(
                new KeyFrame(Duration.seconds(0), (ActionEvent actionEvent) -> {
                    Date date = new Date();
                    DateFormat time_format = new SimpleDateFormat("hh:mm:ss a");
                    current_time.setText(time_format.format(date));
                }),
                new KeyFrame(Duration.seconds(1))
        );
        digitalTime.setCycleCount(Animation.INDEFINITE);
        digitalTime.play();
        //assiging the current data to the current date label
        Date date = new Date();
        DateFormat date_format;
        date_format = new SimpleDateFormat("dd/MM/yyyy");
        current_date.setFont(Font.font("Times New Roman", 21));
        Calendar c = Calendar.getInstance();
        String temp_date = date_format.format(date);
        date = c.getTime();
        String day_of_week = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        current_date.setText(day_of_week + ", " + temp_date);
    }

    //date and time portion ended
    //calculator job below
    private BigDecimal left;
    private String selectedOperator;
    private boolean numberInputting;

    @FXML
    private void handleOnAnyButtonClicked(ActionEvent evt) {
        Button button = (Button) evt.getSource();
        final String buttonText = button.getText();
        if (buttonText.equals("C") || buttonText.equals("AC")) {
            if (buttonText.equals("AC")) {
                left = BigDecimal.ZERO;
            }
            selectedOperator = "";
            numberInputting = false;
            display.setText("0");;
            return;
        }
        if (buttonText.matches("[0-9\\.]")) {
            if (!numberInputting) {
                numberInputting = true;
                display.clear();
            }
            display.appendText(buttonText);
            return;
        }
        if (buttonText.matches("[＋－×÷]")) {
            left = new BigDecimal(display.getText());
            selectedOperator = buttonText;
            numberInputting = false;
            return;
        }
        if (buttonText.equals("=")) {
            final BigDecimal right = numberInputting ? new BigDecimal(display.getText()) : left;
            left = calculate(selectedOperator, left, right);
            display.setText(left.toString());
            numberInputting = false;
            return;
        }
    }

    private BigDecimal calculate(String operator, BigDecimal left, BigDecimal right) {
        switch (operator) {
            case "＋":
                return left.add(right);
            case "－":
                return left.subtract(right);
            case "×":
                return left.multiply(right);
            case "÷":
                return left.divide(right);
            default:
        }
        return right;
    }
//calculator code portion ended

    // set on click on images
    @FXML
    void backupRestoreClicked(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/backupRestore/backupRestore.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Backup / Restore");
            stage.show();
        } catch (Exception e) {
            System.out.println("Exception at backupRestoreClicked(MouseEvent event) "
                    + "at MainwindowController : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void chartsClicked(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/chart/chart.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Charts");
            stage.show();
        } catch (Exception e) {
            System.out.println("Exception at chartsClicked(MouseEvent event) "
                    + "at MainwindowController : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void examManagementClicked(MouseEvent event) {
            try {
            Parent root = FXMLLoader.load(getClass().getResource("/examManagement/ExamManagement.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Exam Management");
            stage.show();
        } catch (Exception e) {
            System.out.println("Exception at examManagementClicked(MouseEvent event) "
                    + "at MainwindowController : " + e.getMessage());
            e.printStackTrace();
        }    
    }

    @FXML
    void ledgerClicked(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ledger/ledger.fxml"));

            Parent root1 = null;
            root1 = fxmlLoader.load();
            LedgerController controller = (LedgerController) fxmlLoader.getController();
            //passing the id of the selected student to the studentdetail.fxml file
            Stage stage = new Stage();
            stage.setTitle("Ledger");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error while loading the file at ledgerClicked() "
                    + "at MainwidowController : " + ex.getMessage());
        }
    }

    @FXML
    void organizationDetailsClicked(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/organization/organization.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Organization Details");
            stage.show();
        } catch (Exception e) {
            System.out.println("Exception at organizationDetailsClicked(MouseEvent event) "
                    + "at MainwindowController : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void reportClicked(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/report/report.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Report Generator");
            stage.show();
        } catch (Exception e) {
            System.out.println("Exception at reportClicked(MouseEvent event) "
                    + "at MainwindowController : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void settingsClicked(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/settings/settings.fxml"));

            Parent root1 = null;
            root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error while loading the file at settingsClicked "
                    + "at MainwindowController : " + ex.getMessage());
        }
    }

    @FXML
    void studentClicked(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/student/student.fxml"));

            Parent root1 = null;
            root1 = fxmlLoader.load();
            StudentController controller = (StudentController) fxmlLoader.getController();
            //passing the id of the selected student to the studentdetail.fxml file
            Stage stage = new Stage();
            stage.setTitle("Students");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error while loading the file at studentClicked() "
                    + "at MainwindowController : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    void subjectInformationClicked(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/settings/settings.fxml"));
            Parent root1;
            root1 = fxmlLoader.load();
            SettingsController controller = (SettingsController) fxmlLoader.getController();
            controller.settings_tabpane.getSelectionModel().select(2);
            ObservableList<Tab> tab = controller.settings_tabpane.getTabs();
            for (int i = 0; i < tab.size(); i++) {
                Tab t = tab.get(i);
                if (i != 2) {
                    t.setDisable(true);
                }
            }
            Stage stage = new Stage();
            stage.setTitle("Subjects");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error while loading the file at subjectInformationClicked "
                    + "at MainwindowController : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    void teacherClicked(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/teacher/teacher.fxml"));
            Parent root1 = null;
            root1 = fxmlLoader.load();
            TeacherController controller = (TeacherController) fxmlLoader.getController();
            //passing the id of the selected student to the studentdetail.fxml file
            Stage stage = new Stage();
            stage.setTitle("Teacher");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error while loading the file at "
                    + "teacherClicked() at MainwindowController : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    void gradeClicked(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/settings/settings.fxml"));
            Parent root1;
            root1 = fxmlLoader.load();
            SettingsController controller = (SettingsController) fxmlLoader.getController();
            controller.settings_tabpane.getSelectionModel().select(1);
            ObservableList<Tab> tab = controller.settings_tabpane.getTabs();
            for (int i = 0; i < tab.size(); i++) {
                Tab t = tab.get(i);
                if (i != 1) {
                    t.setDisable(true);
                }
            }
            Stage stage = new Stage();
            stage.setTitle("Grades");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error while loading the file at gradeInformationClicked "
                    + "at MainwindowController : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    void userClicked(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/user/user.fxml"));
            Parent root1 = null;
            root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Users");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error while loading the file at "
                    + "teacherClicked() at MainwindowController : " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    

    @FXML
    void changePasswordClicked(MouseEvent event) {
        int user_id = 0;
        PasswordDialog pwd_dialog = new PasswordDialog();
        String user = LoginController.current_user;
        pwd_dialog.setTitle("Verify User");
        pwd_dialog.setHeaderText("Enter the Password For \n Username : "
                + user);
        Optional<String> password = pwd_dialog.showAndWait();
        String password_correct = "";
        if (/*checking if cancel btn is pressed */!password.get().isEmpty()) {
            try {
                Connection conn = database.Connection.conn;
                String query = "SELECT * FROM User WHERE "
                        + "Username = BINARY ?";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, user);
                ResultSet result = pst.executeQuery();
                while (result.next()) {
                    password_correct = result.getString("Password");
                    user_id = result.getInt("User_id");
                }

            } catch (Exception e) {
                System.out.println("First Exception  at clickEditUser() "
                        + "at UserController "
                        + " : " + e.getMessage());
                e.printStackTrace();
            }
            if (password.get().equals(password_correct)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/edituser.fxml"));
                    Parent root = loader.load();
                    EdituserController controller = loader.<EdituserController>getController();
                    controller.user_id.set(user_id);
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setTitle("Edit User");
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setScene(scene);
                    stage.show();
                } catch (Exception e) {
                    System.out.println("Last Exception at clickEditUser()"
                            + " at UserController "
                            + " : " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText("Wrong password");
                a.setContentText("Wrong Password For \n Username : " + user);
                a.show();
            }
        }
    }
///////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////

    /*
    Now Menu Items clicked handling
     */
    @FXML
    void viewSettingsMenuItemClicked(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/settings/settings.fxml"));
            Parent root1;
            root1 = fxmlLoader.load();
            //passing the id of the selected student to the studentdetail.fxml file
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error while loading the file at viewSettingsMenuItemClicked "
                    + "at MainwindowController : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    void closeMenuItemClicked(ActionEvent event) {
        Stage window = (Stage) current_date.getScene().getWindow();
        Alert close = new Alert(Alert.AlertType.CONFIRMATION);
        close.setTitle("Confirm Close Request");
        close.setHeaderText("Do you want to close this application");
        close.setContentText("Closing this window will cause all the open windows to close "
                + "because this is the main window of this application");
        Optional<ButtonType> res = close.showAndWait();
        if (res.get() == ButtonType.OK) {
            Platform.exit();
            // ... user chose OK
        } else {
            close.close();
        }

    }

    @FXML
    void addTeacherMenuItemOnAction(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/teacher/addteacher.fxml"));
            Parent root1;
            root1 = fxmlLoader.load();
            //passing the id of the selected student to the studentdetail.fxml file
            Stage stage = new Stage();
            stage.setTitle("Add Teacher");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error while loading the file addteacher.fxml at MainWindow : " + ex.getMessage());
            ex.printStackTrace();
        }

    }

}
