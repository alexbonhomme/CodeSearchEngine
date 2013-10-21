package main.java.fr.idl;

import main.java.fr.idl.CodeSearchEngine.Location;
import main.java.fr.idl.CodeSearchEngine.Type;
import main.java.fr.idl.CodeSearchEngine.TypeKind;

public class TypeImpl implements Type {

	private String name;
	private String fullyQualifiedPackageName;
	private TypeKind kind;
	private Location declaration;

	// Partial
	public TypeImpl(String name, String fullyQualifiedPackageName,
			TypeKind kind, Location declaration) {
		this.name = name;
		this.fullyQualifiedPackageName = fullyQualifiedPackageName;
		this.kind = kind;
		this.declaration = declaration;
	}

	// Tempo
	public TypeImpl() {

	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getFullyQualifiedPackageName() {
		return this.fullyQualifiedPackageName;
	}

	@Override
	public TypeKind getKind() {
		return this.kind;
	}

	@Override
	public Location getDeclaration() {
		return this.declaration;
	}
	
	public String toString() {
		return "Package\t" + this.getFullyQualifiedPackageName() + "\n"
				+ "Return\t" + this.getName() + "\n" ;
	}

}
