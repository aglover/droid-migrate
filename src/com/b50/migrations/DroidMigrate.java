package com.b50.migrations;

import static java.util.Arrays.asList;

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
				String content = generator.generate(options.valueOf("db").toString(), options.valueOf("p").toString(), 1);

//				System.out.println("xml: " + content);
				
				MigrationClassGenerator clzzGenerator = new MigrationClassGenerator("templates", "Migration.java.ftl");
				String content2 = clzzGenerator.generate(options.valueOf("p").toString(), 1);
				
//				System.out.println("migration class: " + content2);
				
				DatabaseHelperClassGenerator dbGenerator = new DatabaseHelperClassGenerator("templates",
						"DatabaseHelper.java.ftl");
				String content3 = dbGenerator.generate(options.valueOf("p").toString());
				
//				System.out.println("helper class: " + content3);
			}

		} else if (args[0] != null && args[0].equalsIgnoreCase("generate")) {
			//read migrations.xml and get sequence number
			//update it
			//generate new migration
		}
	}

}
