package main.java.fr.idl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import main.java.fr.idl.CodeSearchEngine.Field;
import main.java.fr.idl.CodeSearchEngine.Location;
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
		List<Field> list = cse.findFieldsTypedWith("TreeBidiMap", data);
		assertEquals(8, list.size());
		assertEquals(" private String description", list.get(0).getName());
		assertEquals(" private Node K V [] rootNode", list.get(1).getName());
		assertEquals(" private int nodeCount", list.get(2).getName());
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
		List<Method> methods = cse.findMethodsCalled("transformValue", data);

		assertEquals(2, methods.size());
	}

	@Test
	public void testFindOverridingMethodsOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindNewOf() {
		List<Location> listNewOf = cse.findNewOf("ArrayList", data);

		assertNotEquals(0, listNewOf.size());

		// 1858
		assertEquals(listNewOf.get(0).getFilePath(),
				"dataset-src/org/apache/commons/collections/CollectionUtils.java");

		// 3353
		assertEquals(listNewOf.get(6).getFilePath(),
				"dataset-src/org/apache/commons/collections/EnumerationUtils.java");

		// 3554
		assertEquals(listNewOf.get(7).getFilePath(),
				"dataset-src/org/apache/commons/collections/ExtendedProperties.java");

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
		List<Method> listMethod = cse.findMethodsThrowing("NullPointerException", data);
		assertEquals(120, listMethod.size());
		assertEquals("multiValueMap",listMethod.get(11).getName());
		assertEquals("BagIterator",listMethod.get(102).getName());
	}

	@Test
	public void testFindCatchOf() {
		fail("Not yet implemented");
	}

}
