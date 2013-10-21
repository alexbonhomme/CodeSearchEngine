package main.java.fr.idl;

import java.util.List;

import org.jdom2.Element;

public class Util {

	/**
	 * 
	 * @param nameNode
	 *            <name>
	 * @return
	 */
	public static String getFullName(Element nameNode) {
		List nameComponents = nameNode.getChildren();

		if (nameComponents.size() == 0) {
			return nameNode.getText();
		} else {
			return nameNode.getChildText("name");
		}

		// String nameBuilder = "";
		// for (Iterator nameCompIterator = nameComponents.iterator();
		// nameCompIterator.hasNext();) {
		// Element nameComponent = (Element) nameCompIterator.next();
		//
		// if (nameComponent.getChildren().size() > 0) {
		// nameBuilder += getFullName(nameComponent);
		// } else {
		// nameBuilder += nameComponent.getText();
		// }
		//
		// System.err.println(nameComponents.toString());
		// }
		//
		// return nameBuilder;
	}

	public static String escapeSpecialsCaract(String text) {
		switch (text) {
		case "<":
			return "&lt;";
		case ">":
			return "&gt;";

		default:
			return text;
		}
	}
}
