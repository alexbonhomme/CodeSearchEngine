package main.java.fr.idl;

import java.util.Calendar;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class App 
{
    public static void main( String[] args )
    {
    	Long debut = Calendar.getInstance().getTimeInMillis();
    	MyParserXML m = new MyParserXML();
		try {
			XMLReader saxReader = XMLReaderFactory.createXMLReader();
			saxReader.setContentHandler(m);
//			saxReader.parse("/Users/Tony/Documents/srcML/petit.xml");
			saxReader.parse("/Users/Tony/Documents/workspace/CodeSearchEngine/xml/java.xml");
//			saxReader.parse("/Users/Tony/Documents/srcML/testannotation.xml");
//			saxReader.parse("/Users/Tony/Documents/srcML/testEnum.xml");
//			HashMap<String,myClass> list =  m.getList();
//			System.out.println("#####################################################################################");
//			
//			
//			myClass toto = list.get("Checksum");
//			System.out.println(toto.getFileName());
			Long fin = Calendar.getInstance().getTimeInMillis();
			
			System.out.println("\n" + (fin-debut) +" ms");
		} catch (Exception t) {
			t.printStackTrace();
		}
    }
}
