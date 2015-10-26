package ebmextractor;




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



public class ExtractorMonitor  extends Task<Integer> 
{
    private Button but_Start;
    private ExecutorService QueueReaderExecutorService;
    
    public void set_but_Start(Button but_Start)
    {
       this.but_Start = but_Start;
    }

    public Button get_but_Start()
    {
       return but_Start;
    }
        
    public void set_QRS(ExecutorService QueueReaderExecutorService)
    {
       this.QueueReaderExecutorService = QueueReaderExecutorService;
    }

    public ExecutorService get_QRS()
    {
       return QueueReaderExecutorService;
    }
    
    
    protected Integer call() throws Exception 
    {
        
        QueueReaderExecutorService.shutdown();
        while (!QueueReaderExecutorService.isTerminated()) 
        {                
            int x = 1;
        }
        System.out.println("Finished all threads");
        
      
            try {
                Thread.sleep(5000);
            } catch (InterruptedException interrupted) {
                if (isCancelled()) {
                    updateMessage("Cancelled");
                    
                }
            }
            
       
        
        Platform.runLater(new Runnable() 
        {
            public void run() 
            {   
                System.out.println( "(boolean)but_Start.getUserData()= " + (boolean)but_Start.getUserData() + " ");
                
                if((boolean)but_Start.getUserData())
                {
                    // set current state to false so if fire -ed again will continue on.
                    but_Start.setUserData(false);
            
                   
                    but_Start.fire();
                }  
                    
                    
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



