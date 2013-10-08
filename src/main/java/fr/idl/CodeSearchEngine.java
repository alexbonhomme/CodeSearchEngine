package main.java.fr.idl;

import java.util.List;

/**
 * CodeQuery specifies a list of methods for querying source code. 
 * It is designed with Java in mind but should work with many other languages as well.
 * The metamodel of code abstracts over the language (see in TypeKind for example).
 * The queries are inspired from Eclipse Java Search.
 * 
 * A performance contest on a large corpus of Java source code will be organized.
 * 
 * Pull requests on GitHub welcome!
 * 
 * @author Martin Monperrus <martin.monperrus@univ-lille1.fr>
 *
 */
public interface CodeSearchEngine {
  enum TypeKind {
    CLASS, INTERFACE, ENUM, PRIMITIVE, EXCEPTION, ANNOTATION
  }
  
  interface Location {
    String getFilePath();
    String getLineNumber();// optional 
  }
  
  interface Type {
    String getName();
    String getFullyQualifiedPackageName();
    String getKind();
    Location getDeclaration();
  }
  
  interface Member {
    Type getType();
    String getName();
  }
 
  interface Field extends Member {}
 
  interface Method extends Member {
    List<Type> getParamaters();
  }
 
  /** returns the location of class className */
  Type findType(String className);
  
  /** returns all subclasses of class className */
  List<Type> findSubTypesOf(String className);
  
  /** returns all fields typed with className */
  List<Field> findFieldsTypedWith(String className);
 
  /** returns all read accesses of the field given as parameter */
  List<Location> findAllReadAccessesOf(Field field);
 
  /** returns all write accesses of the field given as parameter (this.foo = ... ) */
  List<Location> findAllWriteAccessesOf(Field field);
 
  /** returns all methods of className (does not consider the inherited methods) */
  List<Method> findMethodsOf(String className);
 
  /** returns all methods returning className */
  List<Method> findMethodsReturning(String className);
 
  /** returns all methods using className as parameter */
  List<Method> findMethodsTakingAsParameter(String className);
 
  /** returns all methods called methodName */
  List<Method> findMethodsCalled(String methodName);
 
  /** returns all methods overriding method methodName that is in className */
  List<Method> findOverridingMethodsOf(Method method);
  
  /** returns all locations where there is an instance creation of className: new X() */
  List<Location> findNewOf(String className);
 
  /** returns all locations where there is a cast to className */
  List<Location> findCastsTo(String className);
 
  /** returns all locations where there is an instanceof check to className */
  List<Location> findInstanceOf(String className);
  
  /** returns all methods throwing this exception */
  List<Method> findMethodsThrowing(String exceptionName);
  
  /** returns all locations where there is a cast to className */
  List<Location> findCatchOf(String exceptionName);
 
  /** returns all classes annotated with annotationName */
  List<Type> findClassesAnnotatedWith(String annotationName);
 
}