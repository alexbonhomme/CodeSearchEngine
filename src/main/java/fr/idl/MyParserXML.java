package main.java.fr.idl;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class MyParserXML extends DefaultHandler{
	enum StateEnum {
	 UNIT,ENUM,CLASS,NAMECLASS,NAMEENUM,TYPE,UNKNOW,PACKAGE,NAMEPACKAGE,BLOCKCLASS,DECLSTMT,SPECIFER,NAMEFIELD,EXTENDS,NAMEEXTENDS,IMPLEMENTS,NAMEIMPLEMENTS,FUNCTION,TYPEFUNCTION,NAMETYPEFUNCTION
	}
	private StateEnum state;
	private String specifer="";
	
	public MyParserXML(){
	}
	
	public void startElement(String uri,String localName,String raw,Attributes attributes)throws SAXException{
		if(localName.equals("unit")){
			this.state = StateEnum.UNIT;
			if(attributes.getValue(1) != null){
				//affiche le filename
				System.out.print(attributes.getValue(1)+ " ");
			}
		}
		
		switch(localName){
			case "class":
			case "enum":
				this.state = StateEnum.TYPE;
			break;
			case "decl_stmt":
				this.state= StateEnum.DECLSTMT;
			break;		
		}
		
		switch(this.state){
			case CLASS:
				if(localName.equals("name")){
					this.state = StateEnum.NAMECLASS;
				}else if(localName.equals("extends")){
					this.state = StateEnum.EXTENDS;
				}else if(localName.equals("implements")){
					this.state = StateEnum.IMPLEMENTS;
				}else if(localName.equals("block")){
					this.state = StateEnum.BLOCKCLASS;
				}
			break;
			case UNIT:
				if(localName.equals("package")){
					this.state = StateEnum.PACKAGE;
				}
			break;
			case PACKAGE:
				if(localName.equals("name")){
					this.state = StateEnum.NAMEPACKAGE;
				}	
			break;
			case BLOCKCLASS:
				if(localName.equals("decl_stmt")){
					this.state = StateEnum.DECLSTMT;
				}else if(localName.equals("function")){
					this.state = StateEnum.FUNCTION;
				}else if(localName.equals("constructor")||localName.equals("function")){
					this.state = StateEnum.BLOCKCLASS;
				}
			break;
			
			case FUNCTION:
				if(localName.equals("type")){
					this.state = StateEnum.TYPEFUNCTION;
				}
			break;
			
			case TYPEFUNCTION:
				if(localName.equals("name")){
					this.state = StateEnum.NAMETYPEFUNCTION;
				}
			break;
			
			case DECLSTMT:
				if(localName.equals("specifier")){
					this.state = StateEnum.SPECIFER;
				}else if(localName.equals("constructor")||(localName.equals("init"))||localName.equals("function")){
					this.state = StateEnum.BLOCKCLASS;
				}else if(localName.equals("name")||localName.equals("index")){
					if(this.specifer.length() != 0){
						this.state = StateEnum.NAMEFIELD;
					}
				} 
			break;
			case EXTENDS:
				if(localName.equals("name")){
					this.state = StateEnum.NAMEEXTENDS;
				}
			break;
			case IMPLEMENTS:
				if(localName.equals("name")){
					this.state = StateEnum.NAMEIMPLEMENTS;
				}				
			break;
			default:
			break;
		}
	}
	
	public void endElement(String nameSpaceURI, String localName, String rawName) throws SAXException {
		//detecte la fin du package
		if(localName.equals("package")){
			this.state = StateEnum.UNKNOW;
		}	
		if(localName.equals("decl_stmt")){
			this.specifer = "" ;
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {		
		String s = new String(ch,start,length).trim();
		switch(this.state){
			case TYPE:
				if(s.equals("class")||s.equals("interface")||s.equals("enum")){
					//affiche le type
					System.out.print("Type : " + s + " ");
					this.state = StateEnum.CLASS;
				}
			break;
			case NAMECLASS:
				//Affiche le nom de la class
				System.out.println("Name Class : " + s);
				this.state = StateEnum.CLASS;
			break;
			case NAMEPACKAGE:
				//Affiche un bout de package (eg "java" "." "applet" ";" ) 
				System.out.print(s);	
			break;
			case SPECIFER:
				System.out.println("Specifer : " + s);
				this.specifer = s ;
				this.state = StateEnum.DECLSTMT;
			break;
			case NAMEFIELD:
				if(this.specifer.length() != 0){
					System.out.println("Name Field : " + s);
					this.state = StateEnum.DECLSTMT;	
				}
			break;
			case NAMEEXTENDS:
				System.out.println("Extends : " + s);
				this.state = StateEnum.CLASS;
			break;
			case NAMEIMPLEMENTS:
				System.out.println("Implements : " + s);
				this.state = StateEnum.CLASS;
			break;
			default:
			break;
		}
	}
}
