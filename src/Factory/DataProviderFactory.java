package Factory;

import DataProvider.ConfigurationFile;
import Main_Method.*;

public class DataProviderFactory 
{
	public static ConfigurationFile getProperty()
	{
		
		ConfigurationFile config=new ConfigurationFile(Readme_Agent.infile);
		return config;
	}
}
