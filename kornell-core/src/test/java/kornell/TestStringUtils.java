package kornell;

import static kornell.core.util.StringUtils.mkurl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import kornell.core.util.StringUtils;

import org.junit.Test;

public class TestStringUtils {

	@Test
	public void testComposeURL() {
		assertEquals("http://correct/path",mkurl("http://correct","/path") );		
		assertEquals("http://no/slashes/is/ok",mkurl("http://no","slashes","is","ok") );
		assertEquals("http://double/slash/happens",mkurl("http://double/","/slash/","/happens") );		
		assertEquals("http://empty/segments/too",mkurl("http://empty","/segments","","/too") );
		assertEquals("http://slash/segments/also",mkurl("http://slash","/segments","/","/also") );
		assertEquals("/relative/path/welcome",mkurl(null,"/relative","/path","/welcome") );
		assertEquals("/composite/segments/too",mkurl(null,"/composite/segments","/too") );
		assertEquals("/does/not/start/with/double/slash",mkurl("/","/does/not/start/with/double/slash") );
	}
	
	@Test
	public void testOptionalString(){
		assertEquals(StringUtils.opt("uala").getOrNull(),"uala");
		assertNull(StringUtils.opt(null).getOrNull());
		assertEquals(StringUtils.opt("").orElse("uala").getOrNull(),"uala");
		assertNull(StringUtils.opt(null).orElse("").getOrNull());
	}

}
