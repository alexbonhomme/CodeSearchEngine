package main.java.fr.idl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import main.java.fr.idl.CodeSearchEngine.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CodeSearchEngineInputStreamImplTest {

	InputStream data;
	CodeSearchEngineInputStreamImpl cse;

	@Before
	public void testSetup() throws FileNotFoundException {
		// TODO mettre un lien relatif
		data = new FileInputStream(
				new File(
						"/home/alex/Documents/Cours/M2/IDL/CodeSearchEngine/xml/commons-collections.xml"));

		cse = new CodeSearchEngineInputStreamImpl();
	}

	@After
	public void testCleanup() throws IOException {
		data.close();
		cse = null;
	}

	@Test
	public void testFindMethodsOfAnInterface() {
		List<Method> methods = cse.findMethodsOf("BidiMap", data);

		assertEquals(4, methods.size());

		assertEquals("put", methods.get(0).getName());
		assertEquals("getKey", methods.get(1).getName());
		assertEquals("removeValue", methods.get(2).getName());
		assertEquals("inverseBidiMap", methods.get(3).getName());
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

}
