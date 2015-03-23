import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.pi4j.io.gpio.GpioController;  
import com.pi4j.io.gpio.GpioFactory;  
import com.pi4j.io.gpio.GpioPinDigitalOutput;  
import com.pi4j.io.gpio.PinPullResistance; 
import com.pi4j.io.gpio.PinState;  
import com.pi4j.io.gpio.RaspiPin; 

public class YrTimerModel {
	static Timer timer;
	static final GpioController gpio = GpioFactory.getInstance();  
	static final GpioPinDigitalOutput ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, "MyLED", PinState.LOW);  
	
	  public static void main(String argv[]) {
		  
		  ledPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
		  timer = new Timer();
		  timer.schedule(new TimerTask() {

       public void run() {
    	 
        	try {
        		SAXParserFactory factory = SAXParserFactory.newInstance();
    			Myhandler handler = new Myhandler();
       		    SAXParser saxParser = factory.newSAXParser();
  			    saxParser.parse("http://www.yr.no/sted/Norge/postnummer/7013/varsel.xml", handler);
  			    pinchange(handler.tl.get(0), handler.dl.get(0)); 
  		    } catch (Exception e) {
  		    	gpio.shutdown();
  		        e.printStackTrace();
  		     }
  		
        } 		
  		},0, 1000 * 60 * 360);
	
	}
        static int snow_p = 0;
		static double snow_depth =0.0;
		 
	
	public static void pinchange(Double valuetmp, Double valuep)throws InterruptedException{
		
		String str = String.valueOf(valuetmp<=0.0);
		System.out.println("New Reading starts here:");
		
		switch (str)
		{
		case "true":
			if ((valuep >= 0.5)&((snow_p == 0) ||(snow_p == 1))){
				System.out.println("snow fall. Precipitation:"+valuep);
				ledPin.low();  
		        Thread.sleep(5000);
				snow_p = 1;
				snow_depth = snow_depth + valuep;
			    }
			else if ((valuep < 0.5)&((snow_p == 0) ||(snow_p == 1))){
				System.out.println("no more snow");
				if(snow_p == 0){snow_p = 0; snow_depth = 0;}
				else if(snow_p ==1){ snow_p = 1;}	
				ledPin.low();  
		        Thread.sleep(5000);
			}
			break;
			
		case "false":
			if((snow_p == 1)& ((valuep>=0.5)||(valuep<0.5))){
				System.out.println("Melting snow prediction, indictor on. precipitation: "+valuep);
				ledPin.high();  
			    Thread.sleep(5000);
				snow_p = 0;	
			}
			else if((snow_p == 0)& ((valuep>=0.5)||(valuep<0.5))){
				snow_p = 0;	
				ledPin.low();  
		        Thread.sleep(5000);
			}
			break;
		
		  default:
			  System.out.println("Temperature readings are incorrect");
			  ledPin.low();  
		      Thread.sleep(5000);
		      break;
		}
		System.out.println("<html> <body>");
		System.out.println("<p>Previous snow (0 indicates no, 1 indicates yes):<font color=red>"+snow_p+ "</font> </p>");
		System.out.println("<p>snow_depth:<font color=red>"+snow_depth+ "cm </font></p>");
		System.out.println("<p>Precipitation:<font color=red>"+valuep+ "mm </font> </p>");
		System.out.println("<p>Temperature: <font color=red>"+valuetmp+ "C </font> </p>");
		System.out.println("Led Status on Raspberry Pi: <font color=red>" +ledPin.getState()+"</font>");
		System.out.println("</body></html>");
		timer.purge();
		return;
   }
	
	
}
