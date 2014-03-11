package kornell;

import static kornell.core.util.StringUtils.*;
import static org.junit.Assert.assertEquals;

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
	public void testParseInstitutionName(){
		assertEquals("institution",parseInstitutionNameFromHostName("institution.eduvem.com"));
		assertEquals("institution",parseInstitutionNameFromHostName("institution.test.eduvem.com"));
		assertEquals("institution",parseInstitutionNameFromHostName("institution-test.eduvem.com"));
		assertEquals("institution",parseInstitutionNameFromHostName("institution-test.outrodominio.com.br"));
	}
}
