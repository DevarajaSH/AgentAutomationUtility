package TestCase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Factory.DataProviderFactory;
import Utility.GetDate;

public class TestCase01_Agent_Test_Automation 
{
	  private Connection objConnection = null;
	  private Statement objStatement = null;
	  String baseUrl;
	  public static String 		    filename;
	  ExtentReports 				report1;
	  ExtentTest					logger1;
	  
	  public static String strMSMQMessagEXEPath = null;
	  public static String strAgentName = null;	
	  public static String strDataBaseServer = null;
	  public static String strDataBaseName = null;
	  public static String strDataBaseUserName = null;
	  public static String strDataBasePassword = null;
	  public static String strXMLFolderPath = null;
	  public static String strXMLFolderAfterProcessed = null;
	  public static String strAuditID = null;
	  public static String strTestOutputFilePath = null;
	  public static int nAgentWaitTime = 3000;
	  public static String strPurgeAgentQueues = null;
	  public static boolean bIsProcessed = false;
	  public static int nCountOfInputFiles = 0;
	  public static int nCountProcessedXMLFiles = 0;
	  public static int nCountXMLFilesProcessedSuccessfully = 0;
	  public static int nCountXMLFilesFailedToProcess = 0;
	  public static int nCountXMLFilesUnableToPrcess = 0;
	  public static boolean bIsAuditIDExists;
	  
	@BeforeClass
	public void setup() throws InterruptedException, FileNotFoundException, IOException 
	{
		filename="./Reports/TestResult/"+"Automated_"+DataProviderFactory.getProperty().getAgentName()+"_Report"+GetDate.gettodaysdate()+".html";
		report1= new ExtentReports(filename);
		logger1 = report1.startTest("Automated Test Case For : "+DataProviderFactory.getProperty().getAgentName());			
		logger1.log(LogStatus.INFO, "Started test case !!");
		strMSMQMessagEXEPath = DataProviderFactory.getProperty().getMSMQMessagEXEPath();
   	 	strAgentName = DataProviderFactory.getProperty().getAgentName();
		strDataBaseServer = DataProviderFactory.getProperty().getDataBaseServer();
		strDataBaseName = DataProviderFactory.getProperty().getDataBaseName();
		strDataBaseUserName = DataProviderFactory.getProperty().getDataBaseUserName();
		strDataBasePassword = DataProviderFactory.getProperty().getDataBasePassword();
		strXMLFolderPath = DataProviderFactory.getProperty().getXMLFolderPath ();
		strTestOutputFilePath = DataProviderFactory.getProperty().getTestOutputFilePath ();
		nAgentWaitTime = Integer.parseInt(DataProviderFactory.getProperty().getAgentWaitTime ());
		strXMLFolderAfterProcessed = DataProviderFactory.getProperty().getXMLFolderAfterProcessed ();
	}

	@AfterClass
	public void tearDown() throws Exception 
	{
		report1.flush();
		System.out.println("After class");
		if (bIsProcessed)
			logger1.log(LogStatus.PASS, "Completed processing of all the messages successfully.");
		else
			logger1.log(LogStatus.FAIL, "Failed to completed the processing of all the messages");
		report1.flush();
		report1.endTest(logger1);			
	}
		
	@Test (priority=1)
	 public void SendMessageToQueues () throws SQLException, IOException
	  {
		  logger1.log(LogStatus.INFO, "Send Message To Queues !!");
          try 
          {      
        	 File fXMLFolder = new File(strXMLFolderPath);
        	 File fDestination = new File(strXMLFolderAfterProcessed);
        	 for (File fAllDestinationFiles: fDestination.listFiles()) 
        	 {
        		 System.out.println("Destination file Length:"+fDestination.listFiles().length);
	    			if (!fAllDestinationFiles.isDirectory()) 
	    				fAllDestinationFiles.delete();
        	 }
        	 File[] XMLlistOfFiles = fXMLFolder.listFiles();
        	 nCountOfInputFiles = XMLlistOfFiles.length;
        	 System.out.println("List of Input XML files to process :"+nCountOfInputFiles);
        	 Process nprocess = null;
 	    	 try
 	    	 {
 	    		Thread.sleep(nAgentWaitTime);
 	    		//String strURL = "jdbc:sqlserver://localhost;" +"DatabaseName=SAFE";
 	    		//objConnection = DriverManager.getConnection(strURL, "sa", "database123");
 	    		String strURL = "jdbc:sqlserver://"+strDataBaseServer+";"+"DatabaseName="+strDataBaseName;
 	    		logger1.log(LogStatus.INFO, " Database connection details : "+strURL);
 	    		objConnection = DriverManager.getConnection(strURL, strDataBaseUserName, strDataBasePassword);
 	  		  	objStatement = objConnection.createStatement();
 	    	 }
 	    	 catch (SQLException e)
 	         {
 	        	e.printStackTrace();
 	        	logger1.log(LogStatus.ERROR, "Unable to connect database or Please provide correct database connection details");
   			    report1.flush();
   			    Assert.fail("XML Files are processed and moved to Processed folder but Unable to fetch the response for the XML data. Since database connection is aborted !!");
 	         }
        	 if (XMLlistOfFiles.length >0)
        	 {
	        	 for (File oXMLfile : XMLlistOfFiles) 
	        	 {
	        		 String strAuditXML = oXMLfile.getName().replace(".","-");
	 	    	   	 String[] strFullAuditXML = strAuditXML.split("-");
	 	    	   	 String strOperationAndAuditDetails = strFullAuditXML[0].toString();
	 	    	   	 String[] strXMLOperationDetails = strOperationAndAuditDetails.split("_");
	 	    	     if (strFullAuditXML.length > 0)
	 	    	     {
	 	    	    	   Thread.sleep(nAgentWaitTime);	    	    	     
	 	        	       CheckAuditExists (oXMLfile, objStatement, strXMLOperationDetails[2].toString());
	 	    	     }
	 	    	     else
	 	    	     {
	 	    	    	logger1.log(LogStatus.ERROR, "*************************************************************");
	 	    	    	bIsAuditIDExists = true;
	 	    	     }
	 	    	     if(bIsAuditIDExists)
	 	    	     {
		    	         try 
		    	         {
		        	    	 System.out.println("Absoulate path : " +oXMLfile.getAbsolutePath());
		        	    	 nprocess = new ProcessBuilder(strMSMQMessagEXEPath,"send", "/p", strAgentName,"/f", oXMLfile.getAbsolutePath()).start();
		        	    	 Thread.sleep(nAgentWaitTime);
		        	    	 if (fDestination.isDirectory())
		              	     {
		       	    	    	File strFileName = new File(oXMLfile.getAbsolutePath());
		       	    	    	//FileUtils.moveFileToDirectory(strFileName, fDestination, false);
		       	    	    	logger1.log(LogStatus.PASS, "File name : " +strFileName+ " Moved to Directory : "+fDestination+ " Successfully !! " );
		       	    	    	bIsProcessed = true;
		              	     }
		    	          }
		    	          catch (Exception e)
		    	          {
		    	        	  logger1.log(LogStatus.ERROR, "XML Data Missing in the Process XML Folder");
		    	        	  report1.flush();
		    				  System.out.println("Assert Failure !!");
		    				  Assert.fail("Test Case Failed because Assertion failure !!");
		    	        	  e.printStackTrace();
		    	          }
	        	     }
	 	    	  }
             }
             else
             {
            	System.out.println("Assert Failure: XML Data Missing in the Process XML Folder !!");
        	    logger1.log(LogStatus.ERROR, "XML Data Missing in the Process XML Folder");
  			    report1.flush();
  			    Assert.fail("Test Case Failed due to Search Deatils or Card Details is missing !!");
             }
        	if (nprocess != null)
        		nprocess.destroy();
        	Thread.sleep(nAgentWaitTime);
        	strPurgeAgentQueues = DataProviderFactory.getProperty().getPurgeAgentQueues();
        	if (strPurgeAgentQueues.equalsIgnoreCase("Yes") && bIsAuditIDExists)
        	{
        		logger1.log(LogStatus.INFO, "Purge Agent previous agent queues : "+strPurgeAgentQueues);
        		Process nResponseProcess = new ProcessBuilder(strMSMQMessagEXEPath, "purge", "/p", strAgentName).start();
        		if (nprocess != null)
        		{
        			System.out.println("Purge all the Previous messaging queues for the agent :"+strAgentName);
        			logger1.log(LogStatus.INFO, "Purge all the Previous messaging queues for the agent :"+strAgentName );
        			nResponseProcess.destroy ();
        		}
        	}
        	else if(bIsAuditIDExists)
        		logger1.log(LogStatus.INFO, "Purge Agent previous agent queues : "+strPurgeAgentQueues);
        	if(bIsAuditIDExists)
        	{
		    	 strXMLFolderAfterProcessed = DataProviderFactory.getProperty().getXMLFolderAfterProcessed ();
		    	 File[] XMLFileListsDestination = fDestination.listFiles();
		    	 nCountProcessedXMLFiles = XMLFileListsDestination.length;
		    	 System.out.println("XMLFileListsDestination: nCountProcessedXMLFiles :"+XMLFileListsDestination.length);	    	
		    	 File oWriteFile = new File (strTestOutputFilePath);
	        	 for (File oFile : XMLFileListsDestination) 
	        	 {   
	        		try
	        		{
	        			logger1.log(LogStatus.INFO, " Audit File name : "+oFile.getName());
			            String strAuditXML = oFile.getName().replace(".","-");
		    	   	    String[] strFullAuditXML = strAuditXML.split("-");
		    	   	    String strOperationAndAuditDetails = strFullAuditXML[0].toString();
		    	   	    String[] strXMLOperationDetails = strOperationAndAuditDetails.split("_");
		    	    	if (strFullAuditXML.length > 0)
		    	    	{
		    	    	     Thread.sleep(nAgentWaitTime);	    	    	     
		        	    	 GetAuditResponse (oWriteFile, objStatement, strXMLOperationDetails[2].toString(), strXMLOperationDetails[1].toString());
		        	    	 Thread.sleep(nAgentWaitTime);
		        	    	 bIsProcessed = true;
		    	    	}
	        		}
	        		catch (Exception e) 
	  	          	{
	        			e.printStackTrace();
	    	        	logger1.log(LogStatus.ERROR, "Unable to get Audit Response from database table 'OBJ_AUDIT_RESPONSES' ");
	      			    report1.flush();
	      			    Assert.fail("Unable to get Audit Response from database table 'OBJ_AUDIT_RESPONSES' !!");
	  	          	}
	       	     }
	        	 TestOutputSummary (oWriteFile);
        	 } 
          }
          catch (Exception e) 
          {
        	  e.printStackTrace();
        	  logger1.log(LogStatus.ERROR, "Failure to Send Message To Queues ");
			  report1.flush();
			  Assert.fail("Failure to Send Message To Queues !!");          
		  }  
	  }
	 
	  public void GetAuditResponse (File oWriteFile, Statement objStatement, String strAuditID, String strOperationDetails) throws SQLException, ClassNotFoundException, InterruptedException, IOException 
	  {
		  System.out.println("GetAuditResponse :" + strAuditID);
	      try 
	      {
	    	  if (!strAuditID.isEmpty())
	    	  {
	    		  System.out.println("Query  OBJ_AUDIT_RESPONSES :" + strAuditID);
			      String query = "select top 1 * from OBJ_AUDIT_RESPONSES where AUDITID="+strAuditID+" order by AUDITID desc; ";
			      logger1.log(LogStatus.INFO, "Query to get Audit Response : "+ query);
			      ResultSet objResult = objStatement.executeQuery(query);
			      Thread.sleep(nAgentWaitTime);
		          while (objResult.next()) 
		          {
		        	  String strAuditsID = objResult.getString("AUDITID");
		        	  logger1.log(LogStatus.INFO, "Audit ID value : "+ strAuditsID);
			          String strResponseMessage = objResult.getString("RESPONSEMESSAGE");
			          logger1.log(LogStatus.INFO, " Audit Response Message : "+ strResponseMessage);
			          String strResponse = objResult.getString("RESPONSE");
			          if(strResponse.equalsIgnoreCase("Success"))
			        	  nCountXMLFilesProcessedSuccessfully++;
			          else if (strResponse.equalsIgnoreCase("Failure"))
			        	  nCountXMLFilesFailedToProcess++;
			          else
			        	  nCountXMLFilesUnableToPrcess++;
			          logger1.log(LogStatus.INFO, " Audit Response Status : "+ strResponse);
			          System.out.println("strAuditsID :" + strAuditsID);
			          System.out.println("strResponseMessage : " + strResponseMessage);
			          System.out.println("strResponse : " + strResponse);
			          System.out.println("strOperationDetails : " + strOperationDetails);
			          System.out.println("nCountXMLFilesProcessedSuccessfully :" + nCountXMLFilesProcessedSuccessfully);
			          System.out.println("nCountXMLFilesFailedToProcess : " + nCountXMLFilesFailedToProcess);
			          System.out.println("nCountXMLFilesUnableToPrcess : " + nCountXMLFilesUnableToPrcess);
			          if (oWriteFile.isFile())
			          {
			        	 FileWriter Owriter = new FileWriter(oWriteFile, true);
			        	 Owriter.write("\r\n");
			        	 logger1.log(LogStatus.PASS, " Writing to Out File : "+ oWriteFile.getAbsoluteFile());
			        	 Owriter.write(strOperationDetails+ "|" +strAuditsID +"|"+ strResponse + "|" + strResponseMessage);
			        	 Owriter.close();
			          }
			          else
			        	 logger1.log(LogStatus.FAIL, " Error in writing Response status to OutFile : ");
		          }
			     

	    	  }
	     }
	     catch (SQLException ex) 
	     {
	        System.out.println("test:"+ex);
	        logger1.log(LogStatus.ERROR, "Unable to get the Audit Response. ");
			report1.flush();
	     }
     }
	  
	public void CheckAuditExists (File oWriteFile, Statement objStatement, String strAuditID) throws SQLException, ClassNotFoundException, InterruptedException, IOException 
	{
		  System.out.println("Check Audit ID Exists :" + strAuditID);
		  logger1.log(LogStatus.INFO, "Check Audit ID Exists : "+ strAuditID);
	      try 
	      {
	    	  if (!strAuditID.isEmpty())
	    	  {
	    		  System.out.println("Query  OBJ_AUDIT_RESPONSES :" + strAuditID);
			      String strSelectQuery = "select * from OBJ_AUDIT_RESPONSES where AUDITID="+strAuditID+" order by AUDITID desc; ";
			      logger1.log(LogStatus.INFO, "Query to get Audit Response : "+ strSelectQuery);
			      ResultSet objResult = objStatement.executeQuery(strSelectQuery);
			      Thread.sleep(nAgentWaitTime);
		          if (objResult.next()) 
		          {
		        	  bIsAuditIDExists = false;
		        	  String strAuditsID = objResult.getString("AUDITID");
		        	  logger1.log(LogStatus.INFO, "Audit ID value : "+ strAuditsID);
			          String strResponseMessage = objResult.getString("RESPONSEMESSAGE");
			          logger1.log(LogStatus.INFO, " Audit Response Message : "+ strResponseMessage);
			          String strResponse = objResult.getString("RESPONSE");
			          logger1.log(LogStatus.INFO, " Audit Response Status : "+ strResponse);
		        	  //String strDeleteQuery = "Delete from OBJ_AUDIT_RESPONSES where AUDITID="+strAuditID+";";
		        	  //objStatement.executeUpdate(strDeleteQuery);
		        	  //Thread.sleep(nAgentWaitTime);
		          }
	    	  }
	     }
	     catch (SQLException ex) 
	     {
	        System.out.println("test:"+ex);
	        logger1.log(LogStatus.ERROR, "Unable to get the Audit Response. ");
			report1.flush();
	     }
     }
	
	public boolean TestOutputSummary (File oWriteFile) throws IOException
	{
		boolean bIsOutPutSummary = false;
		try
		{
			logger1.log(LogStatus.INFO, "Test Output Summary : ");
			if (oWriteFile.isFile())
	        {
	        	 FileWriter Owriter = new FileWriter(oWriteFile, true);
	        	 Owriter.write("\r\n");
	        	 Owriter.write("*************************************************************************************************************************************");
	        	 Owriter.write("\r\n");
	        	 Owriter.write(strAgentName + " : Agent Test Summery : ");
	        	 Owriter.write("\r\n");
	        	 Owriter.write("\r\n Total number of input XML file : "+nCountOfInputFiles);
	        	 Owriter.write("\r\n Total number of XML Files which processed with Audit response as Success : "+nCountXMLFilesProcessedSuccessfully);
	        	 Owriter.write("\r\n Total number of Failures in Audit Response : "+nCountXMLFilesFailedToProcess);
	        	 Owriter.write("\r\n Total number of Unknow/Not Processed Audit Response : "+nCountXMLFilesUnableToPrcess);
	        	 logger1.log(LogStatus.INFO, "Test Output Summary For The Agent : "+strAgentName+ " _Number of Input File: "+nCountOfInputFiles+" _Number of Processed Files : "+nCountProcessedXMLFiles+" _ Totatl number of Audit Success : "+nCountXMLFilesProcessedSuccessfully+
	        			 " _ Total number of Audit Failures : "+nCountXMLFilesFailedToProcess+ " _Total number of Unknow Audit Response : "+nCountXMLFilesUnableToPrcess);
	        	 Owriter.write("\r\n");
	        	 Owriter.write("*************************************************************************************************************************************");
	        	 Owriter.close();
	        }
	        else
	            logger1.log(LogStatus.FAIL, " Error in writing Response status to OutFile : ");
		}
		catch(FileNotFoundException FNF)
		{
			FNF.printStackTrace();
			logger1.log(LogStatus.ERROR, "Unable to write test summary details. ");
			report1.flush();
		}
		return bIsOutPutSummary;
	}
}