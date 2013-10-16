package main.java.fr.idl;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class MyParserXML extends DefaultHandler{
	 enum StateEnum {
		 UNIT,ENUM,CLASS,NAMECLASS,NAMEENUM,UNKNOW
		 }
	 
	private HashMap<String,String> map ;
	private StateEnum state;
	
	public MyParserXML(){
		this.map = new HashMap<String, String>();
	}
	
	public void startElement(String uri,String localName,String raw,Attributes attributes)throws SAXException{
		// Reset new file
		if(localName.equals("unit"))
			this.state = StateEnum.UNIT;
	
		// State switch
		switch(this.state){
			case UNIT :
				switch(localName){
					case "class":
						this.state = StateEnum.CLASS;
						break;
					case "enum":
						this.state = StateEnum.ENUM;
						break;
					default:
						break;
				}
				break;
			case CLASS :
				
				switch(localName){
					case "name":
						this.state = StateEnum.NAMECLASS;
						break;
					default:
						break;
				}
				break;
			case ENUM :
				switch(localName){
					case "name":
						this.state = StateEnum.NAMEENUM;
						break;
					default:
						break;
				}
				break;
			default:
				break;
		}
	}
	
	public void endElement(String nameSpaceURI, String localName, String rawName) throws SAXException {
		if(localName.equals("unit")){
			this.state = StateEnum.UNIT;
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {		
		String s = new String(ch,start,length).trim();
		switch(this.state){
			case NAMECLASS:
			case NAMEENUM:
				System.out.println(s); // Recup. classes
				this.state = StateEnum.UNKNOW;
				break;
			default:
				break;
		}
	}
	
	public HashMap<String,String> getMap(){
		return this.map;
	}
}
