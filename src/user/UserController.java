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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import login.LoginController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class UserController implements Initializable {

    @FXML
    private TableView table;

    @FXML
    private ComboBox search_by;

    @FXML
    private JFXTextField search_field;

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
    private Button register_btn;

    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private List<String> column_name = new ArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        makeTable();
        listenerForReEnterPassword();
        listenerForPassword();
        listenerForSearchField();
        listenerForSearchBy();
        listenerForUsername();
        makePhoneTextFieldNumeric();
        user_type.getItems().addAll("Admin", "Regular");
        password_strength.setVisible(false);
    }

    public void refresh() {
        String search = search_field.getText();
        makeTable();
        search_field.setText("");
        search_field.setText(search);
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

    private void makeTable() {
        database.Connection.connect();
        Connection conn = database.Connection.conn;

        //SQL FOR SELECTING ALL OF STUDENT
        String SQL;
        ResultSet rs = null;
        try {
            table.getColumns().clear();
            table.getItems().clear();
            data.clear();
            SQL = "SET @a=0";
            conn.createStatement().executeQuery(SQL);
            SQL = "SELECT @a:=@a+1 AS 'S.N',User_id,Type,Username,Name,Phone FROM  User "
                    + " ORDER BY Type,User_id";
            rs = conn.createStatement().executeQuery(SQL);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Query Error");
        }

        try {
            /**
             * ********************************
             * TABLE COLUMN ADDED DYNAMICALLY * ********************************
             */
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setStyle("-fx-alignment : center;");

                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());

                    }
                });
                if (!col.getText().equalsIgnoreCase("s.n")) {
                    column_name.add(col.getText());
                }
                table.getColumns().addAll(col);
            }

        } catch (Exception ex) {
            System.out.println("Getting metadata error at makeTable() "
                    + "at UserController  " + ex.getMessage());
            ex.printStackTrace();
        }

        try {
            /**
             * ******************************
             * Data added to ObservableList * ******************************
             */
            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                data.add(row);

            }
        } catch (SQLException ex) {
            System.out.println("No next data error at makeTable() "
                    + "at UserController " + ex.getMessage());
            ex.printStackTrace();
        }
        //FINALLY ADDED TO TableView
        table.setItems(data);
        //getting rid of the extra column in the javafx tableview
        // is done by the code table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //but in this case we need the extra column for proper resizing of the columns so
        // the table is being made UNCONSTRAINED_RESIZE_POLICY
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        //setting the table menu button visible which lets user to select the column to view or hide
        table.setTableMenuButtonVisible(true);

    }

    private void clearAllFields() {
        name.clear();
        phone.clear();
        username.clear();
        password.clear();
        re_enter_password.clear();
        user_type.getSelectionModel().select(null);
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
                register_btn.setDisable(false);
            } else {
                re_enter_password.setStyle("-fx-background-color:#DC143C;");
                register_btn.setDisable(true);
            }
        });
    }

    private void listenerForPassword() {
        password.textProperty().addListener(e -> {
            String pass = password.getText();
            String re = re_enter_password.getText();
            if (re.equals(pass)) {
                re_enter_password.setStyle("-fx-background-color:white;");
                register_btn.setDisable(false);
            } else {
                re_enter_password.setStyle("-fx-background-color:#DC143C;");
                register_btn.setDisable(true);
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
                        register_btn.setDisable(true);
                    } else {
                        this.username.setStyle("-fx-background-color:white;");
                        register_btn.setDisable(false);
                    }
                } catch (Exception ex) {
                    System.out.println("Exception at listenerForUsername()  "
                            + "at Username : " + ex.getMessage());
                    ex.printStackTrace();
                    this.username.setStyle("-fx-background-color:#DC143C;");
                    register_btn.setDisable(true);
                }
            } else {
                this.username.setStyle("-fx-background-color:white;");
                register_btn.setDisable(false);
            }
        });
    }

    private void listenerForSearchField() {
        search_field.textProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(javafx.beans.Observable observable) {
                if (search_field.getText() == null) {
                    table.setItems(data);
                    return;
                }
                if (search_field.getText().isEmpty()) {
                    table.setItems(data);
                    return;
                }
                updateFilterTable(search_by.getSelectionModel().getSelectedItem().toString());
            }

        });

    }

    private void listenerForSearchBy() {
        ObservableList<String> choice_list = FXCollections.observableArrayList();
        choice_list.add("All");
        choice_list.addAll(column_name);
        search_by.getSelectionModel().select("All");
        search_by.setItems(choice_list);
        search_by.getSelectionModel().selectFirst();
        //adding listner to the choice box so that when ever the value changes
        //the searchbox will search accordingly the search_choice of the user
        search_by.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String number, String number2) {
                if (!search_by.getSelectionModel().getSelectedItem().toString().isEmpty()) {
                    updateFilterTable(search_by.getSelectionModel().getSelectedItem().toString());
                }
            }
        });
    }

    private void updateFilterTable(String choice) {
        ObservableList tableItems = FXCollections.observableArrayList();

        ObservableList<TableColumn> cols = table.getColumns();

        for (int i = 0; i < data.size(); i++) {

            TableColumn col = new TableColumn();
            switch (choice.toLowerCase()) {

                case "all":
                    for (int j = 0; j < cols.size(); j++) {
                        col = cols.get(j);
                        if (col.isVisible()) {
                            String cellValue = col.getCellData(data.get(i)).toString();

                            cellValue = cellValue.toLowerCase();

                            if (cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                tableItems.add(data.get(i));

                                break;

                            }
                        }
                    }
                    break;
                case "name":
                    for (TableColumn c : cols) {
                        if ("name".equals(c.getText().toLowerCase())) {
                            col = c;
                        }
                    }

                    String cellValue = col.getCellData(data.get(i)).toString();

                    cellValue = cellValue.toLowerCase();

                    if (cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                        tableItems.add(data.get(i));

                    }

                    break;
                case "phone":
                    for (TableColumn c : cols) {
                        if ("phone".equals(c.getText().toLowerCase())) {
                            col = c;
                        }
                    }

                    cellValue = col.getCellData(data.get(i)).toString();

                    cellValue = cellValue.toLowerCase();

                    if (cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                        tableItems.add(data.get(i));

                    }

                    break;
                case "type":
                    for (TableColumn c : cols) {
                        if ("type".equals(c.getText().toLowerCase())) {
                            col = c;
                        }
                    }

                    cellValue = col.getCellData(data.get(i)).toString();

                    cellValue = cellValue.toLowerCase();

                    if (cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                        tableItems.add(data.get(i));
                    }
                    break;
                case "username":
                    for (TableColumn c : cols) {
                        if ("username".equals(c.getText().toLowerCase())) {
                            col = c;
                        }
                    }

                    cellValue = col.getCellData(data.get(i)).toString();

                    cellValue = cellValue.toLowerCase();

                    if (cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                        tableItems.add(data.get(i));
                    }
                    break;
                case "user_id":
                    for (TableColumn c : cols) {
                        if ("user_id".equals(c.getText().toLowerCase())) {
                            col = c;
                        }
                    }

                    cellValue = col.getCellData(data.get(i)).toString();

                    cellValue = cellValue.toLowerCase();

                    if (cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                        tableItems.add(data.get(i));
                    }
                    break;

                default:
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Error search by Type");
                    alert.setContentText("The search type didnot matched."
                            + "Please check the code for the proper search type");
                    alert.show();
                    break;
            }

        }

        table.setItems(tableItems);
    }

    @FXML
    private void clickDeleteUser(MouseEvent event) {
        if (!table.getSelectionModel().getSelectedItems().isEmpty()) {
            if (LoginController.current_user != null) {
                int index = table.getSelectionModel().getSelectedIndex();
                TableColumn col = (TableColumn) table.getColumns().get(1);
                int user_id = Integer.valueOf(String.valueOf(col.getCellData(index)));
                String query = "SELECT `Username` FROM User WHERE User_id = " + user_id;
                String username = new String();
                Connection conn = database.Connection.conn;
                try {
                    ResultSet result = conn.createStatement().executeQuery(query);
                    result.next();
                    username = result.getString(1);
                    refresh();
                } catch (Exception e) {
                    System.out.println("Exception at clickDeleteUser() upper at "
                            + "UserController : " + e.getMessage());
                }
                if (LoginController.current_user.equals(username)) {
                    Alert a = new Alert(AlertType.ERROR);
                    a.setHeaderText("Interrupted !!!  Cannot delete yourself !!");
                    a.setContentText("You cannot delete yourself from the system");
                    a.show();
                } else {
                    Alert a = new Alert(AlertType.CONFIRMATION);
                    a.setHeaderText("Confirm User Deletion ");
                    a.setContentText("Are you sure to delete the user with id : " + user_id);
                    Optional<ButtonType> btn = a.showAndWait();
                    if (btn.get() == ButtonType.OK) {
                        query = "DELETE FROM User WHERE User_id = " + user_id;
                        try {
                            conn.createStatement().execute(query);
                            a = new Alert(AlertType.INFORMATION);
                            a.setHeaderText("Deletion Successful");
                            a.setContentText("Deletion of user with id : " + user_id);
                            a.show();
                            refresh();
                        } catch (Exception e) {
                            System.out.println("Exception at clickDeleteUser() at "
                                    + "UserController : " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    @FXML
    private void clickEditUser(MouseEvent event) {
        if (!table.getSelectionModel().getSelectedItems().isEmpty()) {
            if (table.getSelectionModel().getSelectedIndices().size() == 1) {
                int index = table.getSelectionModel().getSelectedIndex();
                TableColumn col = (TableColumn) table.getColumns().get(1);
                int user_id = Integer.valueOf(String.valueOf(col.getCellData(index)));
                PasswordDialog pwd_dialog = new PasswordDialog();
                col = (TableColumn) table.getColumns().get(3);
                String user = (String) col.getCellData(index);
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
                        }
                    } catch (Exception e) {
                        System.out.println("First Exception  at clickEditUser() "
                                + "at UserController "
                                + " : " + e.getMessage());
                        e.printStackTrace();
                    }
                    if (password.get().equals(password_correct)) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("edituser.fxml"));
                            Parent root = loader.load();
                            EdituserController controller = loader.<EdituserController>getController();
                            controller.user_id.set(user_id);
                            Scene scene = new Scene(root);
                            Stage stage = new Stage();
                            stage.setTitle("Edit User");
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.setOnCloseRequest(e -> {
                                refresh();
                            });
                            stage.setScene(scene);
                            stage.show();
                        } catch (Exception e) {
                            System.out.println("Last Exception at clickEditUser()"
                                    + " at UserController "
                                    + " : " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        Alert a = new Alert(AlertType.ERROR);
                        a.setHeaderText("Wrong password");
                        a.setContentText("Wrong Password For \n Username : " + user);
                        a.show();
                    }
                }
            }
        }
    }

    @FXML
    private void clickRegisterBtn(MouseEvent event) {
        if (areAllFieldsFilled()) {
            String name = this.name.getText();
            String username = this.username.getText();
            String pass = this.password.getText();
            String type = this.user_type.getSelectionModel().getSelectedItem().toString();
            String phone = this.phone.getText();
            try {
                Connection conn = database.Connection.conn;
                String year = LoginController.current_year;
                String query = "INSERT INTO User(Name,Username,Password,Phone,Type) "
                        + "VALUES (?,?,?,?,?)";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, name);
                pst.setString(2, username);
                pst.setString(3, pass);
                pst.setString(4, phone);
                pst.setString(5, type);
                pst.execute();
                Alert a = new Alert(AlertType.INFORMATION);
                a.setHeaderText("User added successfully !!!");
                a.setContentText("New user was added. \n Username : " + username
                        + "\nName : " + name);
                a.show();
                refresh();
                clearAllFields();
            } catch (Exception e) {
                System.out.println("Exception at clickRegisterBtn() "
                        + "at UserController " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Alert a = new Alert(AlertType.ERROR);
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

}
