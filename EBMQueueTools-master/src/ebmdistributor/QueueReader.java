

package ebmdistributor;



import javafx.concurrent.Task;
import javafx.event.ActionEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


// String helperes - See http://ostermiller.org/utils/
import static com.Ostermiller.util.StringHelper.escapeSQL;
import static java.lang.Math.abs;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.stage.Window;

 
 
 public class QueueReader extends Task<Integer> 
 {
    
    private static int DTSStatusToProcess = 1;
    private static int NumberOfQueueThreads = 1;
    private static int NumberOfQueueContactsToProcessPerThread = 1000;
    private static int NumberOfRecordsToInsertAtATime = 100;
    private static String TypeMask = "1,2,3,5";
    private static String RemoteUser = "root";
    private static String RemotePass = "RX1664";
    private static String MainUser = "EBMSQLUser";
    private static String MainPass = "eSU!0126";
    private int CurrModValue;
    private EBMDistributorFXMLController QRVC;
    private Window primaryStage;
 
    public void setQDVC(EBMDistributorFXMLController QRVC)
    {  this.QRVC = QRVC; }

    public EBMDistributorFXMLController getQDVC()
    { return QRVC; }
 
    public QueueReader(int CurrModValue) 
    { this.CurrModValue = CurrModValue;}
 
    public void act_StartProcessing() 
    { new Thread(this).start(); }

    public void act_StopProcessing() 
    { this.cancel();}
    
   
 
 

    @Override protected Integer call() throws Exception 
    {
        int iterations = 0;

        // Statements
        Statement SelectQueueStmt = null;
        Statement UpdateQueueStmt = null;
        Statement InsertRemoteQueueStmt = null;
        Statement DeviceStmt = null;
        Statement DeviceCountStmt = null;
        Statement CollectPrimaryStmt = null;
        Statement InsertRemoteResultsErrorLogStmt = null;
        //ResultSets
        ResultSet SelectQueueRS = null;
        ResultSet DeviceRS = null;
        ResultSet DeviceCountRS = null;
        ResultSet CollectPrimaryResultSet = null;

        boolean flag = false;

        List<Integer> ids = new ArrayList<Integer>();
 
                try 
                {

                    Connection connRemote = DriverManager.getConnection("jdbc:mysql://10.11.0.130:3306/test",MainUser,MainPass); 
                    // Opening Transaction Here
                    connRemote.setAutoCommit(false);
                    String maindbsql = null;
                    maindbsql = "SELECT IP_vch FROM simplexfulfillment.device_test WHERE Enabled_dt = 1 ";
                   //maincountsql = "SELECT Count(IP_vch) AS ipcount FROM simplexfulfillment.device_test WHERE Enabled_dt = 1 ";

                    Map<String,Connection> connections = new HashMap<>();
                    DeviceStmt = connRemote.createStatement();
                    DeviceRS = DeviceStmt.executeQuery(maindbsql);

                            while(DeviceRS.next()){
                                
                                try{ // This is the try and catch required to skip the dialer and move forward in case exception is caught

                                final String ip_address = DeviceRS.getString("IP_vch");
                                // final int ip_count = DeviceRS.getInt("ipcount");
                                System.out.println("Value of IP_vch Field:"+ip_address);
                                connections.put(ip_address,DriverManager.getConnection("jdbc:mysql://" + ip_address + ":3306/test",RemoteUser,RemotePass));

                                if(connections.isEmpty()){
                                System.err.println("Status not set to 1 for any of the databases");
                                }
                                else
                                {
                                System.out.println("Connection to"+ip_address+"is successfull");
                                }
                                }
                                catch(SQLException ex1){
                                    
                                    System.out.println("While Loop Stack Trace Below:");
                                    ex1.printStackTrace();
                                }
                                
                                
                          }//END Of while(DeviceRS.next())

                        int count = 0;

                        for(final String ip : connections.keySet())
                        {
                                    count++;
                                    System.out.println("for loop count: "+count);
                                    final Connection conn = connections.get(ip); 

                                    String QueryString = " SELECT " 

                                                        + " DTSId_int, "
                                                        + " DTS_UUID_vch, "
                                                        + " BatchId_bi, "
                                                        + " DTSStatusType_ti, "
                                                        + " TypeMask_ti, "
                                                        + " TimeZone_ti, "
                                                        + " CurrentRedialCount_ti, "
                                                        + " UserId_int, "
                                                        + " PushLibrary_int, "
                                                        + " PushElement_int, "
                                                        + " PushScript_int, "
                                                        + " PushSkip_int, "
                                                        + " EstimatedCost_int, "
                                                        + " ActualCost_int, "
                                                        + " CampaignTypeId_int, "
                                                        + " GroupId_int, "
                                                        + " Scheduled_dt, "
                                                        + " Queue_dt, "
                                                        + " Queued_DialerIP_vch, "
                                                        + " DialString_vch, "
                                                        + " Sender_vch, "
                                                        + " XMLControlString_vch, "
                                                        + " ContactString_vch, "
                                                        + " LastUpdated_dt, "
                                                        + " ShortCode_vch, "
                                                        + " ProcTime_int "
                                                        + " FROM " 
                                                        + " test.contactqueue_test "
                                                        + " WHERE "
                                                        + " DTSStatusType_ti = " + DTSStatusToProcess 
                                                        + " AND DTSId_int MOD " + NumberOfQueueThreads + " = " + CurrModValue 
                                                        + " AND Scheduled_dt < NOW() "
                                                        + " AND TypeMask_ti IN (" + TypeMask + ") "
                                                        + " ORDER BY DTSId_int ASC "
                                                        + " LIMIT " + NumberOfQueueContactsToProcessPerThread ;
                                        
                                        //NOTE: with the 'MOD' CONCUR_UPDATABLE isn't necessary
                                        SelectQueueStmt = connRemote.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                        SelectQueueRS = SelectQueueStmt.executeQuery(QueryString);
                                        int CurrentRowCount = 0;
                                        String QueryStringInsertRemote = "";

                                        while ( SelectQueueRS.next( ) && !Thread.interrupted()) 
                                         {
                                            CurrentRowCount++;
                                            // Collecting ID's
                                            ids.add(SelectQueueRS.getInt("DTSId_int"));
                                            
                                                if (isCancelled()) 
                                                    {
                                                      updateMessage("Cancelled");
                                                      break;
                                                    }
                                                    updateProgress(SelectQueueRS.getRow(), NumberOfQueueContactsToProcessPerThread);
                                                    updateMessage("Thread " + CurrModValue + " " + SelectQueueRS.getInt("DTSId_int"));

                                                    if(QueryStringInsertRemote.length() > 0)
                                                        {
                                                        QueryStringInsertRemote = QueryStringInsertRemote + " ,( "
                                                                                    + " " + SelectQueueRS.getInt("BatchId_bi") + ", " // <{BatchId_bi: }>, "
                                                                                    + " " + SelectQueueRS.getInt("DTSStatusType_ti") + ", " // <{DTSStatusType_ti: }>, "
                                                                                    + " " + SelectQueueRS.getInt("TimeZone_ti") + ", " // <{TimeZone_ti: }>, "
                                                                                    + " " + SelectQueueRS.getInt("CurrentRedialCount_ti") + ", " // <{CurrentRedialCount_ti: }>, "
                                                                                    + " " + SelectQueueRS.getInt("UserId_int") + ", " // <{UserId_int: }>, "
                                                                                    + " " + SelectQueueRS.getInt("CampaignTypeId_int") + ", " // <{CampaignTypeId_int: }>, "
                                                                                    + " '" + SelectQueueRS.getString("Scheduled_dt") + "', " // <{Scheduled_dt: }>, "
                                                                                    + " '" + escapeSQL(SelectQueueRS.getString("DialString_vch")) + "', " // <{PhoneStr1_vch: }>, "
                                                                                    + " 'EBMQueueTools', " // <{Sender_vch: }>, "
                                                                                    + " '" + escapeSQL(SelectQueueRS.getString("XMLControlString_vch")) + "' " // <{XMLControlString_vch: }>, "
                                                                                    + ") ";
                                                        }
                                                    else
                                                         {
                                                            // Create first record in Insert statement to remote DTS
                                                            QueryStringInsertRemote = " INSERT INTO distributedtosend.dts_test" 
                                                            + " ( "
                                                            + " `BatchId_bi`, "
                                                            + " `DTSStatusType_ti`, " 
                                                            + " `TimeZone_ti`, "
                                                            + " `CurrentRedialCount_ti`, "
                                                            + " `UserId_int`, " 
                                                            + " `CampaignTypeId_int`, " + " `Scheduled_dt`, "
                                                            + " `PhoneStr1_vch`, " 
                                                            + " `Sender_vch`, "
                                                            + " `XMLControlString_vch` "
                                                            + " ) " 
                                                            + " VALUES "
                                                            + " ( "
                                                            + " " + SelectQueueRS.getInt("BatchId_bi") + ", " 
                                                            + " " + SelectQueueRS.getInt("DTSStatusType_ti") + ", " 
                                                            + " " + SelectQueueRS.getInt("TimeZone_ti") + ", " 
                                                            + " " + SelectQueueRS.getInt("CurrentRedialCount_ti") + ", " 
                                                            + " " + SelectQueueRS.getInt("UserId_int") + ", " 
                                                            + " " + SelectQueueRS.getInt("CampaignTypeId_int") + ", " 
                                                            + " '" + SelectQueueRS.getString("Scheduled_dt") + "', " 
                                                            + " '" + escapeSQL(SelectQueueRS.getString("DialString_vch")) + "', " 
                                                            + " 'EBMQueueTools', " // <{Sender_vch: }>, "
                                                            + " '" + escapeSQL(SelectQueueRS.getString("XMLControlString_vch")) + "' " 
                                                            + ") ";

                                                         }

                                                        



                                                        // Execute insert every NumberOfRecordsToInsertAtATime values 
                                                        if( (CurrentRowCount % NumberOfRecordsToInsertAtATime) == 0)
                                                        {
                                                        InsertRemoteQueueStmt = conn.createStatement();
                                                        InsertRemoteQueueStmt.executeUpdate(QueryStringInsertRemote);

                                                        // reset string to empty
                                                        QueryStringInsertRemote = "";

                                                        //SelectQueueRS.updateRow( ); 

                                                        }






                                          }// While SelectQueueRS

                                                // Execute Insert on any remainging records
                                                if(QueryStringInsertRemote.length() > 0)
                                                {
                                                    InsertRemoteQueueStmt = conn.createStatement();
                                                    InsertRemoteQueueStmt.executeUpdate(QueryStringInsertRemote); 

                                                QueryStringInsertRemote = "";

                                                // SelectQueueRS.updateRow( );
                                                }



                                                if(CurrentRowCount == 0)
                                                   {
                                                        System.out.println("No results found this loop");
                                                        try 
                                                            {
                                                                Thread.sleep(100);
                                                            }
                                                        catch (InterruptedException interrupted) 
                                                            {
                                                                if (isCancelled()) 
                                                                {
                                                                   System.out.println("QRC Cancelled");
                                                                // break;
                                                                }
                                                            }
                                                    }

                                        
                                                
                                                
                                        StringBuilder sqlSelect = new StringBuilder(1024);
                                        sqlSelect.append("UPDATE test.contactqueue_test ");
                                        sqlSelect.append("SET DTSStatusType_ti = 3,");
                                        sqlSelect.append("Queued_DialerIP_vch = ? ");
                                        sqlSelect.append("WHERE DTSId_int IN ( ");
                                        
                                        
                                        int paramCount = ids.size();
                                        
                                        int j = 0;
                                        
                                        if(paramCount > 0)
                                        {
                                            for(j=0; j < paramCount; j++ )
                                            {
                                                sqlSelect.append( j > 0 ? ", ?" : "?" );
                                                
                                            }// for each param
                                            sqlSelect.append( ")");
                                            
                                            
                                        }// if ids list is not empty
                                        
                                        // make the prepare statement (pst) with the above sql string
                                        PreparedStatement pst =  connRemote.prepareStatement( sqlSelect.toString() );
                                        
                                        //System.out.println("Checking Prepared Statement:"+pst);
                                        
                                        //now set the parameter values in the query
                                        int paramIndex = 1;
                                        
                                        
                                        pst.setString(paramIndex++, ip);
                                        
                                        if( paramCount > 0)
                                        {
                                            for( j = 0; j < paramCount; j++)
                                            {
                                                pst.setLong(paramIndex++,((Integer)ids.get(j)).intValue());
                                                
                                            }// for each param
                                            
                                        }// if ids list is not empty
                                        
                                       pst.executeUpdate(); 
                                       connRemote.commit();
                                       System.out.println("Prepared Statement:"+pst);
                                       System.out.println("Remote Dialer Just Completed is:" +ip);
                                       
                                        
                                        
                          }// END OF For Each Loop

                }// END OF TRY BLOCK
                catch (SQLException ex) 
                {
                    System.out.println(" Outer StackTrace Below");
                    ex.printStackTrace();
                   // }
                   // flag = true;
                    
                
                    
                 try {
                String QueryStringInsertIntoErrorLogs = "";
                Connection connRemoteforCatch = DriverManager.getConnection("jdbc:mysql://10.11.0.130:3306/test",MainUser,MainPass);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                QueryStringInsertIntoErrorLogs = " INSERT INTO simplequeue.errorlogs "
                                                    + " ( "
                                                    + " ErrorNumber_int, "
                                                    + " Created_dt, "
                                                    + " Subject_vch, "
                                                    + " Message_vch, "
                                                    + " TroubleshootingTips_vch, "
                                                    + " CatchType_vch, "
                                                    + " CatchMessage_vch, "
                                                    + " CatchDetail_vch, "
                                                    + " Host_vch, "
                                                    + " Referer_vch, "
                                                    + " UserAgent_vch, "
                                                    + " Path_vch, "
                                                    + " Querystring_vch "
                                                    + " ) "
                                                    + " VALUES "
                                                    + " ( "
                                                    
                                                    + " '2002' , " 
                                                    + " '"+dateFormat.format(date)+" ' , " 
                                                    + " 'SimpleX Error Notification', " 
                                                    + " 'Error Simple X Queue', " 
                                                    + " 'No Troubleshooting Tips Specified', " 
                                                    + " '" + ex.getSQLState() + "' , " 
                                                    + " '" + ex.getMessage() + "' ," 
                                                    + " NULL, " 
                                                    + " 'ebmdevii.messagebroadcast.com'," 
                                                    + " NULL, " 
                                                    + " 'CFSCHEDULE', " 
                                                    + " 'NULL', " 
                                                    + " 'DBSOURCE=BishopDev' " 
                                                    + " ) ";
                System.out.println("the length of the Query String for Error is :" +QueryStringInsertIntoErrorLogs.length());
                System.out.println("Query Output :" +QueryStringInsertIntoErrorLogs);
                InsertRemoteResultsErrorLogStmt = connRemoteforCatch.createStatement();
                InsertRemoteResultsErrorLogStmt.executeUpdate(QueryStringInsertIntoErrorLogs);
                System.out.println("Value of InsertRemoteResultsErrorLogStmt:"+InsertRemoteResultsErrorLogStmt);
                } // ENd of Try block
            catch(SQLException ex1){
                System.out.println("Stacktrace below:");
                ex1.printStackTrace();
            }   
                    
                    
                }
                finally 
                {

                    if (SelectQueueRS != null) 
                    {
                        try 
                            { SelectQueueRS.close();}
                        catch (SQLException sqlEx) 
                            { sqlEx.printStackTrace(); } 

                        SelectQueueRS = null;
                    }

                    if (SelectQueueStmt != null) 
                    {
                         try 
                            { SelectQueueStmt.close(); }
                        catch (SQLException sqlEx) 
                            { sqlEx.printStackTrace(); } 

                        SelectQueueStmt = null;
                    }

                    if (UpdateQueueStmt != null) 
                    {
                        try 
                            { UpdateQueueStmt.close(); }
                        catch (SQLException sqlEx) 
                            {sqlEx.printStackTrace();}

                            UpdateQueueStmt = null;
                    }

                    if (InsertRemoteQueueStmt != null) 
                    {
                        try 
                            { InsertRemoteQueueStmt.close(); }
                        catch (SQLException sqlEx) 
                             { sqlEx.printStackTrace(); }

                    InsertRemoteQueueStmt = null;
                    } 

                    //connRemote.setAutoCommit(true);
                }// ENd Of Finally
 
         
 
        return iterations;
    }
 
 
 
  }



