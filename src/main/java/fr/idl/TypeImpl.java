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

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param fullyQualifiedPackageName
	 *            the fullyQualifiedPackageName to set
	 */
	public void setFullyQualifiedPackageName(String fullyQualifiedPackageName) {
		this.fullyQualifiedPackageName = fullyQualifiedPackageName;
	}

	/**
	 * @param kind
	 *            the kind to set
	 */
	public void setKind(TypeKind kind) {
		this.kind = kind;
	}

	/**
	 * @param declaration
	 *            the declaration to set
	 */
	public void setDeclaration(Location declaration) {
		this.declaration = declaration;
	}

	@Override
	public String toString() {
		return "Location\t" + this.getDeclaration().getFilePath() + "\n"
				+ "Package\t\t" + this.getFullyQualifiedPackageName() + "\n"
				+ "Name\t\t" + this.getName() + "\n";
	}

	@Override
	public boolean equals(Object obj) {
		Type other = (TypeImpl) obj;

		return this.name.equals(other.getName())
				&& this.fullyQualifiedPackageName.equals(other
						.getFullyQualifiedPackageName())
				&& this.declaration.equals(other.getDeclaration())
				&& this.kind.equals(other.getKind());
	}

}
