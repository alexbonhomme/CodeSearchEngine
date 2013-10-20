package main.java.fr.idl;

import java.util.List;

import main.java.fr.idl.CodeSearchEngine.Method;
import main.java.fr.idl.CodeSearchEngine.Type;

public class MethodImpl implements Method {

	private String name;
	private Type type;
	private Type declaringType;
	private List<Type> parameters;

	/**
	 * @param name
	 * @param type
	 * @param declaringType
	 * @param parameters
	 */
	public MethodImpl(String name, Type type, Type declaringType,
			List<Type> parameters) {
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

}
