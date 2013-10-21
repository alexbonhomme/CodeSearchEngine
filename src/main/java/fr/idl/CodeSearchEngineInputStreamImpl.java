package main.java.fr.idl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public class CodeSearchEngineInputStreamImpl implements
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
			boolean classFound = false;

			// looking for class
			while (xmlsr.hasNext()) {
				eventType = xmlsr.next();

				if (eventType != XMLEvent.START_ELEMENT) {
					continue;
				}

				if (!xmlsr.getLocalName().equals("class")) {
					continue;
				}

				// looking for class.name
				while (xmlsr.hasNext()) {
					eventType = xmlsr.next();

					if (eventType == XMLEvent.START_ELEMENT
							&& xmlsr.getLocalName().equals("name")) {
						break;
					}
				}

				classFound = false;
				eventType = xmlsr.next();
				switch (eventType) {
				case XMLEvent.CHARACTERS:
					if (xmlsr.getText().equals(typeName)) {
						classFound = true; // class.name match
					}
					break;

				// looking for class.name.name
				case XMLEvent.START_ELEMENT:
					if (xmlsr.getLocalName().equals("name")) {
						eventType = xmlsr.next();
						if (eventType == XMLEvent.CHARACTERS) {
							if (xmlsr.getText().equals(typeName)) {
								classFound = true;// class.name.name match
							}
						}
					}

				default:
					break;
				}

				// Class found
				if (classFound) {

					// DEBUG
					// System.out.println("Class match - Line : "
					// + xmlsr.getLocation().getLineNumber());

					// looking for methods
					while (xmlsr.hasNext()) {
						eventType = xmlsr.next();

						// class ending
						if (eventType == XMLEvent.END_ELEMENT
								&& xmlsr.getLocalName().equals("class")) {
							break;
						}

						if (eventType != XMLEvent.START_ELEMENT) {
							continue;
						}

						// function -> class
						// function_decl -> interface
						if (!xmlsr.getLocalName().equals("function")
								&& !xmlsr.getLocalName()
										.equals("function_decl")) {
							continue;
						}

						// DEBUG
						// System.out.println("Method match - Line : "
						// + xmlsr.getLocation().getLineNumber());

						// method found
						MethodImpl method = new MethodImpl();

						// looking for Type
						if ((eventType = xmlsr.next()) != XMLEvent.START_ELEMENT) {
							throw new RuntimeException(
									"Malformed file. '<type>' was expected.");
						}

						while (xmlsr.hasNext()) {
							eventType = xmlsr.next();

							// XXX implémnter le type
							// ending type (starting name)
							if (eventType == XMLEvent.END_ELEMENT
									&& xmlsr.getLocalName().equals("type")) {
								// DEBUG
								// System.out.println("</type> match - Line : "
								// + xmlsr.getLocation().getLineNumber());

								break;
							}
						}

						// skip spaces
						while (xmlsr.hasNext()
								&& eventType != XMLEvent.START_ELEMENT) {
							eventType = xmlsr.next();
						}

						if (!xmlsr.getLocalName().equals("name")) {
							// TODO throw custom exception
							throw new RuntimeException(
									"Malformed file. '<name>' was expected.");
						}

						// name found
						eventType = xmlsr.next();
						if (eventType == XMLEvent.CHARACTERS) {
							method.setName(xmlsr.getText());
						}

						listMethod.add(method);
					}
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
		boolean inPackage = false;
		boolean inPackageName = false;
		boolean inUnit = false;

		boolean typeOK = false;
		boolean nameOK = false;

		String pathValue = "";
		String typeValue = ""; // Returning type
		String nameValue = ""; // Method name
		String packageValue = "";

		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		try {
			XMLStreamReader xmlsr = xmlif.createXMLStreamReader(data);
			while (xmlsr.hasNext()) {
				int eventType = xmlsr.next();
				// Analyze each beacon
				switch (eventType) {
				case XMLEvent.START_ELEMENT:
					// Trace where we are
					if (xmlsr.getLocalName().equals("unit")) {
						pathValue = xmlsr.getAttributeValue(1);
						inUnit = !inUnit;
					}
					if (xmlsr.getLocalName().equals("function"))
						function = !function;
					if (xmlsr.getLocalName().equals("type"))
						type = !type;
					if (xmlsr.getLocalName().equals("name"))
						name = !name; // Method name
					if (xmlsr.getLocalName().equals("package")) {
						inPackage = !inPackage;
						packageValue = ""; // Reset
					}
					if (inPackage && xmlsr.getLocalName().equals("name"))
						inPackageName = !inPackageName;
					break;
				case XMLEvent.CHARACTERS:
					// Extract the return type
					if (function && type && name && !typeOK) {
						typeValue = xmlsr.getText();
						typeOK = !typeOK;
					}

					// Extract the method name
					if (function && !type && name && typeOK && !nameOK) {
						nameValue = xmlsr.getText();
						nameOK = !nameOK;
					}

					// Extract package
					if (inPackageName) {
						if (packageValue.equals(""))
							packageValue += xmlsr.getText();
						else
							packageValue += "." + xmlsr.getText();
					}
					break;
				case XMLEvent.END_ELEMENT:
					// Trace where we are
					if (xmlsr.getLocalName().equals("function")) {
						// Adding Method if returning type is correct
						if (typeValue.equals(typeName)) {
							// Get Kind
							TypeKind kindValue = TypeKind.CLASS;
							// Get Location
							Location locationValue = new LocationImpl(pathValue);
							// Create Method
							MethodImpl method = new MethodImpl(nameValue,
									new TypeImpl(typeValue, packageValue,
											kindValue, locationValue),
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
					if (xmlsr.getLocalName().equals("unit"))
						inUnit = !inUnit;
					if (xmlsr.getLocalName().equals("type"))
						type = !type;
					if (xmlsr.getLocalName().equals("name"))
						name = !name;
					if (xmlsr.getLocalName().equals("package"))
						inPackage = !inPackage;

					if (inPackage && xmlsr.getLocalName().equals("name"))
						inPackageName = !inPackageName;
					break;
				default:
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
