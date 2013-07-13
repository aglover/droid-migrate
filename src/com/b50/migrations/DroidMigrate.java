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

		if (args.length == 0) {
			System.err.println("you must provide some option such as init or generate");
			System.exit(0);
		}

		OptionParser parser = new OptionParser() {
			{
				acceptsAll(asList("h", "?"), "show help").forHelp();
				acceptsAll(asList("p", "package"), "package for migration classes (required only for init)")
						.withRequiredArg().describedAs("com.package").ofType(String.class);
				acceptsAll(asList("d", "database"), "database name (required only for init)").withRequiredArg()
						.describedAs("database_name").ofType(String.class);
			}
		};

		OptionSet options = parser.parse(args);

		if (options.has("h")) {
			parser.printHelpOn(System.out);
		}

		if (args[0] != null && args[0].equalsIgnoreCase("init")) {

			if (!options.has("p") || !options.has("d")) {
				System.err.println("both package name and database name are required for init");
			} else {
				System.out.println("package name is " + options.valueOf("p"));
				System.out.println("db name is " + options.valueOf("d"));

				MigrationXMLGenerator generator = new MigrationXMLGenerator("templates", "migrations.xml.ftl");
				String xmlContent = generator.generate(options.valueOf("d").toString(),
						options.valueOf("p").toString(), 1);
				writeFile("res" + File.separator + "values" + File.separator + "migrations.xml", xmlContent);

				MigrationClassGenerator clzzGenerator = new MigrationClassGenerator("templates", "Migration.java.ftl");
				String migrationClzzContent = clzzGenerator.generate(options.valueOf("p").toString(), 1);

				String pckName = options.valueOf("p").toString();
				String pckPath = pckName.replace(".", File.separator);
				writeFile("src" + File.separator + pckPath + File.separator + "DBVersion1.java", migrationClzzContent);

				DatabaseHelperClassGenerator dbGenerator = new DatabaseHelperClassGenerator("templates",
						"DatabaseHelper.java.ftl");
				String dbHelperContent = dbGenerator.generate(options.valueOf("p").toString());
				writeFile("src" + File.separator + pckPath + File.separator + "DatabaseHelper.java", dbHelperContent);

			}

		} else if (args[0] != null && args[0].equalsIgnoreCase("generate")) {
			// read migrations.xml and get sequence number
			MigrationsResourceFileParser migrationsParser = new MigrationsResourceFileParser("res" + File.separator
					+ "values" + File.separator + "migrations.xml");
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
		File file = new File(path);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		PrintWriter out = new PrintWriter(file);
		out.println(content);
		out.close();
	}
}
