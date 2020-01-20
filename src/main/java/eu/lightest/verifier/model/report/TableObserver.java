package eu.lightest.verifier.model.report;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Observable;
import java.util.Observer;

public class TableObserver implements Observer {
    
    private TableView table = null;
    
    public TableObserver(TableView table) {
        this.table = table;
        
        TableColumn statusColumn = new TableColumn("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setMinWidth(75);
        statusColumn.setMaxWidth(75);
        
        TableColumn lineColumn = new TableColumn("Message");
        lineColumn.setCellValueFactory(new PropertyValueFactory<>("msg"));
        
        table.getColumns().clear();
        table.getColumns().addAll(statusColumn, lineColumn);
        
        table.getItems().addListener((ListChangeListener) (c -> {
            c.next();
            final int size = table.getItems().size();
            if(size > 0) {
                table.scrollTo(size - 1);
            }
        }));
    }
    
    @Override
    public void update(Observable o, Object line) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                TableObserver.this.table.getItems().add(line);
            }
        });
    }
    
}
