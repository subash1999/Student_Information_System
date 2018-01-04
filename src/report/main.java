///*finally dynamically adding the rows and column to the table of
//            marks table 
//            *********************
//            *******************************************
//            ****************************************
//            ****************************************/
//            try {
//            /**********************************
//             * TABLE COLUMN ADDED DYNAMICALLY *
//             **********************************/
//            for(int i=0, k=0; i<result.getMetaData().getColumnCount(); i++){
//                //We are using non property style for making dynamic table
//                final int j = i;                
//                String col_name = result.getMetaData().getColumnName(i+1);
//                if("CAST(SN AS CHAR)".equalsIgnoreCase(col_name)){
//                    col_name = "SN";
//                }
//                TableColumn col = new TableColumn(col_name);
//                col.setStyle("-fx-alignment : center;");                
//                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
//                    @Override
//        public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
//                        //System.out.println(param.getValue().get(j).toString());
//                        return  new SimpleStringProperty(param.getValue().get(j).toString());
//                        
//                    }                    
//                });
//                marks_table.getColumns().addAll(col);                    
//            }
//        } catch (SQLException e) {
//            System.out.println("Error at adding columns dynamically at "
//                    + "makeMarksTable() at ReportCardController : "
//                    + e.getMessage());
//        }
//
//        try {
//            /********************************
//             * Data added to ObservableList *
//             ********************************/
//            while(result.next()){
//                //Iterate Row
//                ObservableList<String> row = FXCollections.observableArrayList();
//                for(int i=1 ; i<=result.getMetaData().getColumnCount(); i++){
//                    //Iterate Column
//                    if("null".equalsIgnoreCase(result.getString(i))){
//                        row.add("");
//                    }
//                    else{
//                        row.add(result.getString(i));
//                    }
//                }                
//                marks.add(row);
//            }
//        } catch (Exception e) {
//            System.out.println("Error at adding rows dynamically at "
//                    + "makeMarksTable() at ReportCardController : "
//                    + e.getMessage());}
//          //FINALLY ADDED TO TableView
//          marks_table.setItems(marks);
//          marks_table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
