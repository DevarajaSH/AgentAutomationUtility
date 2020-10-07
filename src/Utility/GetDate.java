package Utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GetDate {

	
	public static void getDate(int date) 
	{
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		// get current date time with Date()
		Date date1 = new Date();
		// get calendar instance
		Calendar c = Calendar.getInstance();
		// set time to calendar
		c.setTime(date1);
		// add days to calendar
		c.add(Calendar.DATE, date);
		// print the new date
		String newDate = dateFormat.format(c.getTime());
		Date newDateObj;
		try 
		{
			newDateObj = dateFormat.parse(newDate);
			// System.out.println(newDateObj);
		} catch (ParseException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(newDate);
	}
	
	 public static String gettodaysdate()
	 {
		DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH_mm_ss");
		Date today = new Date();
		today = Calendar.getInstance().getTime(); 
		String reportDate = dateFormat.format(today);
		return reportDate;
	 }

}