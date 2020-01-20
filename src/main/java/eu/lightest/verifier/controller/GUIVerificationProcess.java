package eu.lightest.verifier.controller;

import eu.lightest.verifier.model.report.Report;
import javafx.concurrent.Task;

import java.io.File;

public class GUIVerificationProcess extends Task<Boolean> {
    
    private final VerificationProcess process;
    
    public GUIVerificationProcess(File transactionFile, File policyFile, Report report) {
        process = new VerificationProcess(transactionFile, policyFile, report);
    }
    
    @Override
    protected Boolean call() throws Exception {
        return this.process.verify();
    }
}
