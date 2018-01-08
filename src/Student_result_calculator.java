

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import database.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import login.LoginController;
import report.ReportcardController;

/**
 *
 * @author subas
 */
public class Student_result_calculator extends Application {

    private Stage primaryStage = null;

    @Override
    public void start(Stage primaryStage) {
//        Runnable r = new Runnable(){
//            @Override
//        public void run(){
//        Server server = new Server();
//        server.startServer();
//            }
//        };
//        new Thread(r).start();
        Server server = new Server();
        server.startServer();
        try {
            LoginController.current_year = "2074";
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/login/login.fxml"));
//            Parent root = FXMLLoader.load(getClass().getResource("/report/reportcard.fxml"));
//          Parent root = FXMLLoader.load(getClass().getResource("/ledger/ledger.fxml"));    
//           Parent root = FXMLLoader.load(getClass().getResource("chart/chart.fxml"));
//           Parent root = FXMLLoader.load(getClass().getResource("/organization/organization.fxml"));

            Parent root = fxml.load();
//              ReportcardController controller = (ReportcardController) fxml.getController();
            // controller.setValues("25","34");
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            //primaryStage.setResizable(false);
            primaryStage.toFront();
            primaryStage.show();

            //this.primaryStage=primaryStage;
        } catch (Exception ex) {
            System.out.println("File not found" + ex.getLocalizedMessage());
            ex.printStackTrace();
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
