/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backupRestore;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class BackupRestoreController implements Initializable {

    @FXML
    private TextField backup_dest;

    @FXML
    private Button browse_backup;

    @FXML
    private TextField restore_dest;

    @FXML
    private Button browse_restore;

    private String database = "school";
    private String username = "root";
    private String password = "";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    void clickBrowseBackup(MouseEvent event) {
        try {
            FileChooser file_chooser = new FileChooser();
            file_chooser.setInitialFileName("school.sql");
            file_chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All", "*.*"),
                    new FileChooser.ExtensionFilter("SQL (*.sql)", "*.sql")
            );
            file_chooser.setTitle("Backup Data");
            file_chooser.setInitialDirectory(
                    new File(System.getProperty("user.home"))
            );
            Stage stage = (Stage) browse_backup.getScene().getWindow();
            File backup = file_chooser.showSaveDialog(stage);
            if (backup != null) {
                backup_dest.setText(backup.getAbsolutePath());
                Alert a = new Alert(AlertType.CONFIRMATION);
                a.setHeaderText("Confirm Backup");
                a.setContentText("Are you sure you want to backup");
                Optional<ButtonType> btn = a.showAndWait();
                if (btn.get() == ButtonType.OK) {
                    Process p = null;
                    try {
                        Runtime runtime = Runtime.getRuntime();
                        String location = "cd c:\\xampp\\mysql\\bin";

                        String backup_cmd = "c:\\xampp\\mysql\\bin mysqldump -u" + username + " -p" + password + " "
                                + "--add-drop-database -B " + database + " -r "
                                + backup.getAbsolutePath() + backup.getName() + ".sql";
                        System.out.println(backup_cmd);
                        String[] command = new String[]{location, backup_cmd};
                        p = runtime.exec(command);
//change the dbpass and dbname with your dbpass and dbname
                        int processComplete = p.waitFor();

                        if (processComplete == 0) {
                            a.close();
                            a = new Alert(AlertType.INFORMATION);
                            a.setHeaderText("Backup Finished");
                            a.setContentText("Backup Successful !!!");
                            a.show();

                        } else {
                            a.close();
                            a = new Alert(AlertType.ERROR);
                            a.setHeaderText("Backup Error");
                            a.setContentText("Some error in backup !!!");
                            a.show();
                        }

                    } catch (Exception e) {
                        System.out.println("Exception while backing up "
                                + " at clickBrowseBtn(MouseEvent event) "
                                + "at BackupRestoreController : " + e.getMessage());
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("Exception at clickBackupBtn(MouseEvent event) "
                    + "at BackupRestoreController : " + e.getMessage());
        }
    }

    @FXML
    void clickBrowseRestore(MouseEvent event) {
        try {
            FileChooser file_chooser = new FileChooser();
            file_chooser.setInitialFileName("school.sql");
            file_chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All", "*.*"),
                    new FileChooser.ExtensionFilter("SQL (*.sql)", "*.sql")
            );
            file_chooser.setTitle("Restore Data");
            file_chooser.setInitialDirectory(
                    new File(System.getProperty("user.home"))
            );
            Stage stage = (Stage) browse_restore.getScene().getWindow();
            File restore = file_chooser.showOpenDialog(stage);
            if (restore != null) {
                restore_dest.setText(restore.getAbsolutePath());
                Alert a = new Alert(AlertType.CONFIRMATION);
                a.setHeaderText("Confirm Restore");
                a.setContentText("Are you sure you want to restore");
                Optional<ButtonType> btn = a.showAndWait();
                if (btn.get() == ButtonType.OK) {
                    Process p = null;
                    try {
                        String location = "cd c:\\xampp\\mysql\\bin";
                        String[] restoreCmd = new String[]{"cmd.exe", "/c",location,"mysql ", "--user=" + username, "--password=" + password, "-e", "source "
                            + restore.getAbsolutePath()};

                        Process runtimeProcess;

                        runtimeProcess = Runtime.getRuntime().exec(restoreCmd);
                        int processComplete = runtimeProcess.waitFor();
                        if (processComplete == 0) {
                            a.close();
                            a = new Alert(AlertType.INFORMATION);
                            a.setHeaderText("Restore Finished");
                            a.setContentText("Restore Successful !!!");
                            a.show();

                        } else {
                            a.close();
                            a = new Alert(AlertType.ERROR);
                            a.setHeaderText("Restore Error");
                            a.setContentText("Some error in Restore Process !!!");
                            a.show();
                        }

                    } catch (Exception e) {
                        System.out.println("Exception while restoring "
                                + " at clickBrowseRestore(MouseEvent event) "
                                + "at BackupRestoreController : " + e.getMessage());
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("Exception at clickBrowseRestore (MouseEvent event) "
                    + "at BackupRestoreController : " + e.getMessage());
        }
    }

}
