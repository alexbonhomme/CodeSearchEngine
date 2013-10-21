package main.java.fr.idl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import main.java.fr.idl.CodeSearchEngine.Method;

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

			System.out.println("Total file size to read (in bytes) : "
					+ fis.available());

			CodeSearchEngineInputStreamImpl oneShot = new CodeSearchEngineInputStreamImpl();

			// Call methods returning type + print + timer
			double start = System.currentTimeMillis();
			List<Method> l = oneShot.findMethodsReturning("void", fis);
			double end = System.currentTimeMillis();
			
			for (Method m : l) {
				System.out.println(m);
			}
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
