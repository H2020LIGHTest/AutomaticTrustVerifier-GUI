package eu.lightest.verifier.controller;

import eu.lightest.verifier.exceptions.DNSException;
import eu.lightest.verifier.model.report.Report;
import eu.lightest.verifier.model.report.ReportStatus;
import eu.lightest.verifier.model.report.StdOutReportObserver;
import eu.lightest.verifier.model.report.TableObserver;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipException;

public class UIController implements Initializable {
    
    private static Logger logger = Logger.getLogger(UIController.class);
    protected File transactionFile = null;
    protected File policyFile = null;
    private Report report = null;
    @FXML
    private Pane filePanePolicy;
    @FXML
    private Pane filePaneTransaction;
    @FXML
    private Button processButton;
    @FXML
    private Label titleLabel;
    @FXML
    private TableView reportTable;
    
    
    public UIController() {
        UIController.logger.info("init UI controller via default ctor...");
    }
    
    public UIController(Report report) {
        this.report = report;
    }
    
    /**
     * MainView function to demo the UIController in a headless way.
     *
     * @param argv ignored
     */
    public static void main(String[] argv) throws IOException, ZipException, DNSException {
        Report report = new Report();
        
        StdOutReportObserver reporter = new StdOutReportObserver();
        report.addObserver(reporter);
        report.addLine("Reporter Initialized!", ReportStatus.OK);
        
        UIController controller = new UIController(report);
        //controller.transactionFile = new File("Examples/iaik-test-scheme/LIGHTest_simpleContract.asice");
        //controller.policyFile = new File("src/test/policy_eidas.tpl");

//        UIController.initTestData(controller);
        
        controller.processFile();
    }

//    private static void initTestData(UIController controller) {
//        controller.transactionFile = new File("Examples/theAuctionHouse/LIGHTest_theAuctionHouse.asice");
//        controller.policyFile = new File("src/test/policy_eidas.tpl");
//
//        if(controller.filePanePolicy != null) {
//            controller.setFile(controller.filePaneTransaction, controller.transactionFile);
//            controller.setFile(controller.filePanePolicy, controller.policyFile);
//
//            controller.appendToTitle("\n!!! TEST MODE !!!");
//        }
//    }
    
    private static String getFileExtension(File file) {
        String extension = "";
        
        try {
            if(file != null && file.exists()) {
                String name = file.getName();
                extension = name.substring(name.lastIndexOf("."));
            }
        } catch(Exception e) {
            extension = "";
        }
        
        return extension;
        
    }
    
    public void appendToTitle(String text) {
        if(this.titleLabel != null) {
            this.titleLabel.setText(this.titleLabel.getText() + " " + text);
        }
    }
    
    private void setFile(Pane pane, File file) {
        if(!file.exists()) {
            UIController.logger.info("File not found: " + file.getAbsolutePath());
            return;
        }
        
        String extension = UIController.getFileExtension(file);
        String label = "ERROR";
        
        if(pane == this.filePanePolicy) {
            UIController.logger.info("Set policy file to " + file.getAbsolutePath());
            if(!extension.equals(".tpl")) {
                UIController.logger.info("Wrong extension for policy: " + extension);
                return;
            }
            this.policyFile = file;
            label = "POLICY:\n\n" + file.getName();
            
        } else if(pane == this.filePaneTransaction) {
            UIController.logger.info("Set transaction file to " + file.getAbsolutePath());
            if(!extension.equals(".asic") &&
                    !extension.equals(".asics") &&
                    !extension.equals(".asice") &&
                    !extension.equals(".pdf") &&
                    !extension.equals(".xml")) {
                UIController.logger.info("Wrong extension for transaction: " + extension);
                return;
            }
            this.transactionFile = file;
            label = "TRANSACTION:\n\n" + file.getName();
        }
        
        
        setLabel(pane, label);
        toggleButton();
    }
    
    private void setLabel(Pane parent, String label) {
        for(Node child : parent.getChildren()) {
            if(child instanceof Label) {
                ((Label) child).setText(label);
            }
        }
    }
    
    private void toggleButton() {
        if(this.transactionFile != null && this.policyFile != null) {
            this.processButton.setDisable(false);
        } else {
            this.processButton.setDisable(true);
        }
    }
    
    
    /**
     * Function to choose the transaction
     *
     * @throws IOException
     */
    public void chooseFile(Event event) throws IOException {
        
        Pane source = (Pane) event.getSource();
        UIController.logger.info("Choosing file ");
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open file");
        
        configureExtensionFilter(source, fileChooser);
        
        File file = fileChooser.showOpenDialog(new Stage());
        if(file != null) {
            setFile(source, file);
        }
        
        // Send the transaction_file for extraction
        
    }
    
    private void configureExtensionFilter(Pane pane, FileChooser fileChooser) {
        
        if(pane == this.filePanePolicy) {
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TPL files (*.tpl)", "*.tpl");
            fileChooser.getExtensionFilters().add(extFilter);
            
        } else if(pane == this.filePaneTransaction) {
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ASiC, XML or PDF", "*.asice", "*.asics", "*.asic", "*.pdf", "*.xml");
            fileChooser.getExtensionFilters().add(extFilter);
            
            FileChooser.ExtensionFilter extFilter3 = new FileChooser.ExtensionFilter("ASiC Extended files (*.asice)", "*.asice");
            fileChooser.getExtensionFilters().add(extFilter3);
            
            FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("ASiC Simple files (*.asics)", "*.asics");
            fileChooser.getExtensionFilters().add(extFilter2);
            
            FileChooser.ExtensionFilter extFilter1 = new FileChooser.ExtensionFilter("ASiC files (*.asic)", "*.asic");
            fileChooser.getExtensionFilters().add(extFilter1);
            
            FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
            fileChooser.getExtensionFilters().add(extFilterPDF);
            
            FileChooser.ExtensionFilter extFilterXML = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
            fileChooser.getExtensionFilters().add(extFilterXML);
        }
    }
    
    /**
     * dragOver event handler
     *
     * @param event
     */
    public void dragOver(DragEvent event) {
        //UIController.logger.info("dragOver");
        
        if(event.getDragboard().hasContent(DataFormat.FILES)) {
            /* allow for both copying and moving, whatever user chooses */
            //UIController.logger.info("dragOver ok");
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        } else {
            //UIController.logger.info("dragOver not ok");
        }
        
        event.consume();
        
    }
    
    /**
     * dragEntered event handler
     *
     * @param event
     */
    public void dragEntered(DragEvent event) {
        //UIController.logger.info("dragEntered");
        
        if(event.getDragboard().hasContent(DataFormat.FILES)) {
            //UIController.logger.info("dragEntered ok");
        } else {
            //UIController.logger.info("dragEntered not ok");
        }
        
        
        event.consume();
    }
    
    /**
     * Eventhandler to handle drag and drop
     */
    @FXML
    public void droppedFile(DragEvent event) throws IOException {
        Pane source = (Pane) event.getSource();
        UIController.logger.info("droppedFile");
        
        Dragboard dropped = event.getDragboard();
        Boolean success = false;
        
        if(dropped.hasContent(DataFormat.FILES)) {
            setFile(source, dropped.getFiles().get(0));
            success = true;
        }
        
        event.setDropCompleted(success);
        
        // consume event
        event.consume();
    }
    
    public void processFile() throws IOException, ZipException, DNSException {
        
        if(this.transactionFile != null && this.policyFile != null) {
            initialize();
            
            this.report.addLine("Initializing Verification", ReportStatus.OK);
            
            GUIVerificationProcess verify = new GUIVerificationProcess(this.transactionFile, this.policyFile, this.report);
            
            verify.setOnRunning((succeesesEvent) -> {
                this.processButton.setDisable(true);
            });
            
            verify.setOnSucceeded((succeededEvent) -> {
                this.processButton.setDisable(false);
                //this.report.addLine("Shutdown complete!");
                if(verify.getValue()) {
                    this.report.addLine("VERIFICATION WAS SUCCESSFUL.", ReportStatus.OK);
                } else {
                    this.report.addLine("VERIFICATION FAILED.", ReportStatus.FAILED);
                }
            });
            
            ExecutorService executorService
                    = Executors.newFixedThreadPool(1);
            
            this.report.addLine("Executing Verification", ReportStatus.OK);
            executorService.execute(verify);
            
            //this.report.addLine("Verification completed", ReportStatus.OK);
            //executorService.shutdown();
            
        } else {
            if(this.transactionFile != null) {
                UIController.logger.warn("No transaction specified ...");
            }
            if(this.policyFile != null) {
                UIController.logger.warn("No policy specified ...");
            }
        }
    }

//    public void enableTestmode(MouseEvent mouseEvent) {
//        this.report.addLine("Test Mode enabled!");
//        UIController.initTestData(this);
//    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //initialize();
        this.report = new Report();
        
        StdOutReportObserver stdoutReporter = new StdOutReportObserver();
        this.report.addObserver(stdoutReporter);
    }
    
    private void initialize() {
        UIController.logger.info("initialize()");
        
        this.report = new Report();
        
        StdOutReportObserver stdoutReporter = new StdOutReportObserver();
        this.report.addObserver(stdoutReporter);
        
        if(this.reportTable != null) {
            reportTable.getItems().clear();
            TableObserver tableReporter = new TableObserver(this.reportTable);
            this.report.addObserver(tableReporter);
        }
        
        this.report.addLine("Reporter Initialized!", ReportStatus.OK);
    }
}



