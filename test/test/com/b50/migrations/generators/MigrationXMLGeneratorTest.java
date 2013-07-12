package test.com.b50.migrations.generators;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.b50.migrations.generators.MigrationXMLGenerator;

public class MigrationXMLGeneratorTest {

	@Test
	public void testGeneration() throws Exception {
		MigrationXMLGenerator generator = new MigrationXMLGenerator("templates", "migrations.xml.ftl");
		String content = generator.generate("some_database", "my.package", 1);
		assertNotNull(content);	    
		assertTrue(content.contains("<integer name=\"database_version\">1</integer>"));
		assertTrue(content.contains("<string name=\"database_name\">some_database</string>"));
		assertTrue(content.contains("<string name=\"package_name\">my.package</string>"));
	}
}
