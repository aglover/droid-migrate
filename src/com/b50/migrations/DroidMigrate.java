package com.b50.migrations;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.b50.migrations.generators.DatabaseHelperClassGenerator;
import com.b50.migrations.generators.MigrationClassGenerator;
import com.b50.migrations.generators.MigrationXMLGenerator;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

//TODO: refactor main method into testable methods
public class DroidMigrate {

	public static void main(String[] args) throws Exception {

		if (args.length == 0) {
			System.err.println("You must provide some option such as init or generate.");
			System.exit(0);
		}

		OptionParser parser = new OptionParser() {
			{
				acceptsAll(asList("h", "?"), "show help").forHelp();
				acceptsAll(asList("p", "package"), "package for migration classes (optional & only for init)")
						.withOptionalArg().describedAs("com.package").ofType(String.class);
				acceptsAll(asList("d", "database"), "database name (required for init)").withRequiredArg()
						.describedAs("database_name").ofType(String.class);
			}
		};

		OptionSet options = parser.parse(args);

		if (options.has("h")) {
			parser.printHelpOn(System.out);
		}

		if (args[0] != null && args[0].equalsIgnoreCase("init")) {

			if (!options.has("d")) {
				System.err.println("Database name is required for init!");
			} else {
				System.out.println("Database name is going to be: " + options.valueOf("d"));

				ManifestFileParser manifestParser = new ManifestFileParser("AndroidManifest.xml");				
				String corePackageName = manifestParser.getPackageName();
				System.out.println("Core package as found in your AnddroidManifest file: " + corePackageName);
				
				String packageName = null;
				if (options.valueOf("p") != null) {
					System.out.println("Package name for generated classes is going to be: " + options.valueOf("p"));
					packageName = options.valueOf("p").toString();
				} else {
					System.out.println("Generated classes will reside in your app's core package as defined in your Manifest.xml file");
					packageName = corePackageName;
				}
				
				System.out.println("Generating migrations.xml....");
				handleXMLResourceFile(options.valueOf("d").toString(), packageName, 1);
				System.out.println("Generating initial migration class (DBVersion1.java)....");
				handleMigrationClassFile(packageName, 1);

				String pckName = packageName;
				String pckPath = pckName.replace(".", File.separator);
				DatabaseHelperClassGenerator dbGenerator = new DatabaseHelperClassGenerator("templates",
						"DatabaseHelper.java.ftl");
				String dbHelperContent = dbGenerator.generate(packageName, corePackageName);
				System.out.println("Generating DatabaseHelper.java....");
				writeFile("src" + File.separator + pckPath + File.separator + "DatabaseHelper.java", dbHelperContent);
				
				//now copy minimal jar file into Android project's libs directory
				String droidMigrateHome = System.getenv("DROID_MIGRATE_HOME");
				System.out.println("DROID_MIGRATE_HOME environment variable is " + droidMigrateHome);
				System.out.println("Copying minimal jar from DROID_MIGRATE_HOME/dist into your project's libs directory...");
				InputStream in = new FileInputStream(new File(droidMigrateHome + "/dist/droid-migrate-min.jar"));
	            OutputStream out = new FileOutputStream(new File("libs/droid-migrate-min.jar"));

	            // Copy the bits from instream to outstream
	            byte[] buf = new byte[1024];
	            int len;
	            while ((len = in.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
	            in.close();
	            out.close();
	            System.out.println("Done!");
			}

		} else if (args[0] != null && args[0].equalsIgnoreCase("generate")) {
			if (args.length >= 2 && args[1] != null && args[1].equalsIgnoreCase("up")) {
				System.out.println("Generating an upgrade migration...");
				MigrationsResourceFileParser migrationsParser = initMigrationsResourceFileParser();
				int nextSequence = migrationsParser.getSequence() + 1;
				System.out.println("Updating your migrations.xml file...");
				handleXMLResourceFile(migrationsParser.getDatabaseName(), migrationsParser.getPackageName(),
						nextSequence);
				System.out.println("Generating DBVersion" + nextSequence + ".java...");
				handleMigrationClassFile(migrationsParser.getPackageName(), nextSequence);
				System.out.println("Done!");
			} else if (args.length >= 2 && args[1] != null && args[1].equalsIgnoreCase("down")) {
				System.out.println("Generating a rollback migration...");
				MigrationsResourceFileParser migrationsParser = initMigrationsResourceFileParser();
				int prevSequence = 1;
				if (migrationsParser.getSequence() > 1) {
					prevSequence = migrationsParser.getSequence() - 1;
				}
				System.out.println("Rolling back your migrations.xml file to indicate database version " + prevSequence);				
				handleXMLResourceFile(migrationsParser.getDatabaseName(), migrationsParser.getPackageName(),
						prevSequence);

				System.out.println("Done!");
			} else {
				System.err.println("Please indicate up or down for the generate command.");
			}
		}else{
			System.err.println("Didn't do anything. Did you specify an option such as init or generate?");
		}
	}

	private static MigrationsResourceFileParser initMigrationsResourceFileParser() throws SAXException, IOException,
			ParserConfigurationException {
		MigrationsResourceFileParser migrationsParser = new MigrationsResourceFileParser("res" + File.separator
				+ "values" + File.separator + "migrations.xml");
		return migrationsParser;
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
