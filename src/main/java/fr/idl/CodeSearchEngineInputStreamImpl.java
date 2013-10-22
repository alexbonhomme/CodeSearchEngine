package main.java.fr.idl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import main.java.fr.idl.CodeSearchEngine.Location;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class CodeSearchEngineInputStreamImpl implements
		CodeSearchEngineInputStream {

	static final Logger log = Logger
			.getLogger(CodeSearchEngineInputStreamImpl.class);

	@Override
	public Type findType(String typeName, InputStream data) {
		String class_name = "";
		String filename = "";
		List<String> package_name = new ArrayList<String>();
		String type = "";

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
				case XMLEvent.START_ELEMENT:
					if (xmlsr.getLocalName().equals("unit")) {
						in_block = false;
						in_unit = true;
						filename = xmlsr.getAttributeValue(1);
					}
					if (xmlsr.getLocalName().equals("class")) {
						in_class = true;
					}
					if (in_class && xmlsr.getLocalName().equals("name")) {
						in_class_name = true;
					}
					if (in_unit && xmlsr.getLocalName().equals("package")) {
						in_package = true;
					}
					if (in_package && xmlsr.getLocalName().equals("name")) {
						in_package_name = true;
					}
					if (xmlsr.getLocalName().equals("block")) {
						in_block = true;
					}

					break;
				case XMLEvent.CHARACTERS:
					if (in_class) {
						if (!xmlsr.getText().equals(typeName)) {
							type = xmlsr.getText().trim();
						}
					}
					if (in_class && in_class_name) {
						in_class_name = false;
						class_name = xmlsr.getText();
						if (class_name.equals(typeName) && !in_block) {
							stop = true;
						}
					}
					if (in_package && in_package_name) {
						in_package_name = false;
						package_name.add(xmlsr.getText());
					}
					break;
				case XMLEvent.END_ELEMENT:
					if (xmlsr.getLocalName().equals("package")) {
						in_package = false;
						in_package_name = false;
					}
					if (xmlsr.getLocalName().equals("unit")) {
						if (!class_name.equals(typeName)) {
							package_name.clear();
						}
					}
					break;
				}
			}

		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
		if (stop) {
			String res = "";
			for (String s : package_name) {
				res += s + ".";
			}
			TypeKind kind = null;
			switch (type) {
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
			return new TypeImpl(class_name, res, kind, new LocationImpl(
					filename));
		} else {
			return new TypeImpl();
		}
	}

	/**
	 * @author Julien
	 */
	@Override
	public List<Type> findSubTypesOf(String typeName, InputStream data) {
		List<Type> listType = new ArrayList<Type>();
		HashMap<String, List<Type>> hashExtends = new HashMap<String, List<Type>>();

		boolean inExtends = false;
		boolean inExtendsName = false;
		String extendsValue = "";

		boolean inClass = false;
		boolean inClassName = false;
		String classValue = "";

		boolean inUnit = false;
		String pathValue = "";

		boolean inPackage = false;
		boolean inPackageName = false;
		String packageValue = "";

		boolean inType = false;

		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		try {
			XMLStreamReader xmlsr = xmlif.createXMLStreamReader(data);
			while (xmlsr.hasNext()) {
				int eventType = xmlsr.next();
				// Analyze each beacon
				switch (eventType) {
				case XMLEvent.START_ELEMENT:
					if (xmlsr.getLocalName().equals("unit")) {
						pathValue = xmlsr.getAttributeValue(1);
						inUnit = true;
					} else if (xmlsr.getLocalName().equals("class")) {
						extendsValue = ""; // reset
						classValue = "";
						inClass = true;
					} else if (xmlsr.getLocalName().equals("extends") && inClass)
						inExtends = true;
					else if (xmlsr.getLocalName().equals("name") && inExtends)
						inExtendsName = true;
					else if (xmlsr.getLocalName().equals("name") && inClass)
						inClassName = true;
					else if (xmlsr.getLocalName().equals("package")) {
						inPackage = true;
						packageValue = ""; // Reset
					} else if (xmlsr.getLocalName().equals("type")) {
						inType = true;
					}
					if (inPackage && xmlsr.getLocalName().equals("name"))
						inPackageName = true;
					break;
				case XMLEvent.CHARACTERS:
					if (inExtendsName) {
						extendsValue = xmlsr.getText();
					}

					if (inClassName && classValue.equals("")) {
						classValue = xmlsr.getText();
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
					if (xmlsr.getLocalName().equals("unit")) {
						inUnit = false;
					} else if (xmlsr.getLocalName().equals("class")) {
						inClass = false;
					} else if (xmlsr.getLocalName().equals("type")) {
						inType = false;
					} else if (xmlsr.getLocalName().equals("extends")
							&& !inType && !typeName.equals("")
							&& !extendsValue.equals("")) {
						inExtends = false;
						// Get Location
						Location locationValue = new LocationImpl(pathValue);
						// Create Method
						TypeImpl type = new TypeImpl(classValue, packageValue,
								TypeKind.CLASS, locationValue);

						// Ajout hashMap
						 if (!hashExtends.containsKey(extendsValue)) {
							 hashExtends.put(extendsValue, new ArrayList<Type>());
						 }
						 hashExtends.get(extendsValue).add(type);

						// Got all the informations we need
						if (extendsValue.equals(typeName)) {
							listType.add(type);
						}
					} else if (xmlsr.getLocalName().equals("name") && inExtends)
						inExtendsName = false;
					else if (xmlsr.getLocalName().equals("name") && inClassName)
						inClassName = false;
					else if (xmlsr.getLocalName().equals("package"))
						inPackage = false;
					if (inPackage && xmlsr.getLocalName().equals("name"))
						inPackageName = false;
					break;
				default:
					break;
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}

		// Analyze if there is some level of Exception
		Queue<Type> tampon = new LinkedList<Type>();
		
	 	// Copy
		if (!hashExtends.containsKey(typeName))
			return listType;
		
		tampon.addAll(hashExtends.get(typeName));

		while (tampon.size() != 0) {
			Type head = tampon.poll();
			if (hashExtends.containsKey(head.getName())) {
				for (Type t : hashExtends.get(head.getName()))
					tampon.offer(t);
				listType.add(head);
			}
		}

		return listType;
	}

	@Override
	public List<Field> findFieldsTypedWith(String typeName, InputStream data) {
		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		XMLStreamReader xmlsr;

		boolean in_class = false;
		boolean in_class_name = false;
		boolean have_name_class = false;
		boolean in_good_class = false;
		boolean in_class_decl_stmt = false;
		boolean exit = false;
		boolean in_class_constructor = false;
		boolean in_class_function = false;
		boolean in_class_decl_stmt_type_name = false;
		boolean in_class_decl_stmt_name = false;
		boolean in_class_decl_stmt_init = false;

		List<Field> listfield = new ArrayList<Field>();
		String tmp_field = "";

		try {
			xmlsr = xmlif.createXMLStreamReader(data);
			int eventType;
			while (xmlsr.hasNext() && (exit == false)) {
				eventType = xmlsr.next();
				switch (eventType) {
				case XMLEvent.START_ELEMENT:
					if (xmlsr.getLocalName().equals("class")) {
						in_class = true;
					}
					if (in_class && xmlsr.getLocalName().equals("name")
							&& !have_name_class) {
						in_class_name = true;
					}

					if (in_good_class
							&& xmlsr.getLocalName().equals("constructor")) {
						in_class_constructor = true;
					}

					if (in_good_class
							&& xmlsr.getLocalName().equals("function")) {
						in_class_function = true;
					}

					if (in_good_class
							&& xmlsr.getLocalName().equals("decl_stmt")) {
						if (!in_class_constructor && !in_class_function) {
							in_class_decl_stmt = true;
						}
					}

					if (in_good_class && in_class_decl_stmt
							&& xmlsr.getLocalName().equals("type")) {
						in_class_decl_stmt_type_name = true;
					}

					if (in_good_class && in_class_decl_stmt
							&& xmlsr.getLocalName().equals("init")) {
						in_class_decl_stmt_init = true;
					}

					if (in_good_class && in_class_decl_stmt
							&& xmlsr.getLocalName().equals("name")
							&& !in_class_decl_stmt_init) {
						in_class_decl_stmt_name = true;
					}

					if (in_good_class && in_class_decl_stmt
							&& xmlsr.getLocalName().equals("index")
							&& !in_class_decl_stmt_init) {
						in_class_decl_stmt_name = true;
					}

					break;
				case XMLEvent.CHARACTERS:
					if (in_class && in_class_name) {
						String text = xmlsr.getText();
						if (text.equals(typeName)) {
							in_good_class = true;
						}
						have_name_class = true;
						in_class_name = false;
					}
					if (in_class_decl_stmt_type_name) {
						tmp_field += " " + xmlsr.getText();
						in_class_decl_stmt_type_name = false;
					}
					if (in_class_decl_stmt_name) {
						tmp_field += " " + xmlsr.getText();
						in_class_decl_stmt_name = false;
					}
					break;
				case XMLEvent.END_ELEMENT:
					if (xmlsr.getLocalName().equals("class")) {
						in_class = false;
						have_name_class = false;
						in_class_name = false;
						exit = in_good_class;
					}
					if (xmlsr.getLocalName().equals("constructor")) {
						in_class_constructor = false;
					}
					if (xmlsr.getLocalName().equals("function")) {
						in_class_function = false;
					}
					if (xmlsr.getLocalName().equals("decl_stmt")
							&& in_good_class) {
						if (!tmp_field.isEmpty()) {
							Field f = new FieldImpl(tmp_field, null);
							listfield.add(f);
							tmp_field = "";
							in_class_decl_stmt = false;
						}
					}
					if (xmlsr.getLocalName().equals("init")) {
						in_class_decl_stmt_init = false;
					}
					break;

				}
			}

		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
		return listfield;
	}

	@Override
	public List<Location> findAllReadAccessesOf(Field field, InputStream data) {
		return Collections.emptyList();
	}

	@Override
	public List<Location> findAllWriteAccessesOf(Field field, InputStream data) {
		return Collections.emptyList();
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
					SAXBuilder builder = new SAXBuilder();

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
						if (!xmlsr.getLocalName().matches(
								"^function[_A-Za-z]*$")) {
							continue;
						}

						// DEBUG
						// log.debug("Method match - Line : "
						// + xmlsr.getLocation().getLineNumber());

						// Build a DOM string
						String builderDOMStructure = Util
								.builDOMMethodStructureString(xmlsr);
						// log.trace(builderDOMStructure);

						// Build DOM Structure from string
						InputStream inputStreamDOM = new ByteArrayInputStream(
								builderDOMStructure.getBytes());

						try {
							Document document = (Document) builder
									.build(inputStreamDOM);
							Element rootNode = document.getRootElement();

							// now we can build ur method object
							listMethod.add(Util.buildMethodFromDOM(rootNode));

						} catch (IOException e) {
							throw new RuntimeException(e);
						} catch (JDOMException e) {
							throw new RuntimeException(e);
						}
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
		List<Method> listMethods = new ArrayList<Method>();

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
							listMethods.add(method);
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

		return listMethods;
	}

	@Override
	public List<Method> findMethodsTakingAsParameter(String typeName, InputStream data) {
		List<Method> listMethod = new ArrayList<Method>();

		boolean function = false;
		boolean type = false;
		boolean parameter_list = false;
		boolean param = false;
		boolean decl = false;
		boolean name = false;
		boolean inPackage = false;
		boolean inPackageName = false;
		boolean inUnit = false;

		boolean typeOK = false;
//		boolean nameOK = false;

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
					if (xmlsr.getLocalName().equals("parameter_list"))
						parameter_list = !parameter_list;
					if (xmlsr.getLocalName().equals("param"))
						param = !param;
					if (xmlsr.getLocalName().equals("decl"))
						decl = !decl;
					if (xmlsr.getLocalName().equals("name"))
						name = true;
					if (xmlsr.getLocalName().equals("package")) {
						inPackage = !inPackage;
						packageValue = ""; // Reset
					}
					if (inPackage && xmlsr.getLocalName().equals("name"))
						inPackageName = !inPackageName;
					break;
				case XMLEvent.CHARACTERS:
					// Extract the return type
					if (function && parameter_list && param && decl && type && name) {
						if (xmlsr.getText().equals(typeName)){
							typeOK = true;
						}
					}

					// Extract the method name
					if (function && !type && name) {
						nameValue = xmlsr.getText();
					}
					
					if (function && type && name && !parameter_list && (xmlsr.getText() != ">")) {
						typeValue = xmlsr.getText();
					}

					// Extract package
					if (inPackageName) {
						if (packageValue.equals(""))
							packageValue += xmlsr.getText();
						else
							packageValue += "." + xmlsr.getText();
					}

					if (typeOK) {
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
						typeOK = false;
					}
					break;
				case XMLEvent.END_ELEMENT:
					// Trace where we are
					if (xmlsr.getLocalName().equals("function"))
						function = !function;
					if (xmlsr.getLocalName().equals("type"))
						type = !type;
					if (xmlsr.getLocalName().equals("parameter_list"))
						parameter_list = !parameter_list;
					if (xmlsr.getLocalName().equals("param"))
						param = !param;
					if (xmlsr.getLocalName().equals("decl"))
						decl = !decl;
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

	/**
	 * @author Alexandre Bonhomme
	 */
	@Override
	public List<Method> findMethodsCalled(String methodName, InputStream data) {
		ArrayList<Method> listMethods = new ArrayList<>();

		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		try {
			XMLStreamReader xmlsr = xmlif.createXMLStreamReader(data);

			SAXBuilder builder = new SAXBuilder();
			int eventType;
			while (xmlsr.hasNext()) {
				eventType = xmlsr.next();

				if (eventType != XMLEvent.START_ELEMENT
						|| !xmlsr.getLocalName().matches("^function[_A-Za-z]*")) {
					continue;
				}

				// some method found

				// Build a DOM string
				String builderDOMStructure = Util
						.builDOMMethodStructureString(xmlsr);
				// log.trace(builderDOMStructure);

				// Build DOM Structure from string
				InputStream inputStreamDOM = new ByteArrayInputStream(
						builderDOMStructure.getBytes());

				try {
					Document document = (Document) builder
							.build(inputStreamDOM);
					Element rootNode = document.getRootElement();

					// this is the right method ?
					String name = rootNode.getChildText("name");
					if (!name.equals(methodName)) {
						builderDOMStructure = null;
						continue;
					}

					// method found
					// log.debug(name);
					listMethods.add(Util.buildMethodFromDOM(rootNode));

				} catch (IOException e) {
					throw new RuntimeException(e);
				} catch (JDOMException e) {
					throw new RuntimeException(e);
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}

		return listMethods;
	}

	@Override
	public List<Method> findOverridingMethodsOf(Method method, InputStream data) {
		return Collections.emptyList();
	}

	/**
	 * @author Alexandre Bonhomme
	 */
	@Override
	public List<Location> findNewOf(String className, InputStream data) {
		ArrayList<Location> listOfNew = new ArrayList<>();

		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		try {
			XMLStreamReader xmlsr = xmlif.createXMLStreamReader(data);

			SAXBuilder builder = new SAXBuilder();
			int eventType;
			while (xmlsr.hasNext()) {
				eventType = xmlsr.next();

				// looking for 'unit'
				if (eventType != XMLEvent.START_ELEMENT) {
					continue;
				}

				if (!xmlsr.getLocalName().equals("unit")) {
					continue;
				}

				// found a .java file
				Location loc = new LocationImpl("");
				for (int i = 0; i < xmlsr.getAttributeCount(); i++) {
					if (xmlsr.getAttributeLocalName(i).equals("filename")) {
						loc = new LocationImpl(xmlsr.getAttributeValue(i));
					}
				}

				// iterate over
				while (xmlsr.hasNext()) {
					eventType = xmlsr.next();

					if (eventType == XMLEvent.END_ELEMENT
							&& xmlsr.getLocalName().equals("unit")) {
						break;
					}

					// looking for all <expr>
					if (eventType != XMLEvent.START_ELEMENT
							|| !xmlsr.getLocalName().equals("expr")) {
						continue;
					}

					// looking for a 'new' just behind
					eventType = xmlsr.next();
					if (eventType != XMLEvent.CHARACTERS
							|| !xmlsr.getText().matches("^new *")) {
						continue;
					}

					eventType = xmlsr.next();

					// Build a DOM string
					String builderDOMStructure = "";
					if (xmlsr.getLocalName().equals("class")) {
						// get <super>
						eventType = xmlsr.next();

						builderDOMStructure = Util.builDOMStructureString(
								xmlsr, xmlsr.getLocalName());
					} else {
						builderDOMStructure = Util.builDOMStructureString(
								xmlsr, xmlsr.getLocalName());
					}

					// log.trace(builderDOMStructure);

					// Build DOM Structure from string
					InputStream inputStreamDOM = new ByteArrayInputStream(
							builderDOMStructure.getBytes());

					try {
						Document document = (Document) builder
								.build(inputStreamDOM);
						Element rootNode = document.getRootElement();

						// log.trace(rootNode.getChildren());
						Element nameNode = rootNode.getChild("name");

						String instancedClassName = "";
						if (nameNode == null) {
							instancedClassName = rootNode.getText();
						} else {
							instancedClassName = Util.getFullName(nameNode);
						}

						if (instancedClassName.equals(className)) {
							listOfNew.add(loc);
						}

					} catch (IOException e) {
						throw new RuntimeException(e);
					} catch (JDOMException e) {
						throw new RuntimeException(e);
					}

				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}

		// log.debug(listOfNew.size());

		return listOfNew;
	}

	@Override
	public List<Location> findCastsTo(String typeName, InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> findInstanceOf(String typeName, InputStream data) {
		return Collections.emptyList();
	}

	@Override
	public List<CodeSearchEngine.Method> findMethodsThrowing(
			String exceptionName, InputStream data) {
		XMLInputFactory xmlif = XMLInputFactory.newInstance();
        XMLStreamReader xmlsr;
        boolean in_function_throws = false ;
        boolean in_function_throws_name = false;
        boolean in_function = false;
        boolean in_function_type = false;
        boolean in_function_name = false;
        String current_excep = "";
        String method_name = "" ;
        List<Method> listMethod = new ArrayList<Method>();
        
        try {
                xmlsr = xmlif.createXMLStreamReader(data);
                while (xmlsr.hasNext()) {
                        int eventType = xmlsr.next();
                        switch (eventType) {
                                case XMLEvent.START_ELEMENT :
                                        if(xmlsr.getLocalName().equals("throws")){
                                                in_function_throws = true;
                                        }
                                        if(in_function_throws && xmlsr.getLocalName().equals("name")){
                                                in_function_throws_name = true;
                                        }
                                        
                                        if(xmlsr.getLocalName().equals("function") || xmlsr.getLocalName().equals("constructor")){
                                                in_function = true;
                                        }
                                        if(in_function && xmlsr.getLocalName().equals("type")){
                                                in_function_type = true;
                                        }
                                        if(in_function && !in_function_type && xmlsr.getLocalName().equals("name")){
                                                in_function_name = true;
                                        }
                                        
                                break;
                                case XMLEvent.CHARACTERS :
                                        if(in_function && !in_function_type && in_function_name){
                                                in_function = false;
                                                in_function_name = false;
                                                method_name = xmlsr.getText();
                                                //System.out.println(method_name);                                                
                                        }
                                        if(in_function_throws && in_function_throws_name){
                                                current_excep = xmlsr.getText() ;
                                                in_function_throws = false;
                                                in_function_throws_name = false;
                                        }
                                break;
                                case XMLEvent.END_ELEMENT:
                                        if((xmlsr.getLocalName().equals("function"))||(xmlsr.getLocalName().equals("constructor"))){
                                                in_function_throws = false;
                                                in_function_throws_name = false;
                                                if(current_excep.equals(exceptionName)){
                                                        Method m = new MethodImpl(method_name,null,null,null);
                                                        listMethod.add(m);
                                                        current_excep = "";
                                                        //System.out.println(method_name + " " + exceptionName);
                                                }
                                        }
                                        if((xmlsr.getLocalName().equals("type")) && in_function ){
                                                in_function_type = false;
                                        }
                                break;
                        }
                }
        } catch (XMLStreamException e) {
                System.err.println(e.getMessage());
        }
        return listMethod;
	}

	@Override
	public List<Location> findCatchOf(String exceptionName, InputStream data) {
		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		XMLStreamReader xmlsr;
		String path = "" ;
		List<Location> listLoc = new ArrayList<Location>();
		boolean in_class = false;
		boolean in_class_catch = false;
		boolean in_class_catch_name = false;
		boolean detect_exception = false;

		try {
			xmlsr = xmlif.createXMLStreamReader(data);
			while (xmlsr.hasNext()) {
				int eventType = xmlsr.next();
				switch (eventType) {
				case XMLEvent.START_ELEMENT:
					if (xmlsr.getLocalName().equals("unit")) {
						path = xmlsr.getAttributeValue(1);
					}
					if (xmlsr.getLocalName().equals("class")) {
						in_class = true;
					}
					if (in_class && xmlsr.getLocalName().equals("catch")) {
						in_class_catch = true;
					}
					if (in_class && in_class_catch
							&& xmlsr.getLocalName().equals("name")) {
						in_class_catch_name = true;
					}
					break;

				case XMLEvent.CHARACTERS:
					if (in_class && in_class_catch && in_class_catch_name) {
						if (xmlsr.getText().equals(exceptionName)) {
							detect_exception = true;
						}
						in_class_catch_name = false;
						in_class_catch = false;
					}
					break;

				case XMLEvent.END_ELEMENT:
					if (xmlsr.getLocalName().equals("catch")
							&& detect_exception) {
						listLoc.add(new LocationImpl(path));
						in_class = false;
						in_class_catch = false;
						in_class_catch_name = false;
						detect_exception = false;
						path = "";
					}
					break;
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
		return listLoc;
	}

	@Override
	public List<Type> findClassesAnnotatedWith(String annotationName,
			InputStream data) {
		return Collections.emptyList();
	}

}
