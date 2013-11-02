package kornell;

import static kornell.core.util.StringUtils.composeURL;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestStringUtils {

	@Test
	public void testComposeURL() {
		assertEquals("http://some/path",composeURL("http://some/","/path") );
	}
}
