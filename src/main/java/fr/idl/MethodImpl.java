package main.java.fr.idl;

import java.util.ArrayList;
import java.util.List;

import main.java.fr.idl.CodeSearchEngine.Method;
import main.java.fr.idl.CodeSearchEngine.Type;

public class MethodImpl implements Method {

	private String name;
	private Type type;
	private Type declaringType;
	private ArrayList<Type> parameters;

	public MethodImpl() {
		this.name = "";
		this.type = new TypeImpl();
		this.declaringType = new TypeImpl();
		this.parameters = new ArrayList<Type>();
	}

	/**
	 * @param name
	 * @param type
	 * @param declaringType
	 * @param parameters
	 */
	public MethodImpl(String name, Type type, Type declaringType,
			ArrayList<Type> parameters) {
		this.name = name;
		this.type = type;
		this.declaringType = declaringType;
		this.parameters = parameters;
	}

	@Override
	public Type getDeclaringType() {
		return declaringType;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Type> getParamaters() {
		return parameters;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @param declaringType
	 *            the declaringType to set
	 */
	public void setDeclaringType(Type declaringType) {
		this.declaringType = declaringType;
	}

	/**
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParameters(ArrayList<Type> parameters) {
		this.parameters = parameters;
	}

}
