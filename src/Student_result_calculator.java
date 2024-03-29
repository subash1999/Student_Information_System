

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

        try {
           // Server server = new Server();
            //server.startServer();
            database.Connection.connect();
            LoginController.current_year="2074";
            LoginController.user_type="admin";
            LoginController.current_user="s";
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/login/login.fxml"));
//            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/ledger/ledger.fxml"));
//            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/teacher/addteacher.fxml"));
//            Parent root = FXMLLoader.load(getClass().getResource("/report/reportcard.fxml"));
//          Parent root = FXMLLoader.load(getClass().getResource("/ledger/ledger.fxml"));    
//           Parent root = FXMLLoader.load(getClass().getResource("chart/chart.fxml"));
//           Parent root = FXMLLoader.load(getClass().getResource("/organization/organization.fxml"));

            Parent root = fxml.load();
//              ReportcardController controller = (ReportcardController) fxml.getController();
            // controller.setValues("25","34");
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.toFront();
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
        System.out.println("Meg used 0=" + (Runtime.getRuntime().totalMemory()
                - Runtime.getRuntime().freeMemory()) / (1000 * 1000) + "M");

        launch(args);
        System.out.println("Meg used last=" + (Runtime.getRuntime().totalMemory()
                - Runtime.getRuntime().freeMemory()) / (1000 * 1000) + "M");

    }

}
