package com.b50.migrations;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.b50.migrations.generators.DatabaseHelperClassGenerator;
import com.b50.migrations.generators.MigrationClassGenerator;
import com.b50.migrations.generators.MigrationXMLGenerator;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class DroidMigrate {

	public static void main(String[] args) throws Exception {

		OptionParser parser = new OptionParser() {
			{
				acceptsAll(asList("h", "?"), "show help").forHelp();
				acceptsAll(asList("p", "package"), "package name for migration classes (required only for init)")
						.withRequiredArg().describedAs("com.my.package").ofType(String.class);
				acceptsAll(asList("db", "database"), "database name (required only for init)").withRequiredArg()
						.describedAs("database_name").ofType(String.class);
			}
		};

		OptionSet options = parser.parse(args);

		if (options.has("h")) {
			parser.printHelpOn(System.out);
		}

		if (args[0] != null && args[0].equalsIgnoreCase("init")) {

			if (!options.has("p") || !options.has("db")) {
				System.err.println("both package name and database name are required for init");
			} else {
				System.out.println("packaage name is " + options.valueOf("p"));
				System.out.println("db name is " + options.valueOf("db"));

				MigrationXMLGenerator generator = new MigrationXMLGenerator("templates", "migrations.xml.ftl");
				String xmlContent = generator.generate(options.valueOf("db").toString(), options.valueOf("p").toString(), 1);
				writeFile("res" + File.separator + "values" + File.separator + "migrations.xml", xmlContent);

				MigrationClassGenerator clzzGenerator = new MigrationClassGenerator("templates", "Migration.java.ftl");
				String migrationClzzContent = clzzGenerator.generate(options.valueOf("p").toString(), 1);

				String pckName = options.valueOf("p").toString();
				String pckPath = pckName.replace(".", File.separator);
				writeFile("src" + pckPath + File.separator + "DBVersion1.java", migrationClzzContent);

				DatabaseHelperClassGenerator dbGenerator = new DatabaseHelperClassGenerator("templates",
						"DatabaseHelper.java.ftl");
				String dbHelperContent = dbGenerator.generate(options.valueOf("p").toString());
				writeFile("src" + pckPath + File.separator + "DatabaseHelper.java", dbHelperContent);

			}

		} else if (args[0] != null && args[0].equalsIgnoreCase("generate")) {
			// read migrations.xml and get sequence number
			MigrationsResourceFileParser migrationsParser = new MigrationsResourceFileParser("res" + File.separator + "values"
					+ File.separator + "migrations.xml");
			int currentSequence = migrationsParser.getSequence();
			MigrationXMLGenerator generator = new MigrationXMLGenerator("templates", "migrations.xml.ftl");
			String content = generator.generate(migrationsParser.getDatabaseName(), migrationsParser.getPackageName(),
					++currentSequence);

			System.out.println("content is " + content);
			// update it
			// generate new migration
		}
	}

	private static void writeFile(String path, String content) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(path);
		out.println(content);
		out.close();
	}
}
