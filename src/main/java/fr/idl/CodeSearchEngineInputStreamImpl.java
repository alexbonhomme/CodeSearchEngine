package main.java.fr.idl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public class CodeSearchEngineInputStreamImpl
		implements
			CodeSearchEngineInputStream {

	@Override
	public Type findType(String typeName, InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Type> findSubTypesOf(String typeName, InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Field> findFieldsTypedWith(String typeName, InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> findAllReadAccessesOf(Field field, InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> findAllWriteAccessesOf(Field field, InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @author Alexandre Bonhomme
	 */
	@Override
	public List<Method> findMethodsOf(String typeName, InputStream data) {
		ArrayList<Method> listMethod = new ArrayList<>();

		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		try {
			XMLStreamReader xmlsr = xmlif.createXMLStreamReader(data);

			int eventType;
			while (xmlsr.hasNext()) {
				eventType = xmlsr.next();

				switch (eventType) {
					case XMLEvent.START_ELEMENT :
						if (!xmlsr.getName().equals(typeName)) {
							continue;
						}

						// TODO faire une methode pour ça
						while (xmlsr.hasNext()) {
							eventType = xmlsr.next();

							if (eventType != XMLEvent.START_ELEMENT) {
								continue;
							}

							if (!xmlsr.getName().equals("function")) {
								continue;
							}

							MethodImpl method = new MethodImpl();
							while (xmlsr.hasNext()) {
								eventType = xmlsr.next();

								if (eventType != XMLEvent.START_ELEMENT) {
									continue;
								}
							}
						}

						break;
					default :
						break;
				}
			}

		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}

		return listMethod;
	}

	/**
	 * @author Julien Duribreux Note : Petit soucis avec certains types
	 *         abstraits, close enough
	 */
	@Override
	public List<Method> findMethodsReturning(String typeName, InputStream data) {
		List<Method> listMethod = new ArrayList<Method>();

		boolean function = false;
		boolean type = false;
		boolean name = false;
		boolean typeOK = false;
		boolean nameOK = false;

		String typeValue = ""; // Returning type
		String nameValue = ""; // Method name

		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		try {
			XMLStreamReader xmlsr = xmlif.createXMLStreamReader(data);
			while (xmlsr.hasNext()) {
				int eventType = xmlsr.next();
				// Analyze each beacon
				switch (eventType) {
					case XMLEvent.START_ELEMENT :
						// Trace where we are
						if (xmlsr.getLocalName().equals("function"))
							function = !function;
						if (xmlsr.getLocalName().equals("type"))
							type = !type;
						if (xmlsr.getLocalName().equals("name"))
							name = !name;
						break;
					case XMLEvent.CHARACTERS :
						// Extract the return type
						if (function && type && name && !typeOK) {
							// System.out.println("Return type : "
							// + xmlsr.getText());
							typeValue = xmlsr.getText();
							typeOK = !typeOK;
						}

						// Extract the method name
						if (function && !type && name && typeOK && !nameOK) {
							// System.out.println("Method name : "
							// + xmlsr.getText());
							// System.out.println();
							nameValue = xmlsr.getText();
							nameOK = !nameOK;
						}
						break;
					case XMLEvent.END_ELEMENT :
						// Trace where we are
						if (xmlsr.getLocalName().equals("function")) {
							// Adding Method if returning type is correct
							if (typeValue.equals(typeName)) {
								MethodImpl method = new MethodImpl(nameValue,
										new TypeImpl(typeValue, new String(),
												TypeKind.CLASS,
												new LocationImpl()),
										new TypeImpl(), new ArrayList<Type>());
								listMethod.add(method);
							}
							// Reset not matter what
							function = !function;
							typeOK = !typeOK; // ready for an other function
							nameOK = !nameOK;
							typeValue = "";
							nameValue = "";
						}
						if (xmlsr.getLocalName().equals("type"))
							type = !type;
						if (xmlsr.getLocalName().equals("name"))
							name = !name;
						break;
					default :
						break;
				}
			}

		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}

		return listMethod;
	}
	@Override
	public List<Method> findMethodsTakingAsParameter(String typeName,
			InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Method> findMethodsCalled(String methodName, InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Method> findOverridingMethodsOf(CodeSearchEngine.Method method,
			InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> findNewOf(String className, InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> findCastsTo(String typeName, InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> findInstanceOf(String typeName, InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CodeSearchEngine.Method> findMethodsThrowing(
			String exceptionName, InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> findCatchOf(String exceptionName, InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Type> findClassesAnnotatedWith(String annotationName,
			InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

}
