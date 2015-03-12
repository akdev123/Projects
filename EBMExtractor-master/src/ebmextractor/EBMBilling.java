/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ebmextractor;

import static java.lang.Math.ceil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author JLP
 */
public class EBMBilling 
{// class EBMBilling
    public int RateType_int = 1;
    public float Rate1_int = 0.10f;
    public float Rate2_int = 0.10f;
    public float Rate3_int = 0.10f;    
    public int Increment1_int = 30;
    public int Increment2_int = 30;
    public int Increment3_int =30;
    public int CurrentMessageDelieveredFlag = 0;
    public int CurrentMMLS = 1;
    
    // move these to global values in package?
    private static String MainUser = "EBMSQLUser";
    private static String MainPass = "eSU!0126";
    private static String MainDB = "10.11.0.130";
     
    // Used to round float values to four decimal places
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
    
    // Read values for specified user from DB
    
    public void GetUserBillingInfo(int UserId_int)
    {// GetUserBillingInfo
        Statement SelectResultsStmt = null;
        ResultSet SelectResultsRS = null;
        
        try 
        {
            Connection connMain = DriverManager.getConnection("jdbc:mysql://" + MainDB + ":3306/test",MainUser,MainPass);

            String QueryString =      " SELECT "  
                                    + "     RateType_int, "
                                    + "     Rate1_int, "
                                    + "     Rate2_int, "
                                    + "     Rate3_int, "
                                    + "     Increment1_int, "
                                    + "     Increment2_int, "
                                    + "     Increment3_int, "
                                    + " FROM " 
                                    + "     simplebilling.billing  "
                                    + " WHERE "
                                    + "     UserId_int = " + UserId_int;



            SelectResultsStmt = connMain.createStatement();
            SelectResultsRS = SelectResultsStmt.executeQuery(QueryString);

            int CurrentBillingAmount = 0;
            

            int CurrentRowCount = 0;

            // Now do something with the ResultSet ....
            while ( SelectResultsRS.next( ) ) 
            {// While SelectResultsRS
                CurrentRowCount++;
                
                
                RateType_int = SelectResultsRS.getInt("RateType_int");   
                Rate1_int = SelectResultsRS.getFloat("Rate1_int");   
                Rate2_int = SelectResultsRS.getFloat("Rate2_int");   
                Rate3_int = SelectResultsRS.getFloat("Rate3_int");   
                Increment1_int = SelectResultsRS.getInt("Increment1_int");   
                Increment2_int = SelectResultsRS.getInt("Increment2_int");   
                Increment3_int = SelectResultsRS.getInt("Increment3_int");   
                

                // Get Billing amount - need billing method in JAVA
                CurrentBillingAmount = 0;


            }// While SelectResultsRS


            if(CurrentRowCount == 0)
            {
                // Write to log that there was nothing to process
                System.out.println("No results found this loop");

            }





        } 
        catch (SQLException ex) 
        {
             // handle any errors
             System.out.println("SQLException: " + ex.getMessage());
             System.out.println("SQLState: " + ex.getSQLState());
             System.out.println("VendorError: " + ex.getErrorCode());
        }
        finally 
        {
            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (SelectResultsRS != null) 
            {
                try
                {
                    SelectResultsRS.close();
                }
                catch (SQLException sqlEx) 
                { 

                } // ignore

                SelectResultsRS = null;
            }

            if (SelectResultsStmt != null) 
            {
                try 
                {
                   SelectResultsStmt.close();
                }
                catch (SQLException sqlEx) 
                { 

                } // ignore

                SelectResultsStmt = null;
            }


        }

    
    }// GetUserBillingInfo
    
    
    /*
    
    TypeMask_ti:
        1 = Voice
        2 = eMail
        3 = SMS
    
    */
    public float CalculateBilling(int TypeMask_ti, int CurrTotalConnectTime_int )
    {
        float CurrentBillingAmount = 0;
        int CurrNumberofIncrements;
        
        
        switch(RateType_int)
        {
            
            // SimpleX -  Rate 1 at Increment 1
            case 1:
                
                if(CurrTotalConnectTime_int > 0)
                {
                    CurrNumberofIncrements = (int) ceil(CurrTotalConnectTime_int / Increment1_int);                    
                    CurrentBillingAmount = round(CurrNumberofIncrements * Rate1_int, 4);
                }      
                else
                {
                    CurrentBillingAmount = 0;                    
                }
                        
                break;
            
            // SimpleX -  Rate 2 at 1 per unit    
            case 2:
                CurrNumberofIncrements = 1;
                CurrentBillingAmount = Rate1_int; // CurrRate1 = Rate1_int
                
                break;
                   
            // SimpleX -  Per Message Left - Flat Rate - Rate 1 at 1 per unit - Limit length of recording    
            case 3:
                
                if(CurrentMessageDelieveredFlag > 0)
                {
                    CurrNumberofIncrements = 1;
                    CurrentBillingAmount = Rate1_int;
                    
                }
                
                break;
            
            // SimpleX -  Per connected call - Rate 1 at Incerment 1 times MMLS specified number of increments - uses MMLS    
            case 4:
                if(CurrTotalConnectTime_int > 0)
                {
                    CurrNumberofIncrements = CurrentMMLS;
                    CurrentBillingAmount =  round(CurrNumberofIncrements * Rate1_int, 4);
                    
                    
                }
                else
                {
                    CurrentBillingAmount = 0;
                }
                break;
            
            // SimpleX -  Per attempt - Rate 1 at Increment 1 - uses MMLS    
            case 5:
                
                CurrNumberofIncrements = CurrentMMLS;
                CurrentBillingAmount =  round(CurrNumberofIncrements * Rate1_int, 4);
                
                break;
                
            default:
                
                CurrentBillingAmount = Rate1_int;
                
                break;
                
        }
            
        
        
        return CurrentBillingAmount;
    }
    
    
}// class EBMBilling
