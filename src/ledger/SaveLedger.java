/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ledger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

/**
 *
 * @author subas
 */
public class SaveLedger {

    File file;

    public void saveLedgerInExcel(SpreadsheetView spread_sheet, String initial_file_name, Window owner_window) {
        FileChooser file_chooser = new FileChooser();
        file_chooser.setInitialFileName(initial_file_name);
        file_chooser.setTitle("Save Current Ledger");
        //Open directory from existing directory
        if (file != null) {
            File existDirectory = file.getParentFile();
            file_chooser.setInitialDirectory(existDirectory);
        }
        //Set extension filter for xlsx
        FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("MS Excel 2004-2018 (*.xlsx)", "*.xlsx");
        file_chooser.getExtensionFilters().add(extFilter2);
        //Set extension filter for xls
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MS Excel 1997-2003 (*.xls)", "*.xls");
        file_chooser.getExtensionFilters().add(extFilter);
        //making the workbookname
        DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        Date date = new Date();
        String sheet_name = initial_file_name + date_format.format(date);
        //Show save file dialog
        file = file_chooser.showSaveDialog(owner_window);
        if (file != null) {
            if (!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }
            String file_name = file.getName();
            String file_extension = file_name.substring(file_name.indexOf(".") + 1, file.getName().length());
            if (file_extension.equalsIgnoreCase("xlsx")) {
                //writing in the excel file in xlsx format i.e. onging support or extension from 2004-present(2018)
                try {
                    
                    XSSFWorkbook workbook = new XSSFWorkbook();
                    XSSFSheet sheet = workbook.createSheet(sheet_name);
                    int columns = spread_sheet.getColumns().size();
                    Grid spread_grid = spread_sheet.getGrid();
                    int rows = spread_grid.getRowCount();
                    //Now putting the headers in the excel first
                    ObservableList<String> column_headers = spread_grid.getColumnHeaders();
                    XSSFRow header_row = sheet.createRow(0);
                    for (int j = 0; j < columns; j++) {
                        /*auto sizing the columns of worksheet*/                       
                        XSSFCell cell = header_row.createCell(j);
                        cell.setCellValue(column_headers.get(j));
                    }
                    //after headers we are putting the data into the excel
                    ObservableList<ObservableList<SpreadsheetCell>> spread_rows = spread_grid.getRows();
                    for (int i = 1; i <= rows; i++) {
                        XSSFRow row = sheet.createRow(i);
                        ObservableList<SpreadsheetCell> spread_cells_of_a_row = spread_rows.get(i - 1);
                        for (int j = 0; j < columns; j++) {
                            XSSFCell cell = row.createCell(j);
                            cell.setCellValue(spread_cells_of_a_row.get(j).getItem().toString());
                        }
                    }
                    for(int j=0;j<columns;j++){
                         sheet.autoSizeColumn(j);
                    }
                    FileOutputStream file_output_stream = new FileOutputStream(file);
                    workbook.write(file_output_stream);
                    file_output_stream.close();                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Logger.getLogger(SaveLedger.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (file_extension.equalsIgnoreCase("xls")) {
                //writing the excel file in xls format ie supported from 1997 to 2003
                try {
                    HSSFWorkbook workbook = new HSSFWorkbook();
                    HSSFSheet sheet = workbook.createSheet(sheet_name);
                    int columns = spread_sheet.getColumns().size();
                    Grid spread_grid = spread_sheet.getGrid();
                    int rows = spread_grid.getRowCount();
                    //Now putting the headers in the excel first
                    ObservableList<String> column_headers = spread_grid.getColumnHeaders();
                    HSSFRow header_row = sheet.createRow(0);
                    for (int j = 0; j < columns; j++) {
                        /*auto sizing the columns of worksheet*/
                        sheet.autoSizeColumn(j);
                        HSSFCell cell = header_row.createCell(j);
                        cell.setCellValue(column_headers.get(j));
                    }
                    //after headers we are putting the data into the excel
                    ObservableList<ObservableList<SpreadsheetCell>> spread_rows = spread_grid.getRows();
                    for (int i = 1; i <= rows; i++) {
                        HSSFRow row = sheet.createRow(i);
                        ObservableList<SpreadsheetCell> spread_cells_of_a_row = spread_rows.get(i - 1);
                        for (int j = 0; j < columns; j++) {
                            HSSFCell cell = row.createCell(j);
                            cell.setCellValue(spread_cells_of_a_row.get(j).getItem().toString());
                        }
                    }
                    FileOutputStream file_output_stream = new FileOutputStream(file);
                    workbook.write(file_output_stream);
                    file_output_stream.close();                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Logger.getLogger(SaveLedger.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Alert a = new Alert(AlertType.ERROR);
                a.setHeaderText("Error Extension error !!!");
                a.setContentText("You can only save file in *.xlsx or *.xls extension");
                a.show();
            }
        } else {
            
        }
        System.gc();

    }
}
