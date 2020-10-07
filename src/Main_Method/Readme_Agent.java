package Main_Method;

import java.util.List;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.collections.Lists;


public class Readme_Agent 
{
	public static String infile;

	public static void main(String[] args) 
	{
        // TODO Auto-generated method stub
		 if (0 < args.length) 
		 {
			 //infile = args[0];
			 infile="./Configuration/project.properties";
		 	 System.out.println("Reading Configuration from User specifed File");
		 }
		 else
		 {
			 infile="./Configuration/project.properties";
		     System.out.println("No Property file speciduyed by User,Reading default Project Configuration file");
		 }
        TestListenerAdapter tla = new TestListenerAdapter();
        TestNG testng = new TestNG();
        List<String> suites = Lists.newArrayList();
        suites.add("./testng.xml");//path to xml..
        testng.setTestSuites(suites);
        testng.run();
    }
}
