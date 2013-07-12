package test.com.b50.migrations.generators;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.b50.migrations.generators.MigrationClassGenerator;

public class MigrationClassGeneratorTest {

	@Test
	public void testGeneration() throws Exception {
		MigrationClassGenerator generator = new MigrationClassGenerator("templates", "Migration.java.ftl");
		String content = generator.generate("my.package", 1);
		assertNotNull(content);
		assertTrue(content.contains("package my.package;"));
		assertTrue(content.contains("public class DBVersion1 extends AbstractMigration {"));
	}

}
