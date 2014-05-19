package kornell.scorm.client.scorm12;

import java.util.Map;

import junit.framework.TestCase;

public class CMITreeTest extends TestCase {
	public void testEmpty() {
		CMITree tree = CMITree.empty();
		assertEquals(null, tree.getValue("anything"));
	}

	public void testSetAndGet() {
		CMITree tree = CMITree.empty();
		tree.setValue("cmi.some.thing", "somevalue");
		assertEquals("somevalue", tree.getValue("cmi.some.thing"));
	}

	public void testCollect() {
		CMITree tree = CMITree.empty();
		tree.setValue("cmi.some.thing", "somevalue");
		Map<String,String> values = CMITree.collectValues(tree);
		assertTrue(values.containsKey("cmi.some.thing"));
	}

}
