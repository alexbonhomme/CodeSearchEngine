package main.java.fr.idl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import main.java.fr.idl.CodeSearchEngine.Method;
import main.java.fr.idl.CodeSearchEngine.Type;
import main.java.fr.idl.CodeSearchEngine.TypeKind;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CodeSearchEngineInputStreamImplTest {

	InputStream data;
	CodeSearchEngineInputStreamImpl cse;

	@Before
	public void testSetup() throws FileNotFoundException {
		// TODO mettre un lien relatif
		data = new FileInputStream(new File("xml/commons-collections.xml"));
		cse = new CodeSearchEngineInputStreamImpl();
	}

	@After
	public void testCleanup() throws IOException {
		data.close();
		cse = null;
	}

	@Test
	public void testFindType() {
		Type t1 = new TypeImpl(
				"FactoryUtils",
				"org.apache.commons.collections.",
				TypeKind.CLASS,
				new LocationImpl(
						"dataset-src/org/apache/commons/collections/FactoryUtils.java"));
		Type t2 = cse.findType("FactoryUtils", data);
		assertEquals(t1, t2);
	}

	@Test
	public void testFindTypeInterface() {
		Type t1 = new TypeImpl("Get", "org.apache.commons.collections.",
				TypeKind.INTERFACE, new LocationImpl(
						"dataset-src/org/apache/commons/collections/Get.java"));
		Type t2 = cse.findType("Get", data);
		assertEquals(t1, t2);
	}

	@Test
	public void testFindSubTypesOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindFieldsTypedWith() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindAllReadAccessesOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindAllWriteAccessesOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindMethodsOfAnInterface() {
		List<Method> methods = cse.findMethodsOf("BidiMap", data);

		assertEquals(4, methods.size());

		assertEquals("put", methods.get(0).getName());
		assertEquals("V", methods.get(0).getType().getName());

		// Parameters
		List<CodeSearchEngine.Type> parameters = methods.get(0).getParamaters();
		assertEquals(2, parameters.size());
		assertEquals(parameters.get(0), new TypeImpl("K", "", null,
				new LocationImpl("")));
		assertEquals(parameters.get(1), new TypeImpl("V", "", null,
				new LocationImpl("")));

		assertEquals("getKey", methods.get(1).getName());
		assertEquals("K", methods.get(1).getType().getName());

		assertEquals("removeValue", methods.get(2).getName());
		assertEquals("K", methods.get(2).getType().getName());

		assertEquals("inverseBidiMap", methods.get(3).getName());
		// assertEquals("BidiMap<V, K>", methods.get(3).getType().getName());
		assertEquals("BidiMap", methods.get(3).getType().getName());
	}

	@Test
	public void testFindMethodsOfAClass() {
		List<Method> methods = cse.findMethodsOf("ArrayStack", data);

		assertEquals(8, methods.size());

		assertEquals("empty", methods.get(0).getName());
		assertEquals("peek", methods.get(1).getName());
		assertEquals("peek", methods.get(2).getName());
		assertEquals("pop", methods.get(3).getName());
		assertEquals("push", methods.get(4).getName());
		assertEquals("search", methods.get(5).getName());
		assertEquals("get", methods.get(6).getName());
		assertEquals("remove", methods.get(7).getName());
	}

	@Test
	public void testFindMethodsReturning() {
		List<Method> methods = cse.findMethodsReturning("int", data);
		assertEquals("search", methods.get(0).getName());
		assertEquals("int", methods.get(0).getType().getName());
		assertEquals("org.apache.commons.collections", methods.get(0).getType()
				.getFullyQualifiedPackageName());
	}

	@Test
	public void testFindMethodsTakingAsParameter() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindMethodsCalled() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindOverridingMethodsOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindNewOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindCastsTo() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindInstanceOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindMethodsThrowing() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindCatchOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindClassesAnnotatedWith() {
		fail("Not yet implemented");
	}

}
