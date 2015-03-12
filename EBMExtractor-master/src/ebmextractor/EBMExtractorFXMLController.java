/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ebmextractor;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 *
 * @author akhare
 */
public class EBMExtractorFXMLController implements Initializable {
    
    private GridPane FeedBackGrid;
    ExecutorService QueuyeReaderExecutorService;
    
    public boolean RunningButtonState;
    public boolean RunningQRState;
    private static int NumberOfQueueThreads = 10;
    public ResultsReader[] QR; 
         
    private ProgressBar[] prog_QR;
    private Label[] lab_QR;
    
    private GridPane fx_Grid1;
    
    @FXML
    private Button but_Start;
    @FXML
    private AnchorPane EBMExtractorView;
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {

        // State is off by default
        RunningButtonState = false;
        but_Start.setUserData(RunningQRState);
        but_Start.setStyle("-fx-graphic: url('images/play48x48.png');");
                
        QR = new ResultsReader[NumberOfQueueThreads];
        
        prog_QR = new ProgressBar[NumberOfQueueThreads];
        lab_QR = new Label[NumberOfQueueThreads];
        
        FeedBackGrid = new GridPane();
        FeedBackGrid.setPrefHeight(240);
        FeedBackGrid.setPrefWidth(565);
        FeedBackGrid.setLayoutX(18.0); 
        FeedBackGrid.setLayoutY(148.0); 
        EBMExtractorView.getChildren().add(FeedBackGrid);
        
        
        
       for(int i=0; i<NumberOfQueueThreads; i++)
        {
            
            int CurrPosX = (i*30) + 35;
            int XSpacing = 5;
                        
         
            prog_QR[i] = new ProgressBar();
            
            lab_QR[i] = new Label("Thread " + i);
            lab_QR[i].setPrefHeight(11);
            lab_QR[i].setPrefWidth(300);
            lab_QR[i].setStyle("-fx-font-family: Myriad Web Pro Bold; -fx-font-size: 10px; -fx-font-weight: bold; -fx-base: #b6e7c9;");
            
            prog_QR[i].setPrefHeight(11);
            prog_QR[i].setProgress(0);
            prog_QR[i].setMinWidth(200);
            prog_QR[i].setStyle("padding-bottom:15px;");
            
           
            
            System.out.println( "lab_QR X[" + i + "] " + lab_QR[i].getLayoutX());
            System.out.println( "prog_QR X[" + i + "] "  +  prog_QR[i].getLayoutX());
                
            
   
            
            FeedBackGrid.add(lab_QR[i],  0, (i*2)); 
            FeedBackGrid.add(prog_QR[i], 0, (i*2)+1); 
            
            FeedBackGrid.setValignment(prog_QR[i], VPos.TOP);
          
            
           
             
        }
        
    }    
    
    
    @FXML
    private void act_Start(ActionEvent event) 
    {
        
         RunningQRState = (boolean)but_Start.getUserData();
        
        if(RunningQRState)
        {// was on    
         
            RunningQRState = false;
            but_Start.setUserData(RunningQRState);
            
            QueuyeReaderExecutorService.shutdownNow();
            
            but_Start.setStyle("-fx-graphic: url('images/play48x48.png');");
            but_Start.setText("Start");
                        
        }
        else
        {// was off
          
            but_Start.setStyle("-fx-graphic: url('images/stop48x48.png');");
            but_Start.setText("Stop");
                        
            RunningQRState = true;
            but_Start.setUserData(RunningQRState);
            
            // Set up thread pool
            QueuyeReaderExecutorService = Executors.newFixedThreadPool(NumberOfQueueThreads);
                                   
            for(int i=0; i<NumberOfQueueThreads; i++)
            {
                QR[i] = new ResultsReader(i);

                prog_QR[i].progressProperty().bind(QR[i].progressProperty());
                lab_QR[i].textProperty().bind(QR[i].messageProperty());

                System.out.println( "CurrTaskNumber " + i + " is now reset! ");

                
                QR[i].setQDVC(this);
                
                QueuyeReaderExecutorService.execute(QR[i]);
                
                
            }
            
   
            
             RunningQRState = false;
             
             ExtractorMonitor QRM = new ExtractorMonitor();
                          
             QRM.set_but_Start(but_Start);
             QRM.set_QRS(QueuyeReaderExecutorService);
             QRM.act_StartProcessing();             
            
        }
        
        
    }
           
    public void ResultsReaderCallBackonComplete(int CurrTaskNumber)
    {
              
        System.out.println( "CurrTaskNumber " + CurrTaskNumber + " is Complete! ");
        
    }
    
}
