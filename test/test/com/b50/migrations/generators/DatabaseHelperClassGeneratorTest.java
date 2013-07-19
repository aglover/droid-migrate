package test.com.b50.migrations.generators;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.b50.migrations.generators.DatabaseHelperClassGenerator;

public class DatabaseHelperClassGeneratorTest {

	@Test
	public void testGeneration() throws Exception {
		DatabaseHelperClassGenerator generator = new DatabaseHelperClassGenerator("templates", "DatabaseHelper.java.ftl");
		String content = generator.generate("my.package", "com.core");
		assertNotNull(content);
		assertTrue(content.contains("package my.package;"));
		assertTrue(content.contains("import com.core.R"));
		assertTrue(content.contains("public class DatabaseHelper extends SQLiteOpenHelper {"));
	}

}
