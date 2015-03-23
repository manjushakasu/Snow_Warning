import java.util.ArrayList;
import java.util.Arrays;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Myhandler extends DefaultHandler  {
	boolean Eprecipitation = false;
	boolean Etemperature = false;
	boolean Etime = false;
    double valuet,valuep;
    String from, to, period;
    ArrayList<String> al = new ArrayList<String>();
	ArrayList<Double> dl = new ArrayList<Double>();
	ArrayList<Double> tl = new ArrayList<Double>();
   
	public void startElement(String uri, String localName,String qName, 
                Attributes attributes) throws SAXException {
 
		if (qName.equalsIgnoreCase("precipitation")) {
			 valuep = Double.parseDouble( attributes.getValue("value"));
			 Eprecipitation = true;
		}
		 
		 
		if (qName.equalsIgnoreCase("temperature")) {
			valuet=Double.parseDouble(attributes.getValue("value"));
			Etemperature = true;
		}
 
		if (qName.equalsIgnoreCase("time")) {
			from =attributes.getValue("from");
			to =attributes.getValue("to");
			period =attributes.getValue("period"); 
			Etime = true;
		}
 
	}
 
	public void endElement(String uri, String localName,
		String qName) throws SAXException {
		
		if (qName.equalsIgnoreCase("time")&& period!=null) { 
			String s[]={from,to,period}; 
			al.addAll(Arrays.asList(s));
			dl.addAll(Arrays.asList(valuep));
			tl.addAll(Arrays.asList(valuet));
			Etime = false;
		}
	}
 
	public void characters(char ch[], int start, int length) throws SAXException {
		
		if (Etime && period!=null) {  
			Etime = false;
		}
		if (Eprecipitation) {
			Eprecipitation = false;
		}
		if (Etemperature) {
		    Etemperature = false;
		}
	}
	
}
