package kornell;

import static org.junit.Assert.*;

import org.junit.Test;

import static kornell.core.shared.util.StringUtils.*;

public class TestStringUtils {

	@Test
	public void testComposeURL() {
		assertEquals("http://some/path",composeURL("http://some/","/path") );
	}
}
