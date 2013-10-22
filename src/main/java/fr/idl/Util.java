package main.java.fr.idl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import main.java.fr.idl.CodeSearchEngine.Method;
import main.java.fr.idl.CodeSearchEngine.Type;

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

	/**
	 * 
	 * @param xmlsr
	 * @param regexEndParsing
	 * @return
	 * @throws XMLStreamException
	 */
	// TODO check deeper of 'regexEndParsing'
	public static String builDOMStructureString(XMLStreamReader xmlsr,
			String regexEndParsing) throws XMLStreamException {
		int deepLevel = 0;
		String builderDOMStructure = "<root>";

		int eventType;
		while (xmlsr.hasNext()) {
			eventType = xmlsr.next();

			if (eventType == XMLEvent.END_ELEMENT
					&& xmlsr.getLocalName().matches(regexEndParsing)
					&& deepLevel == 0) {
				break;
			}

			// build ur DOM string
			switch (eventType) {
			case XMLEvent.START_ELEMENT:
				builderDOMStructure += "<" + xmlsr.getLocalName() + ">";

				if (xmlsr.getLocalName().matches(regexEndParsing)) {
					++deepLevel;
				}
				break;
			case XMLEvent.END_ELEMENT:
				builderDOMStructure += "</" + xmlsr.getLocalName() + ">";

				if (xmlsr.getLocalName().matches(regexEndParsing)) {
					--deepLevel;
				}
				break;

			case XMLEvent.CHARACTERS:
				builderDOMStructure += Util.escapeSpecialsCaract(xmlsr
						.getText());
				break;

			default:

				break;
			}

			if (eventType == XMLEvent.END_ELEMENT
					&& xmlsr.getLocalName().equals("parameter_list")) {
				break;
			}
		}

		return builderDOMStructure + "</root>";

	}

	/**
	 * 
	 * @param xmlsr
	 * @return
	 * @throws XMLStreamException
	 */
	public static String builDOMMethodStructureString(XMLStreamReader xmlsr)
			throws XMLStreamException {
		return builDOMStructureString(xmlsr, "^function[_A-Za-z]*$");
	}

	/**
	 * 
	 * @param methodNode
	 * @return
	 */
	public static Method buildMethodFromDOM(Element methodNode) {
		MethodImpl method = new MethodImpl();

		// Name
		method.setName(methodNode.getChildText("name"));

		// Type
		Element methodTypeName = methodNode.getChild("type").getChild("name");
		method.setType(new TypeImpl(Util.getFullName(methodTypeName), "", null,
				null));

		// Parameters
		ArrayList<Type> parameters = new ArrayList<Type>();
		for (Iterator paramIterator = methodNode.getChild("parameter_list")
				.getChildren("param").iterator(); paramIterator.hasNext();) {
			Element node = (Element) paramIterator.next();

			Element typeNameNode = node.getChild("decl").getChild("type")
					.getChild("name");
			parameters.add(new TypeImpl(Util.getFullName(typeNameNode), "",
					null, new LocationImpl("")));
		}
		method.setParameters(parameters);

		return method;
	}
}
