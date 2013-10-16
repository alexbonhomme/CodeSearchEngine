package cse;
 
import java.util.List;
 
/**
 * CodeQuery specifies a list of methods for querying source code. 
 * It is designed with Java in mind but should work with many other languages as well.
 * The metamodel of code abstracts over the language (see in TypeKind for example).
 * The queries are inspired from Eclipse Java Search.
 * 
 * The code query engine can either:
 *  - works in one-shot mode (see  {@link CodeSearchEngineFile}
 *  - loads and pre-computes some data, in this case T may represent your own optimized data structure
 * 
 * @author Martin Monperrus <martin.monperrus@univ-lille1.fr>
 *
 */
public interface CodeSearchEngine<T> {
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
    TypeKind getKind();
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
 
  /** returns the type (and its location through getLocation) of class className */
  Type findType(String className, T data);
  
  /** returns all subclasses of class className */
  List<Type> findSubTypesOf(String className, T data);
  
  /** returns all fields typed with className */
  List<Field> findFieldsTypedWith(String className, T data);
 
  /** returns all read accesses of the field given as parameter */
  List<Location> findAllReadAccessesOf(Field field, T data);
 
  /** returns all write accesses of the field given as parameter (this.foo = ... ) */
  List<Location> findAllWriteAccessesOf(Field field, T data);
 
  /** returns all methods of className (does not consider the inherited methods) */
  List<Method> findMethodsOf(String className, T data);
 
  /** returns all methods returning className */
  List<Method> findMethodsReturning(String className, T data);
 
  /** returns all methods using className as parameter */
  List<Method> findMethodsTakingAsParameter(String className, T data);
 
  /** returns all methods called methodName */
  List<Method> findMethodsCalled(String methodName, T data);
 
  /** returns all methods overriding method methodName that is in className */
  List<Method> findOverridingMethodsOf(Method method, T data);
  
  /** returns all locations where there is an instance creation of className: new X() */
  List<Location> findNewOf(String className, T data);
 
  /** returns all locations where there is a cast to className */
  List<Location> findCastsTo(String className, T data);
 
  /** returns all locations where there is an instanceof check to className */
  List<Location> findInstanceOf(String className, T data);
  
  /** returns all methods throwing this exception */
  List<Method> findMethodsThrowing(String exceptionName, T data);
  
  /** returns all locations where there is a cast to className */
  List<Location> findCatchOf(String exceptionName, T data);
 
  /** returns all classes annotated with annotationName */
  List<Type> findClassesAnnotatedWith(String annotationName, T data);
 
}
