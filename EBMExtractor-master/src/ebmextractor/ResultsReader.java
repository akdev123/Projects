package ebmextractor;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
// String helperes - See http://ostermiller.org/utils/
import static com.Ostermiller.util.StringHelper.escapeSQL;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import static java.lang.Math.abs;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class ResultsReader extends Task<Integer>
{
    // User settings - adjust for best perfromance
    private static int NumberOfResultsThreads = 10;
    private static int NumberOfResultsContactsToProcessPerThread = 1000;
    private static int NumberOfRecordsToInsertAtATime = 100;
    private static String TypeMask = "1,2,3,5";
    private static String RemoteUser = "root";
    private static String RemotePass = "RX1664";
    private static String MainUser = "EBMSQLUser";
    private static String MainPass = "eSU!0126";
    
    // Alllows different systems to use the same RXDialers
    private static int ESID = -15;
    private int CurrModValue;
    private static String IP[];
    // Task local reference to the controller class to call back to when complete
    private EBMExtractorFXMLController QRVC;
    
    public void setQDVC(EBMExtractorFXMLController QRVC)
    {
    this.QRVC = QRVC;
    }
    public EBMExtractorFXMLController getQDVC()
    {
    return QRVC;
    }
    public ResultsReader(int CurrModValue)
    {
    this.CurrModValue = CurrModValue;
    }
    public void act_StartProcessing()
    {
    new Thread(this).start();
    }
    public void act_StopProcessing()
    {
    this.cancel();
    }
    
    
    @Override protected Integer call() throws Exception
    {
    int iterations = 0;
    int i = 0;
    Statement SelectResultsStmt = null;
    Statement UpdateResultsStmt = null;
    Statement InsertRemoteResultsStmt = null;
    Statement DeviceStmt = null;
    Statement InsertRemoteResultsErrorLogStmt = null;
    Statement InsertGlobalResultsErrorLogStmt = null;
    ResultSet SelectResultsRS = null;
    ResultSet SelectErrorLogRS = null;
    ResultSet DeviceRS = null;
    Connection connRemote = null;
    Connection connRemoteforCatch = null;
    
      try
        {
        connRemote = DriverManager.getConnection("jdbc:mysql://10.11.0.130:3306/test",MainUser,MainPass);
        
        if(connRemote != null){
            
            System.out.println("Connection to Mail DB 10.11.0.130 Successfull");
        }
        String maindbsql = null;
        maindbsql = "SELECT IP_vch FROM simplexfulfillment.device_test WHERE Enabled_dt = 1 ";
        
        Map<String,Connection> connections = new HashMap<>();
        DeviceStmt = connRemote.createStatement();
        DeviceRS = DeviceStmt.executeQuery(maindbsql);
        while(DeviceRS.next()){
            
            final String ip_address = DeviceRS.getString("IP_vch");
            System.out.println("Value of IP_vch Field:"+ip_address);
            
            connections.put(ip_address,DriverManager.getConnection("jdbc:mysql://" + ip_address + ":3306/test",RemoteUser,RemotePass));
        
                    if(connections.isEmpty()){
                    System.err.println("Status not set to 1 for any of the databases");
                    }
         }
        int count = 0;
        for(final String ip : connections.keySet()){
            
            count++;
                        System.out.println("for loop count: "+count);
            
            final Connection conn = connections.get(ip);
            
        
        String QueryString = " SELECT "
                            + " RXCallDetailId_int, "
                            + " DTSID_int, "
                            + " CallDetailsStatusId_ti, "
                            + " BatchId_bi, "
                            + " PhoneId_int, "
                            + " TotalObjectTime_int, "
                            + " TotalCallTimeLiveTransfer_int, "
                            + " TotalCallTime_int, "
                            + " TotalAnswerTime_int, "
                            + " UserSpecifiedLineNumber_si, "
                            + " NumberOfHoursToRescheduleRedial_si, "
                            + " TransferStatusId_ti, "
                            + " IsHangUpDetected_ti, "
                            + " IsOptOut_ti, "
                            + " IsMaxRedialsReached_ti, "
                            + " IsRescheduled_ti, "
                            + " CallResult_int, "
                            + " SixSecondBilling_int, "
                            + " SystemBilling_int, "
                            + " RXCDLStartTime_dt, "
                            + " CallStartTime_dt, "
                            + " CallEndTime_dt, "
                            + " CallStartTimeLiveTransfer_dt, "
                            + " CallEndTimeLiveTransfer_dt, "
                            + " CallResultTS_dt, "
                            + " PlayFileStartTime_dt, "
                            + " PlayFileEndTime_dt, "
                            + " HangUpDetectedTS_dt, "
                            + " Created_dt, "
                            + " DialerName_vch, "
                            + " CurrTS_vch, "
                            + " CurrVoice_vch, "
                            + " CurrTSLiveTransfer_vch, "
                            + " CurrVoiceLiveTransfer_vch, "
                            + " CurrCDP_vch, "
                            + " CurrCDPLiveTransfer_vch, "
                            + " DialString_vch, "
                            + " RedialNumber_int, "
                            + " TimeZone_ti, "
                            + " MessageDelivered_si, "
                            + " SingleResponseSurvey_si, "
                            + " ReplayTotalCallTime_int, "
                            + " XMLResultStr_vch, "
                            + " TotalConnectTime_int, "
                            + " IsOptIn_ti, "
                            + " FileSeqNumber_int, "
                            + " UserSpecifiedData_vch, "
                            + " XMLControlString_vch, "
                            + " DTS_UUID_vch, "
                            + " MainMessageLengthSeconds_int "
                            + " FROM "
                            + " calldetail.rxcalldetails_test "
                            + " WHERE "
                            + " CallDetailsStatusId_ti = " + ESID
                            + " AND RXCallDetailId_int MOD " + NumberOfResultsThreads + " = " + CurrModValue
                            + " ORDER BY RXCallDetailId_int ASC "
                            + " LIMIT " + NumberOfResultsContactsToProcessPerThread ;
    SelectResultsStmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
    //SelectResultsStmt = MainConnection[i].createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
    SelectResultsRS = SelectResultsStmt.executeQuery(QueryString);
       
    int CurrentBillingAmount = 0;
    int CurrentRowCount = 0;
    String QueryStringInsertFromRemote = "";
    
    while ( SelectResultsRS.next( ) && !Thread.interrupted())
    {
      CurrentRowCount++;
      if (isCancelled())
      {
       updateMessage("Cancelled");
       break;
      }
    updateProgress(SelectResultsRS.getRow(), NumberOfResultsContactsToProcessPerThread);
    updateMessage("Thread " + CurrModValue + " " + SelectResultsRS.getInt("DTSId_int"));
    CurrentBillingAmount = 0;
    
    QueryStringInsertFromRemote = " INSERT INTO SimpleXResults.ContactResults_test "
                                                + " ( "
                                                + " MasterRXCallDetailId_int, "
                                                + " RXCallDetailId_int, "
                                                + " DTSID_int, "
                                                + " BatchId_bi, "
                                                + " PhoneId_int, "
                                                + " TotalObjectTime_int, "
                                                + " TotalCallTimeLiveTransfer_int, "
                                                + " TotalCallTime_int, "
                                                + " TotalConnectTime_int, "
                                                + " ReplayTotalCallTime_int, "
                                                + " TotalAnswerTime_int, "
                                                + " UserSpecifiedLineNumber_si, "
                                                + " NumberOfHoursToRescheduleRedial_si, "
                                                + " TransferStatusId_ti, "
                                                + " IsHangUpDetected_ti, "
                                                + " IsOptOut_ti, "
                                                + " IsMaxRedialsReached_ti, "
                                                + " IsRescheduled_ti, "
                                                + " CallResult_int, "
                                                + " SixSecondBilling_int, "
                                                + " SystemBilling_int, "
                                                + " RXCDLStartTime_dt, "
                                                + " CallStartTime_dt, "
                                                + " CallEndTime_dt, "
                                                + " CallStartTimeLiveTransfer_dt, "
                                                + " CallEndTimeLiveTransfer_dt, "
                                                + " CallResultTS_dt, "
                                                + " PlayFileStartTime_dt, "
                                                + " PlayFileEndTime_dt, "
                                                + " HangUpDetectedTS_dt, "
                                                + " Created_dt, "
                                                + " DialerName_vch, "
                                                + " DialerIP_vch, "
                                                + " CurrTS_vch, "
                                                + " CurrVoice_vch, "
                                                + " CurrTSLiveTransfer_vch, "
                                                + " CurrVoiceLiveTransfer_vch, "
                                                + " CurrCDP_vch, "
                                                + " CurrCDPLiveTransfer_vch, "
                                                + " ContactString_vch, "
                                                + " RedialNumber_int, "
                                                + " TimeZone_ti, "
                                                + " MessageDelivered_si, "
                                                + " SingleResponseSurvey_si, "
                                                + " XMLResultStr_vch, "
                                                + " IsOptIn_ti, "
                                                + " ActualCost_int, "
                                                + " DTS_UUID_vch, "
                                                + " XMLControlString_vch, "
                                                + " UserSpecifiedData_vch, "
                                                + " FileSeqNumber_int, "
                                                + " MainMessageLengthSeconds_int "
                                                + " ) "
                                    + " VALUES "
                                                + " ( "
                                                + " NULL, "
                                                + " " + SelectResultsRS.getInt("RXCallDetailId_int") + ", " 
                                                + " " + SelectResultsRS.getInt("DTSID_int") + ", "
                                                + " " + SelectResultsRS.getInt("BatchId_bi") + ", " 
                                                + " " + SelectResultsRS.getInt("PhoneId_int") + ", " 
                                                + " " + SelectResultsRS.getInt("TotalObjectTime_int") + ", " 
                                                + " " + SelectResultsRS.getInt("TotalCallTimeLiveTransfer_int") + ", " 
                                                + " " + SelectResultsRS.getInt("TotalCallTime_int") + ", " 
                                                + " " + SelectResultsRS.getInt("TotalConnectTime_int") + ", " 
                                                + " " + SelectResultsRS.getInt("ReplayTotalCallTime_int") + ", " 
                                                + " " + SelectResultsRS.getInt("TotalAnswerTime_int") + ", " 
                                                + " " + SelectResultsRS.getInt("UserSpecifiedLineNumber_si") + ", " 
                                                + " " + SelectResultsRS.getInt("NumberOfHoursToRescheduleRedial_si") + ", " 
                                                + " " + SelectResultsRS.getInt("TransferStatusId_ti") + ", " 
                                                + " " + SelectResultsRS.getInt("IsHangUpDetected_ti") + ", " 
                                                + " " + SelectResultsRS.getInt("IsOptOut_ti") + ", " 
                                                + " " + SelectResultsRS.getInt("IsMaxRedialsReached_ti") + ", " 
                                                + " " + SelectResultsRS.getInt("IsRescheduled_ti") + ", " 
                                                + " " + SelectResultsRS.getInt("CallResult_int") + ", " 
                                                + " " + SelectResultsRS.getInt("SixSecondBilling_int") + ", " 
                                                + " " + SelectResultsRS.getInt("SystemBilling_int") + ", " 
                                                + " '" + SelectResultsRS.getString("RXCDLStartTime_dt") + "', " 
                                                + " '" + SelectResultsRS.getString("CallStartTime_dt") + "', " 
                                                + " '" + SelectResultsRS.getString("CallEndTime_dt") + "', " 
                                                + " '" + SelectResultsRS.getString("CallStartTimeLiveTransfer_dt") + "', " 
                                                + " '" + SelectResultsRS.getString("CallEndTimeLiveTransfer_dt") + "', " 
                                                + " '" + SelectResultsRS.getString("CallResultTS_dt") + "', " 
                                                + " '" + SelectResultsRS.getString("PlayFileStartTime_dt") + "', " 
                                                + " '" + SelectResultsRS.getString("PlayFileEndTime_dt") + "', " 
                                                + " '" + SelectResultsRS.getString("HangUpDetectedTS_dt") + "', " 
                                                + " '" + SelectResultsRS.getString("Created_dt") + "', " 
                                                + " '" + SelectResultsRS.getString("DialerName_vch") + "', " 
                                               + " '" + ip + "', "
                                                + " '" + SelectResultsRS.getString("CurrTS_vch") + "', " 
                                                + " '" + SelectResultsRS.getString("CurrVoice_vch") + "', " 
                                                + " '" + SelectResultsRS.getString("CurrTSLiveTransfer_vch") + "', " 
                                                + " '" + SelectResultsRS.getString("CurrVoiceLiveTransfer_vch") + "', " 
                                                + " '" + SelectResultsRS.getString("CurrCDP_vch") + "', " 
                                                + " '" + SelectResultsRS.getString("CurrCDPLiveTransfer_vch") + "', " 
                                                + " '" + escapeSQL(SelectResultsRS.getString("DialString_vch")) + "', " 
                                                + " " + SelectResultsRS.getInt("RedialNumber_int") + ", " 
                                                + " " + SelectResultsRS.getInt("TimeZone_ti") + ", " 
                                                + " " + SelectResultsRS.getInt("MessageDelivered_si") + ", " 
                                                + " " + SelectResultsRS.getInt("SingleResponseSurvey_si") + ", " 
                                                + " '" + escapeSQL(SelectResultsRS.getString("XMLResultStr_vch")) + "', " 
                                                + " " + SelectResultsRS.getInt("IsOptIn_ti") + ", " 
                                                + " " + CurrentBillingAmount + ", " 
                                                + " '" + SelectResultsRS.getString("DTS_UUID_vch") + "', " 
                                                + " '" + escapeSQL(SelectResultsRS.getString("XMLControlString_vch")) + "', " 
                                                + " '" + escapeSQL(SelectResultsRS.getString("UserSpecifiedData_vch")) + "', " 
                                                + " " + SelectResultsRS.getInt("FileSeqNumber_int") + ", " 
                                                + " " + SelectResultsRS.getInt("MainMessageLengthSeconds_int") + " " 
                                                + " ) ";
     
                    SelectResultsRS.updateInt( "CallDetailsStatusId_ti", abs(ESID) );
                    InsertRemoteResultsStmt = connRemote.createStatement();
                    InsertRemoteResultsStmt.executeUpdate(QueryStringInsertFromRemote);
                    QueryStringInsertFromRemote = "";
                    SelectResultsRS.updateRow( );
                } // END OF while ( SelectResultsRS.next( ) && !Thread.interrupted())
                    if(QueryStringInsertFromRemote.length() > 0)
                    {
                        InsertRemoteResultsStmt = connRemote.createStatement();
                        InsertRemoteResultsStmt.executeUpdate(QueryStringInsertFromRemote);
                        QueryStringInsertFromRemote = "";
                        SelectResultsRS.updateRow( );
                    }
                if(CurrentRowCount == 0)
                {
                System.out.println("No results found in this loop");
                    try
                    {
                    Thread.sleep(100);
                    }
                    catch (InterruptedException interrupted)
                    {
                    if (isCancelled())
                        {
                        System.out.println("QRC Cancelled");
                        }
                    }
                }
    
    
            System.out.println("Dialer Just Completed is:" +ip);
            }// END of FOR Collections
            
        } // END Of try block
        catch (SQLException ex)
        {
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
        ex.printStackTrace();
        
           
            try {
                String QueryStringInsertIntoErrorLogs = "";
                connRemoteforCatch = DriverManager.getConnection("jdbc:mysql://10.11.0.130:3306/test",MainUser,MainPass);
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
                //+ " NULL, " 
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
        }// END Of ORIGINAL CATCH
            catch(Exception e){
            System.out.println("Stacktrace Below:");
            e.printStackTrace();
            }
           
//} // ENd Of While (i <= 5)
return iterations;
}
}
