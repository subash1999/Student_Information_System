/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package organization;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class OrganizationController implements Initializable {

    @FXML
    private ImageView logo_image;

    @FXML
    private Button browse_btn;

    @FXML
    private TextField name_textfield;

    @FXML
    private TextField address_textfield;

    @FXML
    private TextField phone_textfield;

    @FXML
    private Button save_btn;

    @FXML
    private Button edit_btn;

    @FXML
    private ProgressIndicator progress_indicator;

    @FXML
    private Label edit_label;

    @FXML
    private TextField estd_textfield;

    @FXML
    private ComboBox year_format_combobox;

    private File logo_file = null;

    //var holding old values
    String o_name = null;
    String o_established = null;
    String o_address = null;
    String o_phone = null;
    File o_logo_file = null;
    String o_year_format = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        edit_label.setVisible(false);
        progress_indicator.setVisible(false);
        browse_btn.setDisable(true);
        save_btn.setDisable(true);
        year_format_combobox.setDisable(true);
        year_format_combobox.getItems().addAll("B.S", "A.D");
        year_format_combobox.getSelectionModel().select("B.S");
        getOrganizationDetails();
        validateEstablishedYearInput();
        validatePhoneInput();

    }

    private void validateEstablishedYearInput() {
        estd_textfield.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    char ch = estd_textfield.getText().charAt(oldValue.intValue());
                    // Check if the new character is the number or other's
                    if (!(ch >= '0' && ch <= '9')) {
                        // if it's not number then just setText to previous one
                        estd_textfield.setText(estd_textfield.getText().substring(0, estd_textfield.getText().length() - 1));
                    }
                }

            }
        });
    }

    private void validatePhoneInput() {
        phone_textfield.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    char ch = phone_textfield.getText().charAt(oldValue.intValue());
                    // Check if the new character is the number or other's
                    if (!((ch >= '0' && ch <= '9') || ch == ',')) {
                        // if it's not number then just setText to previous one
                        phone_textfield.setText(phone_textfield.getText().substring(0, phone_textfield.getText().length() - 1));
                    }
                }

            }
        });
    }

    private void getOrganizationDetails() {
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;

            String query = "SELECT Name,Established,Year_format,Logo,Address,Contact "
                    + "FROM organization ;";
            ResultSet result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                name_textfield.setText(result.getString(1));
                estd_textfield.setText(result.getString(2));
                year_format_combobox.getSelectionModel().select(result.getString(3));
                address_textfield.setText(result.getString(5));
                phone_textfield.setText(result.getString(6));
                InputStream is = result.getBinaryStream(4);
                File logo_file = new File("logo.png");
                OutputStream out = new FileOutputStream(logo_file);
                String st = result.getString(4);
                int len = st.length();
                byte[] contents = new byte[len];
                int size = 0;
                while ((size = is.read(contents)) != -1) {
                    out.write(contents, 0, size);
                }
                Image image = new Image("file:logo.png");
                logo_image.setImage(image);
                
            }
        } catch (Exception e) {
            System.out.println("Exception at getOrganizationDetails() at "
                    + "ReportController : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void clickBrowseBtn(MouseEvent event) {
        FileChooser file_chooser = new FileChooser();
        file_chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(" All ", "*.*"),
                new FileChooser.ExtensionFilter(" Images ", "*.jpeg", "*.png",
                        "*.tiff", "*.bmp", "*.bpg", "*.img", "*.jpg", "*.ico")
        );
        file_chooser.setTitle("Select Logo Of Organization");
        Stage stage = (Stage) browse_btn.getScene().getWindow();
        logo_file = file_chooser.showOpenDialog(stage);
        if(logo_file.length()<950000){
        if (logo_file != null) {
            try {
                BufferedImage image = ImageIO.read(logo_file);
                Image photo = new Image(logo_file.toURI().toString());
                logo_image.setImage(photo);

            } catch (Exception ex) {
                System.out.println("Exception at clickBrowseBtn() at "
                        + "OrganizationController : " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
        else{
            Alert a = new Alert(AlertType.INFORMATION);
            a.setHeaderText("Please select file less than  or equal 900 kB");
            a.setContentText("Only file less than or equal 900 kb are accepted");
            a.showAndWait();
        }
    }

    @FXML
    private void clickEditBtn(MouseEvent event) {
        edit_btn.setDisable(true);
        progress_indicator.setVisible(true);
        progress_indicator.setProgress(-1.0);
        year_format_combobox.setDisable(false);
        edit_label.setVisible(true);
        save_btn.setDisable(false);
        browse_btn.setDisable(false);
        name_textfield.setEditable(true);
        estd_textfield.setEditable(true);
        address_textfield.setEditable(true);
        phone_textfield.setEditable(true);
        //storing the old datas
        String o_name = name_textfield.getText();
        String o_established = estd_textfield.getText();
        String o_address = address_textfield.getText();
        String o_phone = phone_textfield.getText();
        File o_logo_file = logo_file;
        String o_year_format = year_format_combobox.getSelectionModel().getSelectedItem().toString();

    }
    private void changeBackToOldValues(){
        database.Connection.connect();
        Connection conn = database.Connection.conn;
        if (!name_textfield.getText().isEmpty() && !address_textfield.getText().isEmpty()) {
            try {
                String name = o_name;
                String established = o_established;
                String year_format = o_year_format;
                String address = o_address;
                String phone = o_phone;
                String query = null;
                PreparedStatement pst = null;
                query = "TRUNCATE TABLE organization";
                conn.createStatement().execute(query);
                if (o_logo_file != null) {
                    query = "INSERT INTO organization(Name,Established,year_format"
                            + ",Address,Contact,Logo) "
                            + "VALUES(?,?,?,?,?,?)";
                    pst = conn.prepareStatement(query);
                    FileInputStream fis = new FileInputStream(o_logo_file.getAbsoluteFile());
                    pst.setBinaryStream(6, fis, fis.available());
                } else {
                    query = "INSERT INTO organization(Name,Established,year_format"
                            + ",Address,Contact) "
                            + "VALUES(?,?,?,?,?)";
                    pst = conn.prepareStatement(query);
                }
                pst.setString(1, name);
                pst.setString(2, established);
                pst.setString(3, year_format);
                pst.setString(4, address);
                pst.setString(5, phone);
                pst.executeUpdate();
                Alert a = new Alert(AlertType.INFORMATION);
                a.setHeaderText("Old values retrived Successfully");
                a.setContentText("The datas of the organization were retrived to old values");
                a.show();
                //Disabling all the editing tools
                progress_indicator.setVisible(false);
                progress_indicator.setProgress(0);
                year_format_combobox.setDisable(true);
                edit_label.setVisible(false);
                save_btn.setDisable(true);
                browse_btn.setDisable(true);
                name_textfield.setEditable(false);
                estd_textfield.setEditable(false);
                address_textfield.setEditable(false);
                phone_textfield.setEditable(false);
                edit_btn.setDisable(false);
            } catch (Exception e) {
                System.out.println("Exception at changeBackToOldValues() "
                        + "at OrganizationController : " + e.getMessage());
                e.printStackTrace();
                Alert a = new Alert(AlertType.ERROR);
                a.setHeaderText("Changing back Failed");
                a.setContentText("The data were not changed back due to :  " + e.getMessage());
                a.show();
            }
        } else {
            Alert a = new Alert(AlertType.ERROR);
            a.setHeaderText("Name and address cannot be null");
            a.setContentText("Name and address are compulsory");
            a.show();
        }
    }
    @FXML
    private void clickSaveBtn(MouseEvent event) {
        database.Connection.connect();
        Connection conn = database.Connection.conn;
        if (!name_textfield.getText().isEmpty() && !address_textfield.getText().isEmpty()) {
            try {
                String name = name_textfield.getText();
                String established = estd_textfield.getText();
                String year_format = year_format_combobox.getSelectionModel().getSelectedItem().toString();
                String address = address_textfield.getText();
                String phone = phone_textfield.getText();
                String query = null;
                PreparedStatement pst = null;
                query = "TRUNCATE TABLE organization";
                conn.createStatement().execute(query);
                if (logo_file != null) {
                    System.out.println("not null");
                    query = "INSERT INTO organization(Name,Established,year_format"
                            + ",Address,Contact,Logo) "
                            + "VALUES(?,?,?,?,?,?)";
                    pst = conn.prepareStatement(query);
                    FileInputStream fis = new FileInputStream(logo_file.getAbsoluteFile());
                    pst.setBinaryStream(6, fis, fis.available());
                } else {
                    System.out.println("null");
                    query = "INSERT INTO organization(Name,Established,year_format"
                            + ",Address,Contact) "
                            + "VALUES(?,?,?,?,?)";
                    pst = conn.prepareStatement(query);
                }
                pst.setString(1, name);
                pst.setString(2, established);
                pst.setString(3, year_format);
                pst.setString(4, address);
                pst.setString(5, phone);
                pst.executeUpdate();
                Alert a = new Alert(AlertType.INFORMATION);
                a.setHeaderText("Updating Successful");
                a.setContentText("The datas of the organization were updated successfully ");
                a.show();
                //Disabling all the editing tools
                progress_indicator.setVisible(false);
                progress_indicator.setProgress(0);
                year_format_combobox.setDisable(true);
                edit_label.setVisible(false);
                save_btn.setDisable(true);
                browse_btn.setDisable(true);
                name_textfield.setEditable(false);
                estd_textfield.setEditable(false);
                address_textfield.setEditable(false);
                phone_textfield.setEditable(false);
                edit_btn.setDisable(false);

            } catch (Exception e) {
                System.out.println("Exception at clickSaveBtn() "
                        + "at OrganizationController : " + e.getMessage());
                e.printStackTrace();
                Alert a = new Alert(AlertType.ERROR);
                a.setHeaderText("Updating Failed");
                a.setContentText("The data were not updated due to :  " + e.getMessage());
                a.showAndWait();
                changeBackToOldValues();                
            }
        } else {
            Alert a = new Alert(AlertType.ERROR);
            a.setHeaderText("Name and address cannot be null");
            a.setContentText("Name and address are compulsory");
            a.show();
        }

    }

}
