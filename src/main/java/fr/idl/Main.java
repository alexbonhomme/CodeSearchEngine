package main.java.fr.idl;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Main {

	public static void main(String[] args) throws XPathExpressionException {
		
		if (args.length != 1) {
			System.err.println("Usage : cse path/to/file.xml");
			System.exit(-1);
		}
		
		// input source
		InputSource source = new InputSource(args[0]);
	
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression query = (XPathExpression) xpath.compile("//function_decl/name/text()");
		
		NodeList result = (NodeList) query.evaluate(source, XPathConstants.NODESET);
		
		System.out.println("Resultat: ");
		for (int i = 0; i < result.getLength(); i++) {
			System.out.println(result.item(i));
		}
	}

}
