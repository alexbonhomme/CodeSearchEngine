package main.java.fr.idl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import main.java.fr.idl.CodeSearchEngine.Method;
import main.java.fr.idl.CodeSearchEngine.Type;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jdom2.Element;

public class Util {

	/**
	 * 
	 * @param nameNode
	 *            <name>
	 * @return
	 */
	public static String getFullName(Element nameNode) {
		List<Element> nameComponents = nameNode.getChildren();

		if (nameComponents.size() == 0) {
			return nameNode.getText();
		} else {
			return nameNode.getChildText("name");
		}
	}

	/**
	 * 
	 * @param xmlsr
	 * @param regexEndParsing
	 * @return
	 * @throws XMLStreamException
	 */
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

				++deepLevel;
				break;
			case XMLEvent.END_ELEMENT:
				builderDOMStructure += "</" + xmlsr.getLocalName() + ">";

				--deepLevel;
				break;

			case XMLEvent.CHARACTERS:
				builderDOMStructure += StringEscapeUtils.escapeXml(xmlsr
						.getText());
				break;

			default:

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
		int deepLevel = 0;
		String builderDOMStructure = "<root>";

		int eventType;
		while (xmlsr.hasNext()) {
			eventType = xmlsr.next();

			if (eventType == XMLEvent.END_ELEMENT
					&& xmlsr.getLocalName().matches("^function[_A-Za-z]*$")
					&& deepLevel == 0) {
				break;
			}

			// build ur DOM string
			switch (eventType) {
			case XMLEvent.START_ELEMENT:
				builderDOMStructure += "<" + xmlsr.getLocalName() + ">";

				++deepLevel;
				break;
			case XMLEvent.END_ELEMENT:
				builderDOMStructure += "</" + xmlsr.getLocalName() + ">";

				--deepLevel;
				break;

			case XMLEvent.CHARACTERS:
				builderDOMStructure += StringEscapeUtils.escapeXml(xmlsr
						.getText());
				break;

			default:

				break;
			}

			// just for method
			if (eventType == XMLEvent.END_ELEMENT
					&& xmlsr.getLocalName().equals("parameter_list")) {
				break;
			}
		}

		return builderDOMStructure + "</root>";
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
		for (Iterator<Element> paramIterator = methodNode
				.getChild("parameter_list").getChildren("param").iterator(); paramIterator
				.hasNext();) {
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
