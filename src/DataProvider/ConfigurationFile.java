package DataProvider;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ConfigurationFile 
{
	
	Properties pro;
	
	public ConfigurationFile(String path)
	{		
		try 
		{
			pro=new Properties();
			pro.load(new FileInputStream(new File(path)));
			
		} 
		catch (Exception e) 
		{
			System.out.println("Unable to locate file "+e.getMessage());
		} 		
	}
	
	public String getMSMQMessagEXEPath()
	{
		return pro.getProperty("MSMQMessagEXEPath");
	}
	
	public String getAgentName()
	{
		return pro.getProperty("AgentName");
	}
	
	public String getDataBaseServer()
	{
		return pro.getProperty("DataBaseServer");
	}
	
	public String getDataBaseName()
	{
		return pro.getProperty("DataBaseName");
	}
	
	public String getDataBaseUserName()
	{
		return pro.getProperty("DataBaseUserName");
	}
	
	public String getDataBasePassword()
	{
		return pro.getProperty("DataBasePassword");
	}
	
	public String getXMLFolderPath()
	{
		return pro.getProperty("XMLFolderPath");
	}
	
	public String getXMLFolderAfterProcessed()
	{
		return pro.getProperty("XMLFolderAfterProcessed");
	}
	
	public String getTestOutputFilePath()
	{
		return pro.getProperty("TestOutputFile");
	}
	public String getAgentWaitTime()
	{
		return pro.getProperty("AgentWaitTime");
	}
	public String getPurgeAgentQueues()
	{
		return pro.getProperty("DoYouWantToPurgePreviousMessagesInTheAgentQueues");
	}
}
