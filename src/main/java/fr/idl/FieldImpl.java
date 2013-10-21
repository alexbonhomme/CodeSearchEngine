package main.java.fr.idl;

import main.java.fr.idl.CodeSearchEngine.Field;
import main.java.fr.idl.CodeSearchEngine.Type;

public class FieldImpl implements Field {

	private String name;
	private Type type;
	
	public FieldImpl(String name,Type type){
		this.name = name;
		this.type = type;
	}
	
	@Override
	public Type getDeclaringType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getType() {
		return this.type;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
