/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package restapi;

/**
 *
 * @author akhare
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;// Make sure you have javax.servlet-api-3.0.1.JAR in your Apache Tomcat
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.crypto.Mac;
import org.apache.commons.codec.binary.Hex;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

@WebServlet("/CPP_Authenticate")
public class CPP_Authenticate extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CPP_Authenticate() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        response.setContentType("text/html;charset=UTF-8");
        // Allocate a output writer to write the response message into the network socket
        PrintWriter out = response.getWriter();
     
        // Use ResourceBundle to keep localized string in "LocalStrings_xx.properties"
     
        // Write the response message, in an HTML page
        try {
            out.println("<!DOCTYPE html");  // HTML 5
            out.println("<html><head>");
            out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
            
            out.println(AddNewCpp("cpp test - new"));
            
            out.println("<head><title>Test API</title></head>");
            out.println("<body>");
            out.println("<h3>TestAPI</h3>");
            
            // Tabulate the request information 
            out.println("</body></html>");
        } 	
        finally {
            out.close();  // Always close the output writer
        }
    }
    
    public static Object AddNewCpp(String cppDesc) throws IOException {
        String accessKey = "FDE3568B165B1C510591";
        String secretKey = "CAd9fbd60f81e66EAFd79455607e89631eAA41/0";
        String uRLCppList = "http://ebmdevii.messagebroadcast.com/webservice/ebm/cpps/9304052224/contacts/authenticate";
        String method = "POST";
        java.util.Date currentTime = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

        // Give it to me in GMT time.
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateTimeString = sdf.format(currentTime);
                
        String signature = generateSignature(method, secretKey, dateTimeString);
        String authorization = accessKey + ":" + signature;
        Map<String, String> params = new HashMap<String, String>();
        params.put("cppListDesc", cppDesc);
        
        String[] result = sendHttpRequest(uRLCppList, "POST", params, dateTimeString, authorization);
        
        return result;
    }
    
    
    public static String[] sendHttpRequest(String requestUrl, String method, Map<String, String> params, String dateTimeString, String authorization) throws IOException {
        List<String> response = new ArrayList<String>();
            
        StringBuffer requestParams = new StringBuffer();
        
        if (params != null && params.size() > 0) {
            Iterator<String> paramIterator = params.keySet().iterator();
            while (paramIterator.hasNext()) {
                String key = paramIterator.next();
                String value = params.get(key);
                requestParams.append(URLEncoder.encode(key, "UTF-8"));
                requestParams.append("=").append(URLEncoder.encode(value, "UTF-8"));
                requestParams.append("&");
            }
        }
        
        URL url = new URL(requestUrl);
        URLConnection urlConn = url.openConnection();
        urlConn.setRequestProperty("accept", "application/json");
        urlConn.setRequestProperty("datetime", dateTimeString);
        urlConn.setRequestProperty("authorization", authorization);
       
        urlConn.setUseCaches(false);
        
        // the request will return a response
        urlConn.setDoInput(true);
        
        if ("POST".equals(method)) {
            // set request method to POST
            urlConn.setDoOutput(true);
        } else {
            // set request method to GET
            urlConn.setDoOutput(false);
        }
        
        if ("POST".equals(method) && params != null && params.size() > 0) {
            OutputStreamWriter writer = new OutputStreamWriter(urlConn.getOutputStream());
            writer.write(requestParams.toString());
            writer.flush();  
        }
        
        // reads response, store line by line in an array of Strings
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
         
        String line = "";
        while ((line = reader.readLine()) != null) {
            response.add(line);
        }
        
        reader.close();
        
        return (String[]) response.toArray(new String[0]);
    }
     
    public static String generateSignature(String method, String secretKey, String dateTimeString) {		
        
        String cs = String.format("%s\n\n\n%s\n\n\n", method, dateTimeString);
        String signature = createSignature(cs, secretKey);
        
        return  signature;
    }
    
    public static String createSignature(String stringIn, String scretKey) {
    
        String fixedData = stringIn.replace('\n', (char)10);
        // Calculate the hash of the information
        String digest = hmacSha1(scretKey, fixedData);
        
        return digest;
    } 
    
    
    public static String hmacSha1(String key, String value) {
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = key.getBytes("iso-8859-1");           
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(value.getBytes("iso-8859-1"));

            //  Covert array of Hex bytes to a String
            return Base64.encode(rawHmac);
            
            //return new String(hexBytes, "UTF-8");
        } 
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}