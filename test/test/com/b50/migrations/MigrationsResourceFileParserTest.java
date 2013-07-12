package test.com.b50.migrations;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.b50.migrations.MigrationsResourceFileParser;

public class MigrationsResourceFileParserTest {
	@Test
	public void testRetreiveSequenceNumber() throws Exception {
		MigrationsResourceFileParser parser = new MigrationsResourceFileParser("etc/migrations.xml");
		int value = parser.getSequence();
		assertEquals(2, value);
	}
	
	@Test
	public void testRetreivePackage() throws Exception {
		MigrationsResourceFileParser parser = new MigrationsResourceFileParser("etc/migrations.xml");
		String value = parser.getPackageName();
		assertEquals("com.b40.example", value);
	}
	
	@Test
	public void testRetreiveDatabaseName() throws Exception {
		MigrationsResourceFileParser parser = new MigrationsResourceFileParser("etc/migrations.xml");
		String value = parser.getDatabaseName();
		assertEquals("things", value);
	}
}
