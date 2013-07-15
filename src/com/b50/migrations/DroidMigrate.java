package com.b50.migrations;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

				handleXMLResourceFile(options.valueOf("d").toString(), options.valueOf("p").toString(), 1);

				handleMigrationClassFile(options.valueOf("p").toString(), 1);

				String pckName = options.valueOf("p").toString();
				String pckPath = pckName.replace(".", File.separator);
				DatabaseHelperClassGenerator dbGenerator = new DatabaseHelperClassGenerator("templates",
						"DatabaseHelper.java.ftl");
				String dbHelperContent = dbGenerator.generate(options.valueOf("p").toString());
				writeFile("src" + File.separator + pckPath + File.separator + "DatabaseHelper.java", dbHelperContent);

			}

		} else if (args[0] != null && args[0].equalsIgnoreCase("generate")) {
			if (args[1] != null && args[1].equalsIgnoreCase("up")) {
				MigrationsResourceFileParser migrationsParser = new MigrationsResourceFileParser("res" + File.separator
						+ "values" + File.separator + "migrations.xml");
				int nextSequence = migrationsParser.getSequence() + 1;

				handleXMLResourceFile(migrationsParser.getDatabaseName(), migrationsParser.getPackageName(),
						nextSequence);
				
				handleMigrationClassFile(migrationsParser.getPackageName(), nextSequence);
			} else if (args[1] != null && args[1].equalsIgnoreCase("down")) {
				MigrationsResourceFileParser migrationsParser = new MigrationsResourceFileParser("res" + File.separator
						+ "values" + File.separator + "migrations.xml");
				int prevSequence = 1;
				if (migrationsParser.getSequence() > 1) {
					prevSequence = migrationsParser.getSequence() - 1;
				}

				handleXMLResourceFile(migrationsParser.getDatabaseName(), migrationsParser.getPackageName(),
						prevSequence);

				handleMigrationClassFile(migrationsParser.getPackageName(), prevSequence);
			} else {
				System.err.println("Please indicate up or down for generate");
			}
		}
	}

	private static void handleMigrationClassFile(String packageName, int sequence) throws IOException {
		MigrationClassGenerator clzzGenerator = new MigrationClassGenerator("templates", "Migration.java.ftl");
		String migrationClzzContent = clzzGenerator.generate(packageName, sequence);
		String pckPath = packageName.replace(".", File.separator);
		writeFile("src" + File.separator + pckPath + File.separator + "DBVersion" + sequence + ".java",
				migrationClzzContent);
	}

	private static void handleXMLResourceFile(String dbName, String packageName, int sequence) throws IOException {
		MigrationXMLGenerator generator = new MigrationXMLGenerator("templates", "migrations.xml.ftl");
		String content = generator.generate(dbName, packageName, sequence);
		writeFile("res" + File.separator + "values" + File.separator + "migrations.xml", content);
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
