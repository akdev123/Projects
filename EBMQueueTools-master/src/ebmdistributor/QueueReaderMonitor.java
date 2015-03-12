package ebmdistributor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author akhare
 */

import javafx.concurrent.Task;
import javafx.event.ActionEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.scene.control.Button;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;



public class QueueReaderMonitor extends Task<Integer> 
{
    private Button but_Start;
    private ExecutorService QueuyeReaderExecutorService;
    
    public void set_but_Start(Button but_Start)
    {
       this.but_Start = but_Start;
    }

    public Button get_but_Start()
    {
       return but_Start;
    }
        
    public void set_QRS(ExecutorService QueuyeReaderExecutorService)
    {
       this.QueuyeReaderExecutorService = QueuyeReaderExecutorService;
    }

    public ExecutorService get_QRS()
    {
       return QueuyeReaderExecutorService;
    }
    
    
    protected Integer call() throws Exception 
    {
        
        QueuyeReaderExecutorService.shutdown();
        while (!QueuyeReaderExecutorService.isTerminated()) 
        {                
            int x = 1;
        }
        System.out.println("Finished all threads");
            
        // Thread.sleep(2000);
        
        Platform.runLater(new Runnable() 
        {
            public void run() 
            {   
                System.out.println( "(boolean)but_Start.getUserData()= " + (boolean)but_Start.getUserData() + " ");
                
                if((boolean)but_Start.getUserData())
                {
                    // set current state to false so if fire -ed again will continue on.
                    but_Start.setUserData(false);
            
                    // but_Start.setStyle("-fx-graphic: url('images/play48x48.png');");
                    // but_Start.setText("Start");
            
                    but_Start.fire();
                }  
                    // See to pass data http://stackoverflow.com/questions/14187963/passing-parameters-javafx-fxml
                    
            }
        });
        
        
        return 0;
    }
    
    
    public void act_StartProcessing() 
    {
        new Thread(this).start();

    }

    public void act_StopProcessing() 
    {
        this.cancel();                  
    }
        
    
}