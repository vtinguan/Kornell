package kornell;

import static kornell.core.util.StringUtils.composeURL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import kornell.core.util.StringUtils;

import org.junit.Test;

public class TestStringUtils {

	@Test
	public void testComposeURL() {
		assertEquals("http://correct/path",composeURL("http://correct","/path") );		
		assertEquals("http://no/slashes/is/ok",composeURL("http://no","slashes","is","ok") );
		assertEquals("http://double/slash/happens",composeURL("http://double/","/slash/","/happens") );		
		assertEquals("http://empty/segments/too",composeURL("http://empty","/segments","","/too") );
		assertEquals("http://slash/segments/also",composeURL("http://slash","/segments","/","/also") );
		assertEquals("/relative/path/welcome",composeURL(null,"/relative","/path","/welcome") );
		assertEquals("/composite/segments/too",composeURL(null,"/composite/segments","/too") );
	}
	
	@Test
	public void testOptionalString(){
		assertEquals(StringUtils.opt("uala").getOrNull(),"uala");
		assertNull(StringUtils.opt(null).getOrNull());
		assertEquals(StringUtils.opt("").orElse("uala").getOrNull(),"uala");
		assertNull(StringUtils.opt(null).orElse("").getOrNull());
	}
}
