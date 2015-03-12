/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ebmdistributor;

// Use library to prevent user from running same app twice
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;



/**
 *
 * @author akhare
 */
public class EBMDistributor extends Application {
    
   
    @Override
    public void start(Stage stage) throws Exception {
        
        // junique only prevents one user from opening multiple instances
        String appId = "ReactionX.EBMDistributor";
	boolean alreadyRunning;
	try 
        {
            JUnique.acquireLock(appId);
            alreadyRunning = false;
	}
        catch (AlreadyLockedException e) 
        {
            alreadyRunning = true;

            System.out.println("Another instance is already running!");

            System.exit(0);
                
	}
	
        if (!alreadyRunning)
        {
        
            Parent root = FXMLLoader.load(getClass().getResource("QueueReaderDoc.fxml"));
        
            Scene scene = new Scene(root);
        
            stage.setScene(scene);
            
            
            stage.setTitle("EBM Distributor Tools");
            stage.getIcons().add(new Image("images/hub48x48.png"));
            
            stage.show();
        
        }
        
        
        
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
