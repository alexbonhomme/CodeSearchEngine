package main.java.fr.idl;

import java.io.File;

import net.sf.saxon.Configuration;
import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.WhitespaceStrippingPolicy;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmSequenceIterator;

public class MainSaxon {

	public static void main(String[] args) throws SaxonApiException {
		
		if (args.length != 1) {
			System.err.println("Usage : cse path/to/file.xml");
			System.exit(-1);
		}
		
		long startTime, endTime;
		System.out.println("# ----- START ----- #");
		
		// start counter
		startTime = System.currentTimeMillis();
		
//		Configuration config = new Configuration();
//        config.setHostLanguage(Configuration.XML10);
//        config.setLineNumbering(false);
//        config.setValidation(false);
//        config.setTiming(false);
//        
//        Processor proc = new Processor(config);
        
        Processor proc = new Processor(false);
        XQueryCompiler xquery = proc.newXQueryCompiler();

        DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(false);
        
        XdmNode buildedFile = builder.build(new File( args[0] ));

        // 
        XQueryEvaluator selector = xquery.compile("//class[super/extends/name='Exception']/name").load();
        selector.setContextItem(buildedFile);

        int count=0;
        for (XdmItem item : selector) {
			System.out.println(item.getStringValue());
			++count;
		}
        System.out.println("#\n# Number of results : " + count);
        
//        selector.setContextItem(booksDoc);
//        QName titleName = new QName("TITLE");
//        for (XdmItem item : selector) {
//            XdmNode title = getChild((XdmNode) item, titleName);
//            System.out.println(title.getNodeName() +
//                    "(" + title.getLineNumber() + "): " +
//                    title.getStringValue());
//        }
        
        
        // end counter
        endTime = System.currentTimeMillis();
        
        System.out.println("# ----- END ----- #");
        System.out.println("# Time : " + (endTime - startTime) + "ms" ); 
	}
	
    // Helper method to get the first child of an element having a given name.
    // If there is no child with the given name it returns null
    private static XdmNode getChild(XdmNode parent, QName childName) {
        XdmSequenceIterator iter = parent.axisIterator(Axis.CHILD, childName);
        if (iter.hasNext()) {
            return (XdmNode) iter.next();
        } else {
            return null;
        }
    }

}
