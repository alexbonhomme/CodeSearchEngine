package main.java.fr.idl;

import java.io.InputStream;
import java.util.List;

public class CodeSearchEngineInputStreamImpl implements
		CodeSearchEngineInputStream {

	@Override
	public Type findType(String typeName, InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Type> findSubTypesOf(String typeName, InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Field> findFieldsTypedWith(String typeName, InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> findAllReadAccessesOf(CodeSearchEngine.Field field,
			InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> findAllWriteAccessesOf(CodeSearchEngine.Field field,
			InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Method> findMethodsOf(String typeName, InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CodeSearchEngine.Method> findMethodsReturning(String typeName,
			InputStream data) {
		// TODO Auto-generated method stub
		return null;
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
