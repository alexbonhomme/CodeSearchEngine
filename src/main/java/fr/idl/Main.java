package main.java.fr.idl;

import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.InputSource;

public class Main {

  public static void main(String[] args) throws XPathExpressionException {

    if (args.length != 1) {
      System.err.println("Usage : cse path/to/file.xml");
      System.exit(-1);
    }

    // input source
    InputSource source = new InputSource(args[0]);

  }

}
