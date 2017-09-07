package User;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public  class MainWindow  {
    protected BorderPane BorderPane;
    protected  MenuBar menubar;
    protected  Menu file;
    protected  MenuItem view_session_menu_item;
    protected  MenuItem change_session_menuu_item;
    protected  MenuItem close_menu_item;
    protected  Menu edit;
    protected  MenuItem delete_menu_item;
    protected  Menu help;
    protected  MenuItem about_menu_item;
    protected  Menu teacher;
    protected  MenuItem view_all_menu_item;
    protected  MenuItem view_classwise_menu_item;
    protected  Menu admin;
    protected  MenuItem dashboard_menu_item;
    protected  Menu chat;
    protected  MenuItem open_chat_menu_item;
    protected  GridPane gridPane;
    protected  ColumnConstraints columnConstraints;
    protected  RowConstraints rowConstraints;
    protected  RowConstraints rowConstraints0;
    protected  RowConstraints rowConstraints1;
    protected  RowConstraints rowConstraints2;
    protected  RowConstraints rowConstraints3;
    protected  RowConstraints rowConstraints4;
    protected  RowConstraints rowConstraints5;
    protected  RowConstraints rowConstraints6;
    protected  RowConstraints rowConstraints7;
    protected  RowConstraints rowConstraints8;
    protected  RowConstraints rowConstraints9;
    protected  RowConstraints rowConstraints10;
    protected  RowConstraints rowConstraints11;
    protected  RowConstraints rowConstraints12;
    protected  RowConstraints rowConstraints13;
    protected  RowConstraints rowConstraints14;
    protected  AnchorPane anchorPane;
    protected  GridPane gridPane0;
    protected  ColumnConstraints columnConstraints0;
    protected  ColumnConstraints columnConstraints1;
    protected  ColumnConstraints columnConstraints2;
    protected  RowConstraints rowConstraints15;
    protected  RowConstraints rowConstraints16;
    protected  RowConstraints rowConstraints17;
    protected  AnchorPane student_anchor;
    protected  ImageView student_image;
    protected  Label student_label;
    protected  AnchorPane teacher_anchor;
    protected  ImageView teacher_image;
    protected  Label teacher_label;
    protected  AnchorPane marksheet_anchor;
    protected  Label marksheet_label;
    protected  ImageView marksheet_image;
    protected  AnchorPane legder_anchor;
    protected  Label ledger_label;
    protected  ImageView ledger_label0;
    protected  AnchorPane chart_anchor;
    protected  Label chart_label;
    protected  ImageView chart_image;
    protected  AnchorPane attendance_anchor;
    protected  ImageView attendance_image;
    protected  Label attendance_label;
    protected  AnchorPane session_anchor;
    protected  ImageView session_image;
    protected  Label session_label;
    protected  AnchorPane classwise_anchor;
    protected  ImageView classwise_anchor0;
    protected  Label classwise_label;

    public Node display() {

        menubar = new MenuBar();
        file = new Menu();
        view_session_menu_item = new MenuItem();
        change_session_menuu_item = new MenuItem();
        close_menu_item = new MenuItem();
        edit = new Menu();
        delete_menu_item = new MenuItem();
        help = new Menu();
        about_menu_item = new MenuItem();
        teacher = new Menu();
        view_all_menu_item = new MenuItem();
        view_classwise_menu_item = new MenuItem();
        admin = new Menu();
        dashboard_menu_item = new MenuItem();
        chat = new Menu();
        open_chat_menu_item = new MenuItem();
        gridPane = new GridPane();
        columnConstraints = new ColumnConstraints();
        rowConstraints = new RowConstraints();
        rowConstraints0 = new RowConstraints();
        rowConstraints1 = new RowConstraints();
        rowConstraints2 = new RowConstraints();
        rowConstraints3 = new RowConstraints();
        rowConstraints4 = new RowConstraints();
        rowConstraints5 = new RowConstraints();
        rowConstraints6 = new RowConstraints();
        rowConstraints7 = new RowConstraints();
        rowConstraints8 = new RowConstraints();
        rowConstraints9 = new RowConstraints();
        rowConstraints10 = new RowConstraints();
        rowConstraints11 = new RowConstraints();
        rowConstraints12 = new RowConstraints();
        rowConstraints13 = new RowConstraints();
        rowConstraints14 = new RowConstraints();
        anchorPane = new AnchorPane();
        gridPane0 = new GridPane();
        columnConstraints0 = new ColumnConstraints();
        columnConstraints1 = new ColumnConstraints();
        columnConstraints2 = new ColumnConstraints();
        rowConstraints15 = new RowConstraints();
        rowConstraints16 = new RowConstraints();
        rowConstraints17 = new RowConstraints();
        student_anchor = new AnchorPane();
        student_image = new ImageView();
        student_label = new Label();
        teacher_anchor = new AnchorPane();
        teacher_image = new ImageView();
        teacher_label = new Label();
        marksheet_anchor = new AnchorPane();
        marksheet_label = new Label();
        marksheet_image = new ImageView();
        legder_anchor = new AnchorPane();
        ledger_label = new Label();
        ledger_label0 = new ImageView();
        chart_anchor = new AnchorPane();
        chart_label = new Label();
        chart_image = new ImageView();
        attendance_anchor = new AnchorPane();
        attendance_image = new ImageView();
        attendance_label = new Label();
        session_anchor = new AnchorPane();
        session_image = new ImageView();
        session_label = new Label();
        classwise_anchor = new AnchorPane();
        classwise_anchor0 = new ImageView();
        classwise_label = new Label();

        BorderPane.setPrefHeight(768.0);
        BorderPane.setPrefWidth(1366.0);

        BorderPane.setAlignment(menubar, javafx.geometry.Pos.CENTER);
        menubar.setId("menu");
        menubar.setPrefHeight(37.0);
        menubar.setPrefWidth(694.0);

        file.setId("file");
        file.setMnemonicParsing(false);
        file.setText("File");

        view_session_menu_item.setMnemonicParsing(false);
        view_session_menu_item.setText("View Session...");

        change_session_menuu_item.setMnemonicParsing(false);
        change_session_menuu_item.setText("Change Session...");

        close_menu_item.setMnemonicParsing(false);
        close_menu_item.setText("Close");

        edit.setId("edit");
        edit.setMnemonicParsing(false);
        edit.setText("Edit");

        delete_menu_item.setMnemonicParsing(false);
        delete_menu_item.setText("Delete");

        help.setId("help");
        help.setMnemonicParsing(false);
        help.setText("Help");

        about_menu_item.setMnemonicParsing(false);
        about_menu_item.setText("About...");

        teacher.setId("teacher");
        teacher.setMnemonicParsing(false);
        teacher.setText("Teacher");

        view_all_menu_item.setMnemonicParsing(false);
        view_all_menu_item.setText("View all...");

        view_classwise_menu_item.setMnemonicParsing(false);
        view_classwise_menu_item.setText("View Classwise...");

        admin.setId("admin");
        admin.setMnemonicParsing(false);
        admin.setText("Admin");

        dashboard_menu_item.setMnemonicParsing(false);
        dashboard_menu_item.setText("Dashboard...");

        chat.setId("chat");
        chat.setMnemonicParsing(false);
        chat.setText("Chat");

        open_chat_menu_item.setMnemonicParsing(false);
        open_chat_menu_item.setText("Open Chat...");
        BorderPane.setTop(menubar);

        BorderPane.setAlignment(gridPane, javafx.geometry.Pos.CENTER);
        gridPane.setPrefHeight(460.0);
        gridPane.setPrefWidth(210.0);
        gridPane.setStyle("-fx-background-color: #141414;");
        //adding the clock to the gridpane        
        
        columnConstraints.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
        columnConstraints.setMinWidth(10.0);
        columnConstraints.setPrefWidth(100.0);

        rowConstraints.setMaxHeight(148.0);
        rowConstraints.setMinHeight(0.0);
        rowConstraints.setPrefHeight(35.0);
        rowConstraints.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints0.setMaxHeight(369.0);
        rowConstraints0.setMinHeight(9.0);
        rowConstraints0.setPrefHeight(47.0);
        rowConstraints0.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints1.setMaxHeight(418.0);
        rowConstraints1.setMinHeight(10.0);
        rowConstraints1.setPrefHeight(51.0);
        rowConstraints1.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints2.setMaxHeight(418.0);
        rowConstraints2.setMinHeight(10.0);
        rowConstraints2.setPrefHeight(58.0);
        rowConstraints2.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints3.setMaxHeight(418.0);
        rowConstraints3.setMinHeight(10.0);
        rowConstraints3.setPrefHeight(62.0);
        rowConstraints3.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints4.setMaxHeight(418.0);
        rowConstraints4.setMinHeight(10.0);
        rowConstraints4.setPrefHeight(203.0);
        rowConstraints4.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints5.setMaxHeight(418.0);
        rowConstraints5.setMinHeight(10.0);
        rowConstraints5.setPrefHeight(203.0);
        rowConstraints5.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints6.setMaxHeight(418.0);
        rowConstraints6.setMinHeight(10.0);
        rowConstraints6.setPrefHeight(203.0);
        rowConstraints6.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints7.setMaxHeight(418.0);
        rowConstraints7.setMinHeight(10.0);
        rowConstraints7.setPrefHeight(203.0);
        rowConstraints7.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints8.setMaxHeight(418.0);
        rowConstraints8.setMinHeight(10.0);
        rowConstraints8.setPrefHeight(203.0);
        rowConstraints8.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints9.setMaxHeight(418.0);
        rowConstraints9.setMinHeight(10.0);
        rowConstraints9.setPrefHeight(203.0);
        rowConstraints9.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints10.setMaxHeight(418.0);
        rowConstraints10.setMinHeight(10.0);
        rowConstraints10.setPrefHeight(203.0);
        rowConstraints10.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints11.setMaxHeight(418.0);
        rowConstraints11.setMinHeight(10.0);
        rowConstraints11.setPrefHeight(203.0);
        rowConstraints11.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints12.setMaxHeight(418.0);
        rowConstraints12.setMinHeight(10.0);
        rowConstraints12.setPrefHeight(203.0);
        rowConstraints12.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints13.setMaxHeight(418.0);
        rowConstraints13.setMinHeight(10.0);
        rowConstraints13.setPrefHeight(203.0);
        rowConstraints13.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints14.setMaxHeight(418.0);
        rowConstraints14.setMinHeight(10.0);
        rowConstraints14.setPrefHeight(203.0);
        rowConstraints14.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
        BorderPane.setLeft(gridPane);

        BorderPane.setAlignment(anchorPane, javafx.geometry.Pos.CENTER);

        AnchorPane.setBottomAnchor(gridPane0, 0.0);
        AnchorPane.setLeftAnchor(gridPane0, 14.0);
        AnchorPane.setRightAnchor(gridPane0, 0.0);
        AnchorPane.setTopAnchor(gridPane0, 14.0);
        gridPane0.setLayoutX(81.0);
        gridPane0.setLayoutY(14.0);
        gridPane0.setPrefHeight(717.0);
        gridPane0.setPrefWidth(1075.0);

        columnConstraints0.setHalignment(javafx.geometry.HPos.LEFT);
        columnConstraints0.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
        columnConstraints0.setMaxWidth(237.0);
        columnConstraints0.setMinWidth(10.0);
        columnConstraints0.setPrefWidth(216.0);

        columnConstraints1.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
        columnConstraints1.setMaxWidth(237.0);
        columnConstraints1.setMinWidth(10.0);
        columnConstraints1.setPrefWidth(216.0);

        columnConstraints2.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
        columnConstraints2.setMaxWidth(216.0);
        columnConstraints2.setMinWidth(10.0);
        columnConstraints2.setPrefWidth(216.0);

        rowConstraints15.setMaxHeight(241.0);
        rowConstraints15.setMinHeight(10.0);
        rowConstraints15.setPrefHeight(146.0);
        rowConstraints15.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints16.setMaxHeight(208.0);
        rowConstraints16.setMinHeight(10.0);
        rowConstraints16.setPrefHeight(208.0);
        rowConstraints16.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints17.setMaxHeight(208.0);
        rowConstraints17.setMinHeight(10.0);
        rowConstraints17.setPrefHeight(208.0);
        rowConstraints17.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        student_image.setAccessibleRole(javafx.scene.AccessibleRole.HYPERLINK);
        student_image.setFitHeight(138.0);
        student_image.setFitWidth(147.0);
        student_image.setLayoutY(14.0);
        student_image.setPickOnBounds(true);
        student_image.setPreserveRatio(true);
        student_image.setImage(new Image(getClass().getResource("../images/student.png").toExternalForm()));

        student_label.setLayoutY(167.0);
        student_label.setPrefHeight(21.0);
        student_label.setPrefWidth(186.0);
        student_label.setText("  Student Information");
        student_label.setFont(new Font(18.0));

        GridPane.setColumnIndex(teacher_anchor, 1);
        teacher_anchor.setPrefHeight(219.0);
        teacher_anchor.setPrefWidth(291.0);
        GridPane.setMargin(teacher_anchor, new Insets(10.0));

        teacher_image.setAccessibleRole(javafx.scene.AccessibleRole.HYPERLINK);
        teacher_image.setFitHeight(138.0);
        teacher_image.setFitWidth(147.0);
        teacher_image.setPickOnBounds(true);
        teacher_image.setPreserveRatio(true);
        teacher_image.setImage(new Image(getClass().getResource("../images/teacher.jpg").toExternalForm()));

        teacher_label.setLayoutY(156.0);
        teacher_label.setPrefHeight(27.0);
        teacher_label.setPrefWidth(201.0);
        teacher_label.setText("  Teacher information");
        teacher_label.setFont(new Font(18.0));

        GridPane.setRowIndex(marksheet_anchor, 1);

        marksheet_label.setLayoutY(167.0);
        marksheet_label.setPrefHeight(21.0);
        marksheet_label.setPrefWidth(186.0);
        marksheet_label.setText(" Marksheet");
        marksheet_label.setFont(new Font(18.0));

        marksheet_image.setAccessibleRole(javafx.scene.AccessibleRole.HYPERLINK);
        marksheet_image.setFitHeight(138.0);
        marksheet_image.setFitWidth(147.0);
        marksheet_image.setPickOnBounds(true);
        marksheet_image.setPreserveRatio(true);
        marksheet_image.setImage(new Image(getClass().getResource("../images/marksheet.jpg").toExternalForm()));

        GridPane.setColumnIndex(legder_anchor, 1);
        GridPane.setRowIndex(legder_anchor, 1);

        ledger_label.setLayoutX(14.0);
        ledger_label.setLayoutY(163.0);
        ledger_label.setPrefHeight(27.0);
        ledger_label.setPrefWidth(201.0);
        ledger_label.setText(" Legder");
        ledger_label.setFont(new Font(18.0));

        ledger_label0.setAccessibleRole(javafx.scene.AccessibleRole.HYPERLINK);
        ledger_label0.setFitHeight(138.0);
        ledger_label0.setFitWidth(147.0);
        ledger_label0.setLayoutX(14.0);
        ledger_label0.setLayoutY(14.0);
        ledger_label0.setPickOnBounds(true);
        ledger_label0.setPreserveRatio(true);
        ledger_label0.setImage(new Image(getClass().getResource("../images/ledger.jpg").toExternalForm()));

        GridPane.setRowIndex(chart_anchor, 2);

        chart_label.setLayoutY(167.0);
        chart_label.setPrefHeight(27.0);
        chart_label.setPrefWidth(216.0);
        chart_label.setText(" Comparative Chart");
        chart_label.setFont(new Font(18.0));

        chart_image.setAccessibleRole(javafx.scene.AccessibleRole.HYPERLINK);
        chart_image.setFitHeight(138.0);
        chart_image.setFitWidth(147.0);
        chart_image.setPickOnBounds(true);
        chart_image.setPreserveRatio(true);
        chart_image.setImage(new Image(getClass().getResource("../images/chart.jpg").toExternalForm()));

        GridPane.setColumnIndex(attendance_anchor, 1);
        GridPane.setRowIndex(attendance_anchor, 2);

        attendance_image.setAccessibleRole(javafx.scene.AccessibleRole.HYPERLINK);
        attendance_image.setFitHeight(150.0);
        attendance_image.setFitWidth(200.0);
        attendance_image.setPickOnBounds(true);
        attendance_image.setPreserveRatio(true);
        attendance_image.setImage(new Image(getClass().getResource("../images/attendance.png").toExternalForm()));

        attendance_label.setLayoutX(14.0);
        attendance_label.setLayoutY(167.0);
        attendance_label.setPrefHeight(27.0);
        attendance_label.setPrefWidth(216.0);
        attendance_label.setText(" Attendance");
        attendance_label.setFont(new Font(18.0));

        GridPane.setColumnIndex(session_anchor, 2);
        session_anchor.setPrefHeight(219.0);
        session_anchor.setPrefWidth(291.0);

        session_image.setAccessibleRole(javafx.scene.AccessibleRole.HYPERLINK);
        session_image.setFitHeight(138.0);
        session_image.setFitWidth(147.0);
        session_image.setPickOnBounds(true);
        session_image.setPreserveRatio(true);
        session_image.setImage(new Image(getClass().getResource("../images/previous_session.png").toExternalForm()));

        session_label.setLayoutY(157.0);
        session_label.setPrefHeight(27.0);
        session_label.setPrefWidth(201.0);
        session_label.setText("Change Session");
        session_label.setFont(new Font(18.0));

        GridPane.setColumnIndex(classwise_anchor, 2);
        GridPane.setRowIndex(classwise_anchor, 1);
        classwise_anchor.setPrefHeight(219.0);
        classwise_anchor.setPrefWidth(291.0);

        classwise_anchor0.setAccessibleRole(javafx.scene.AccessibleRole.HYPERLINK);
        classwise_anchor0.setFitHeight(138.0);
        classwise_anchor0.setFitWidth(147.0);
        classwise_anchor0.setPickOnBounds(true);
        classwise_anchor0.setPreserveRatio(true);
        classwise_anchor0.setImage(new Image(getClass().getResource("../images/class.png").toExternalForm()));

        classwise_label.setLayoutY(157.0);
        classwise_label.setPrefHeight(27.0);
        classwise_label.setPrefWidth(201.0);
        classwise_label.setText("Classwise information");
        classwise_label.setFont(new Font(18.0));
        BorderPane.setCenter(anchorPane);
        BorderPane.setOpaqueInsets(new Insets(10.0));

        file.getItems().add(view_session_menu_item);
        file.getItems().add(change_session_menuu_item);
        file.getItems().add(close_menu_item);
        menubar.getMenus().add(file);
        edit.getItems().add(delete_menu_item);
        menubar.getMenus().add(edit);
        help.getItems().add(about_menu_item);
        menubar.getMenus().add(help);
        teacher.getItems().add(view_all_menu_item);
        teacher.getItems().add(view_classwise_menu_item);
        menubar.getMenus().add(teacher);
        admin.getItems().add(dashboard_menu_item);
        menubar.getMenus().add(admin);
        chat.getItems().add(open_chat_menu_item);
        menubar.getMenus().add(chat);
        gridPane.getColumnConstraints().add(columnConstraints);
        gridPane.getRowConstraints().add(rowConstraints);
        gridPane.getRowConstraints().add(rowConstraints0);
        gridPane.getRowConstraints().add(rowConstraints1);
        gridPane.getRowConstraints().add(rowConstraints2);
        gridPane.getRowConstraints().add(rowConstraints3);
        gridPane.getRowConstraints().add(rowConstraints4);
        gridPane.getRowConstraints().add(rowConstraints5);
        gridPane.getRowConstraints().add(rowConstraints6);
        gridPane.getRowConstraints().add(rowConstraints7);
        gridPane.getRowConstraints().add(rowConstraints8);
        gridPane.getRowConstraints().add(rowConstraints9);
        gridPane.getRowConstraints().add(rowConstraints10);
        gridPane.getRowConstraints().add(rowConstraints11);
        gridPane.getRowConstraints().add(rowConstraints12);
        gridPane.getRowConstraints().add(rowConstraints13);
        gridPane.getRowConstraints().add(rowConstraints14);
        gridPane0.getColumnConstraints().add(columnConstraints0);
        gridPane0.getColumnConstraints().add(columnConstraints1);
        gridPane0.getColumnConstraints().add(columnConstraints2);
        gridPane0.getRowConstraints().add(rowConstraints15);
        gridPane0.getRowConstraints().add(rowConstraints16);
        gridPane0.getRowConstraints().add(rowConstraints17);
        student_anchor.getChildren().add(student_image);
        student_anchor.getChildren().add(student_label);
        gridPane0.getChildren().add(student_anchor);
        teacher_anchor.getChildren().add(teacher_image);
        teacher_anchor.getChildren().add(teacher_label);
        gridPane0.getChildren().add(teacher_anchor);
        marksheet_anchor.getChildren().add(marksheet_label);
        marksheet_anchor.getChildren().add(marksheet_image);
        gridPane0.getChildren().add(marksheet_anchor);
        legder_anchor.getChildren().add(ledger_label);
        legder_anchor.getChildren().add(ledger_label0);
        gridPane0.getChildren().add(legder_anchor);
        chart_anchor.getChildren().add(chart_label);
        chart_anchor.getChildren().add(chart_image);
        gridPane0.getChildren().add(chart_anchor);
        attendance_anchor.getChildren().add(attendance_image);
        attendance_anchor.getChildren().add(attendance_label);
        gridPane0.getChildren().add(attendance_anchor);
        session_anchor.getChildren().add(session_image);
        session_anchor.getChildren().add(session_label);
        gridPane0.getChildren().add(session_anchor);
        classwise_anchor.getChildren().add(classwise_anchor0);
        classwise_anchor.getChildren().add(classwise_label);
        gridPane0.getChildren().add(classwise_anchor);
        anchorPane.getChildren().add(gridPane0);
        return BorderPane;
    }
}
