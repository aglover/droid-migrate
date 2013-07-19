package test.com.b50.migrations;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b50.migrations.ManifestFileParser;

public class ManifestFileParserTest {

	/**
	 * @throws Exception
	 */
	@Test
	public void testRetreivePackage() throws Exception {
		ManifestFileParser parser = new ManifestFileParser("etc/AndroidManifest.xml");
		String value = parser.getPackageName();
		assertEquals("com.b50.databaseexample", value);
	}

}
