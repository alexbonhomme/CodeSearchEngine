package main.java.fr.idl;

import java.util.List;

/**
 * CodeQuery specifies a list of methods for querying source code. It is
 * designed with Java in mind but should work with many other languages as well.
 * The metamodel of code abstracts over the language (see in TypeKind for
 * example). The queries are inspired from Eclipse Java Search.
 * 
 * The code query engine can either: - works in one-shot mode (see
 * {@link CodeSearchEngineFile} - loads and pre-computes some data, in this case
 * T may represent your own optimized data structure
 * 
 * @author Martin Monperrus <martin.monperrus@univ-lille1.fr>
 * 
 */
public interface CodeSearchEngine<T> {
	enum TypeKind {
		CLASS, INTERFACE, ENUM, PRIMITIVE, EXCEPTION, ANNOTATION
	}

	interface Location {
		/**
		 * returns a path of the file containing a code element. E.g.
		 * "./foo/Toto.java"
		 */
		String getFilePath();

		int getLineNumber(); // optional
	}

	interface Type {
		String getName();

		String getFullyQualifiedPackageName();

		TypeKind getKind(); // optional

		Location getDeclaration();
	}

	interface Member {
		/** returns the class that defines this method */
		Type getDeclaringType();

		/** returns the class of the field or the returned object */
		Type getType();

		/** returns the name of this member (e.g. the field name) */
		String getName();
	}

	interface Field extends Member {
	}

	interface Method extends Member {
		List<Type> getParamaters();
	}

	/** returns the type (and its location through getLocation) of type typeName */
	Type findType(String typeName, T data);

	/** returns all subtypes of type typeName */
	List<Type> findSubTypesOf(String typeName, T data);

	/** returns all fields typed with typeName */
	List<Field> findFieldsTypedWith(String typeName, T data);

	/** returns all read accesses of the field given as parameter */
	List<Location> findAllReadAccessesOf(Field field, T data);

	/**
	 * returns all write accesses of the field given as parameter (this.foo =
	 * ... )
	 */
	List<Location> findAllWriteAccessesOf(Field field, T data);

	/**
	 * returns all methods of typeName (does not consider the inherited methods)
	 */
	List<Method> findMethodsOf(String typeName, T data);

	/** returns all methods returning typeName */
	List<Method> findMethodsReturning(String typeName, T data);

	/** returns all methods using typeName as parameter */
	List<Method> findMethodsTakingAsParameter(String typeName, T data);

	/** returns all methods called methodName */
	List<Method> findMethodsCalled(String methodName, T data);

	/** returns all methods overriding the given method */
	List<Method> findOverridingMethodsOf(Method method, T data);

	/**
	 * returns all locations where there is an instance creation of typeName:
	 * new X()
	 */
	List<Location> findNewOf(String className, T data);

	/** returns all locations where there is a cast to typeName */
	List<Location> findCastsTo(String typeName, T data);

	/** returns all locations where there is an instanceof check to typeName */
	List<Location> findInstanceOf(String typeName, T data);

	/** returns all methods throwing this exception */
	List<Method> findMethodsThrowing(String exceptionName, T data);

	/** returns all locations where there is a cast to className */
	List<Location> findCatchOf(String exceptionName, T data);

	/** returns all classes annotated with annotationName */
	List<Type> findClassesAnnotatedWith(String annotationName, T data);

}