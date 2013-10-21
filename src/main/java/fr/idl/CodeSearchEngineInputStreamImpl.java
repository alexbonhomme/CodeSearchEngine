package main.java.fr.idl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;


import main.java.fr.idl.CodeSearchEngine.TypeKind;
import org.apache.log4j.Logger;


public class CodeSearchEngineInputStreamImpl implements
		CodeSearchEngineInputStream {

	static final Logger log = Logger
			.getLogger(CodeSearchEngineInputStreamImpl.class);

	@Override
	public Type findType(String typeName, InputStream data) {
		String class_name = "";
		String filename = "";
		List<String> package_name = new ArrayList<String>();
		String type = "" ; 
		
		boolean in_class = false;
		boolean in_unit = false;
		boolean in_class_name = false;
		boolean stop = false;
		boolean in_package = false;
		boolean in_package_name = false;
		boolean have_type = false;
		boolean in_block = false;
		
		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		XMLStreamReader xmlsr;
		try {
			xmlsr = xmlif.createXMLStreamReader(data);
			while (xmlsr.hasNext() && (stop == false)) {
				int eventType = xmlsr.next();
				switch (eventType) {
					case XMLEvent.START_ELEMENT :
						if(xmlsr.getLocalName().equals("unit")){
							in_block = false;
							in_unit = true;
							filename = xmlsr.getAttributeValue(1);
						}
						if(xmlsr.getLocalName().equals("class") ){
							in_class = true;
						}
						if(in_class && xmlsr.getLocalName().equals("name")){
							in_class_name = true;
						}
						if(in_unit && xmlsr.getLocalName().equals("package")){
							in_package = true;
						}
						if(in_package && xmlsr.getLocalName().equals("name")){
							in_package_name = true;
						}
						if(xmlsr.getLocalName().equals("block")){
							in_block = true;
						}
	
					break;
					case XMLEvent.CHARACTERS:
						if(in_class){
							if(!xmlsr.getText().equals(typeName)){
								type = xmlsr.getText().trim();
							}
						}
						if(in_class && in_class_name){
							in_class_name = false;
							class_name = xmlsr.getText();
							if(class_name.equals(typeName)&& !in_block){
								stop = true;
							}
						}
						if(in_package && in_package_name){
							in_package_name = false;
							package_name.add(xmlsr.getText());
						}
					break;
					case XMLEvent.END_ELEMENT:
						if(xmlsr.getLocalName().equals("package")){
							in_package = false;
							in_package_name = false;
						}
						if(xmlsr.getLocalName().equals("unit")){
							if(!class_name.equals(typeName)){
								package_name.clear();
							}
						}
					break;
				}
			}
			
			
		}catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(stop){
			String res ="";
			for(String s : package_name){
				res += s+".";
			}
			TypeKind kind = null ; 
			switch(type){
				case "class":
					kind = TypeKind.CLASS;
				break;
				case "interface":
					kind = TypeKind.INTERFACE;
				break;
				case "enum":
					kind = TypeKind.ENUM;
					break;
			}
			return new TypeImpl(class_name,res,kind,new LocationImpl(filename));
		}else{
			return null;
		}
	}

	/**
	 * @author Julien
	 */
	@Override
	public List<Type> findSubTypesOf(String typeName, InputStream data) {
		List<Type> listType = new ArrayList<Type>();

		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		try {
			XMLStreamReader xmlsr = xmlif.createXMLStreamReader(data);
			while (xmlsr.hasNext()) {
				int eventType = xmlsr.next();
				// Analyze each beacon
				switch (eventType) {
					case XMLEvent.START_ELEMENT:
						break;
					case XMLEvent.CHARACTERS:
						break;
					case XMLEvent.END_ELEMENT:
						break;
					default:
						break;
				}
			}

		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
		return listType;
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
						log.debug("Method match - Line : "
								+ xmlsr.getLocation().getLineNumber());

						// method found (function)
						MethodImpl method = new MethodImpl();

						// looking for function.type
						if ((eventType = xmlsr.next()) != XMLEvent.START_ELEMENT) {
							// TODO throw custom exception
							throw new RuntimeException(
									"Malformed file. '<type>' was expected.");
						}

						// type found
						TypeImpl type = new TypeImpl("", "", null, null);

						// looking for function.type.name
						while (xmlsr.hasNext()) {
							eventType = xmlsr.next();

							if (eventType == XMLEvent.START_ELEMENT
									&& xmlsr.getLocalName().equals("name")) {
								break;
							}
						}

						String typeNameBuilder = "";
						while (xmlsr.hasNext()) {
							eventType = xmlsr.next();

							if (eventType == XMLEvent.END_ELEMENT
									&& xmlsr.getLocalName().equals("type")) {
								break;
							}
							if (eventType == XMLEvent.CHARACTERS) {
								typeNameBuilder += xmlsr.getText();
							}
						}

						// Set the return type name of the method
						type.setName(typeNameBuilder.replace("&lt;", "<")
								.replace("&gt;", ">").trim());
						method.setType(type);

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
	 * @author Julien Duribreux 
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
					} else if (xmlsr.getLocalName().equals("function"))
						function = !function;
					else if (xmlsr.getLocalName().equals("type"))
						type = !type;
					else if (xmlsr.getLocalName().equals("name"))
						name = !name; // Method name
					else if (xmlsr.getLocalName().equals("package")) {
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
					else if (xmlsr.getLocalName().equals("type"))
						type = !type;
					else if (xmlsr.getLocalName().equals("name"))
						name = !name;
					else if (xmlsr.getLocalName().equals("package"))
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
