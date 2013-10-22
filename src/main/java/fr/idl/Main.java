package main.java.fr.idl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import main.java.fr.idl.CodeSearchEngine.Location;
import main.java.fr.idl.CodeSearchEngine.Method;
import main.java.fr.idl.CodeSearchEngine.Type;

public class Main {

	public static void main(String[] args) throws XPathExpressionException {

		if (args.length != 1) {
			System.err.println("Usage : cse path/to/file.xml");
			System.exit(-1);
		}

		File file = new File(args[0]);
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(file);

			System.out.println("Total file size to read : "
					+ fis.available() + " bytes\n-------------");

			CodeSearchEngineInputStreamImpl oneShot = new CodeSearchEngineInputStreamImpl();

			// Call methods returning type + print + timer
			double start = System.currentTimeMillis();
			List<Type> l = oneShot.findSubTypesOf("RuntimeException", fis);
			double end = System.currentTimeMillis();
			for (Type t : l) {
				System.out.println(t.toString());
			}
			String res ;
			if (l.size() > 1) res = " results" ; else res = " result";
			System.out.println("-------------\n"+l.size() + res + " found.");
			System.out.println("Time : " + (end-start) + "ms");
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
